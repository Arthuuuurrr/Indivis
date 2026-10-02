package net.hautecapitale.metiers.command;

import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.creature.CreatureEnums.SpawnOrigin;
import net.hautecapitale.metiers.creature.CreatureProfile;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.entity.CarcassEntity;
import net.hautecapitale.metiers.entity.HcmEntities;
import net.hautecapitale.metiers.hunt.HuntEngine;
import net.hautecapitale.metiers.hunt.OriginMarker;
import net.hautecapitale.metiers.profession.Profession;
import net.hautecapitale.metiers.skin.SkinningEngine;
import net.hautecapitale.metiers.skin.SkinningEngine.Cancel;
import net.hautecapitale.metiers.skin.SkinningEngine.Refusal;
import net.hautecapitale.metiers.skin.SkinningFeedback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

/**
 * Diagnostic serveur des carcasses et du Dépeceur.
 *
 * <p>Un lapin et un loup vanilla, tués pour de vrai par un Chasseur en
 * mémoire, laissent de vraies carcasses ; un second joueur, Dépeceur, les
 * travaille par le vrai moteur — canalisation, verrous, matières, XP —,
 * l'horloge étant avancée à la main pour ne pas attendre.
 *
 * <p>Ce que ce diagnostic ne peut pas voir : le rendu. La reconstruction de
 * la créature se fait sur le client, et seul un client la montre.
 */
final class SkinDiagnostic {

    private static boolean forcedByUs = false;

    private SkinDiagnostic() {
    }

    static List<String> run(MinecraftServer server, List<String> report) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) {
            return report;
        }
        CreatureProfile rabbit = HcmData.CREATURES.get(Identifier.of("minecraft", "rabbit"));
        CreatureProfile wolf = HcmData.CREATURES.get(Identifier.of("minecraft", "wolf"));
        if (rabbit == null || rabbit.skinning().isEmpty() || wolf == null || wolf.skinning().isEmpty()) {
            report.add("--- Carcasses");
            report.add("  FAIL  fiches de lapin ou de loup absentes, ou sans bloc depecage");
            return report;
        }

        BlockPos spawn = world.getSpawnPoint().getPos();
        BlockPos base = new BlockPos(spawn.getX(), Math.min(world.getTopYInclusive() - 12, 240), spawn.getZ()).north(6).east(6);
        ChunkPos chunk = new ChunkPos(base);
        report.add("--- Carcasses — terrain d'essai");
        if (!world.isChunkLoaded(chunk.toLong())) {
            if (!world.getForcedChunks().contains(chunk.toLong())) {
                world.setChunkForced(chunk.x, chunk.z, true);
                forcedByUs = true;
            }
            report.add("  WAIT  chunk d'essai forcé en " + chunk + ", ses entités se chargent : relancez"
                    + " « /metiers diagnostic » dans quelques secondes pour la partie Carcasses");
            return report;
        }
        // Un sol sous les carcasses, pour qu'elles ne tombent pas dans le vide.
        boolean floorFree = true;
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                floorFree &= world.isAir(base.down().add(dx, 0, dz));
            }
        }
        boolean ready = floorFree;
        check(report, "chunk d'essai chargé, entités suivies, sol libre en " + base.toShortString(), () -> ready);
        if (!ready) {
            return report;
        }
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                world.setBlockState(base.down().add(dx, 0, dz), net.minecraft.block.Blocks.BARRIER.getDefaultState());
            }
        }

        ServerPlayerEntity hunter = testPlayer(server, world, "SkinTestHunter");
        ServerPlayerEntity skinner = testPlayer(server, world, "SkinTestA");
        ServerPlayerEntity other = testPlayer(server, world, "SkinTestB");
        for (ServerPlayerEntity player : List.of(hunter, skinner, other)) {
            player.setPosition(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D);
        }
        Box area = new Box(base).expand(10.0D);
        long[] offset = {0L};
        SkinningEngine.setClock(() -> System.currentTimeMillis() + offset[0]);
        String previousTool = MetiersConfig.get().depecage_outil;
        Item knife = Registries.ITEM.getOptionalValue(Identifier.of("farmersdelight", "flint_knife")).orElse(null);
        if (knife == null) {
            // Sans Farmer's Delight, aucun couteau n'existe : on lève l'exigence
            // d'outil le temps de l'essai, et on le dit.
            MetiersConfig.get().depecage_outil = "";
            report.add("      Farmer's Delight absent : aucun couteau ici, l'outil n'est pas exigé pour l'essai");
        }

        try {
            Metiers.learn(hunter, Profession.CHASSEUR);
            scenarioCarcassAppears(world, hunter, base, report);
            scenarioSnapshot(world, base, report);
            scenarioRefusals(world, hunter, skinner, wolf, knife, base, report);
            scenarioSkin(world, hunter, skinner, rabbit, knife, base, offset, report);
            scenarioTwoSkinners(world, hunter, skinner, other, rabbit, knife, base, offset, report);
            scenarioCancel(world, hunter, skinner, knife, base, offset, report);
            scenarioExpiry(world, base, offset, report);
            scenarioLimits(world, base, report);
            scenarioFeedback(rabbit, report);
        } finally {
            SkinningEngine.clearChannels();
            SkinningEngine.setClock(null);
            MetiersConfig.get().depecage_outil = previousTool;
            world.getEntitiesByClass(Entity.class, area, entity -> entity instanceof CarcassEntity
                    || entity instanceof RabbitEntity || entity instanceof WolfEntity
                    || entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity).forEach(Entity::discard);
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    world.setBlockState(base.down().add(dx, 0, dz), net.minecraft.block.Blocks.AIR.getDefaultState());
                }
            }
            for (ServerPlayerEntity player : List.of(hunter, skinner, other)) {
                Metiers.forget(player, Profession.CHASSEUR);
                Metiers.forget(player, Profession.DEPECEUR);
            }
            if (forcedByUs) {
                world.setChunkForced(chunk.x, chunk.z, false);
                forcedByUs = false;
            }
        }
        report.add("      rendu des carcasses (reconstruction, GeckoLib) : côté client, hors de portée d'un diagnostic serveur");
        return report;
    }

    // ------------------------------------------------------------------

    /** Tuer un lapin laisse une carcasse de lapin ; un loup, une carcasse de loup. */
    private static void scenarioCarcassAppears(ServerWorld world, ServerPlayerEntity hunter, BlockPos base, List<String> report) {
        section(report, "Carcasses — une proie tombe");
        RabbitEntity rabbit = spawn(world, EntityType.RABBIT, base, SpawnReason.NATURAL);
        Vec3d where = rabbit.getEntityPos();
        HuntEngine.Outcome outcome = kill(world, rabbit, hunter);
        CarcassEntity carcass = outcome.carcass();
        check(report, "tuer un lapin sauvage : une carcasse apparaît", () -> carcass != null && carcass.isAlive());
        check(report, "c'est une carcasse de lapin", () -> carcass != null
                && Identifier.of("minecraft", "rabbit").equals(carcass.mobType()));
        check(report, "posée là où il est tombé", () -> carcass != null && carcass.getEntityPos().distanceTo(where) < 0.5D);
        check(report, "de la taille du lapin", () -> carcass != null && Math.abs(carcass.mobWidth() - EntityType.RABBIT.getWidth()) < 0.01F);
        check(report, "sa fiche est celle du lapin", () -> carcass != null && carcass.profile() != null && carcass.profile().isSkinnable());
        check(report, "libre : personne ne la dépèce, personne ne l'a prise",
                () -> carcass != null && carcass.channeler() == null && !carcass.isClaimed());
        check(report, "elle expire à l'heure de la fiche (60 s)", () -> carcass != null
                && carcass.expiresAt() - SkinningEngine.now() > 55_000L && carcass.expiresAt() - SkinningEngine.now() <= 60_000L);
        check(report, "la carcasse est visible dans le monde", () -> world.getEntitiesByClass(CarcassEntity.class,
                new Box(base).expand(8.0D), Entity::isAlive).contains(carcass));

        // Par le vrai chemin : de vrais dégâts, l'écouteur de mort, la carcasse.
        RabbitEntity real = spawn(world, EntityType.RABBIT, base, SpawnReason.NATURAL);
        int carcassesBefore = world.getEntitiesByClass(CarcassEntity.class, new Box(base).expand(8.0D), Entity::isAlive).size();
        real.damage(world, world.getDamageSources().playerAttack(hunter), 1000.0F);
        check(report, "par de vrais dégâts et l'écouteur de mort : une carcasse de plus", () -> real.isDead()
                && world.getEntitiesByClass(CarcassEntity.class, new Box(base).expand(8.0D), Entity::isAlive).size() == carcassesBefore + 1);

        WolfEntity wolf = spawn(world, EntityType.WOLF, base, SpawnReason.NATURAL);
        CarcassEntity wolfCarcass = kill(world, wolf, hunter).carcass();
        check(report, "tuer un loup : une carcasse de loup", () -> wolfCarcass != null
                && Identifier.of("minecraft", "wolf").equals(wolfCarcass.mobType()));

        RabbitEntity mother = spawn(world, EntityType.RABBIT, base, SpawnReason.NATURAL);
        RabbitEntity father = spawn(world, EntityType.RABBIT, base, SpawnReason.NATURAL);
        mother.breed(world, father);
        List<RabbitEntity> babies = world.getEntitiesByClass(RabbitEntity.class, new Box(base).expand(8.0D), RabbitEntity::isBaby);
        check(report, "un petit d'élevage tué ne laisse pas de carcasse (même règle d'origine que le butin)", () -> {
            if (babies.isEmpty()) {
                return false;
            }
            HuntEngine.Outcome bred = kill(world, babies.get(0), hunter);
            return bred.origin() == SpawnOrigin.ELEVAGE && bred.carcass() == null;
        });
        mother.discard();
        father.discard();
        check(report, "l'entité carcasse ne se sauvegarde jamais : au redémarrage, plus rien, rien de dupliqué",
                () -> carcass != null && !carcass.shouldSave() && !HcmEntities.CARCASS.isSaveable());
        if (carcass != null) {
            carcass.discard();
        }
        if (wolfCarcass != null) {
            wolfCarcass.discard();
        }
    }

    /** L'instantané : ce que le client recevra pour reconstruire la créature. */
    private static void scenarioSnapshot(ServerWorld world, BlockPos base, List<String> report) {
        section(report, "Carcasses — l'instantané pour le rendu");
        CarcassEntity carcass = CarcassEntity.create(world, EntityType.WOLF, Vec3d.ofBottomCenter(base), 60);
        check(report, "une carcasse posée par commande existe", () -> carcass != null && carcass.isAlive());
        check(report, "elle transporte le type de la créature", () -> carcass != null
                && Identifier.of("minecraft", "wolf").equals(carcass.mobType()));
        check(report, "et ses données, en SNBT lisible", () -> {
            if (carcass == null || carcass.mobData().isEmpty()) {
                return false;
            }
            NbtCompound nbt = StringNbtReader.readCompound(carcass.mobData());
            return nbt.contains("Health") && !nbt.contains("Passengers") && !nbt.contains("Brain") && !nbt.contains("UUID");
        });
        check(report, "dimensions du loup transportées", () -> carcass != null
                && Math.abs(carcass.mobHeight() - EntityType.WOLF.getHeight()) < 0.01F);
        check(report, "sa boîte est couchée : plus longue que haute", () -> carcass != null
                && carcass.getBoundingBox().getLengthX() > carcass.getBoundingBox().getLengthY());
        if (carcass != null) {
            carcass.discard();
        }
    }

    /** Sans métier, sans niveau, sans couteau, trop loin : refus, et la carcasse ne bouge pas. */
    private static void scenarioRefusals(ServerWorld world, ServerPlayerEntity hunter, ServerPlayerEntity skinner,
                                         CreatureProfile wolf, Item knife, BlockPos base, List<String> report) {
        section(report, "Carcasses — refus");
        WolfEntity prey = spawn(world, EntityType.WOLF, base, SpawnReason.NATURAL);
        CarcassEntity carcass = kill(world, prey, hunter).carcass();
        if (carcass == null) {
            report.add("  FAIL  pas de carcasse de loup à refuser");
            return;
        }
        check(report, "sans le métier : refus", () -> SkinningEngine.check(skinner, carcass) == Refusal.METIER_NON_APPRIS);
        Metiers.learn(skinner, Profession.DEPECEUR);
        check(report, "Dépeceur niveau 1 sur un loup (niveau " + wolf.skinning().orElseThrow().level() + ") : refus",
                () -> SkinningEngine.check(skinner, carcass) == Refusal.NIVEAU_INSUFFISANT);
        Metiers.setLevel(skinner, Profession.DEPECEUR, 20);
        skinner.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        check(report, "au niveau, sans le couteau exigé par la fiche (compagnon) : refus",
                () -> SkinningEngine.check(skinner, carcass) == Refusal.MAUVAIS_OUTIL);
        Identifier ironKnife = Identifier.of("farmersdelight", "iron_knife");
        if (Registries.ITEM.containsId(ironKnife)) {
            skinner.setStackInHand(Hand.MAIN_HAND, new ItemStack(Registries.ITEM.get(ironKnife)));
            check(report, "avec un couteau en fer : accepté", () -> SkinningEngine.check(skinner, carcass) == null);
            skinner.setPosition(base.getX() + 0.5D, base.getY(), base.getZ() + 8.5D);
            check(report, "à huit blocs : trop loin", () -> SkinningEngine.check(skinner, carcass) == Refusal.TROP_LOIN);
            skinner.setPosition(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D);
        } else {
            report.add("      (couteau en fer absent : acceptation et portée testées sur le lapin)");
        }
        check(report, "la carcasse est toujours là, intacte", () -> carcass.isAlive() && !carcass.isClaimed()
                && carcass.channeler() == null);
        check(report, "le joueur n'a rien reçu", () -> skinner.getInventory().isEmpty()
                || (skinner.getInventory().count(Items.RABBIT_HIDE) == 0 && count(skinner, Identifier.of("haute_capitale_metiers", "fourrure_commune")) == 0));
        carcass.discard();
        Metiers.setLevel(skinner, Profession.DEPECEUR, 1);
        skinner.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
    }

    /** Le Chasseur tue, le Dépeceur dépèce : matière, XP, et la carcasse disparaît. */
    private static void scenarioSkin(ServerWorld world, ServerPlayerEntity hunter, ServerPlayerEntity skinner,
                                     CreatureProfile rabbit, Item knife, BlockPos base, long[] offset, List<String> report) {
        section(report, "Carcasses — dépecer");
        CreatureProfile.SkinningEntry entry = rabbit.skinning().orElseThrow();
        RabbitEntity prey = spawn(world, EntityType.RABBIT, base, SpawnReason.NATURAL);
        CarcassEntity carcass = kill(world, prey, hunter).carcass();
        if (carcass == null) {
            report.add("  FAIL  pas de carcasse de lapin à dépecer");
            return;
        }
        if (knife != null) {
            skinner.setStackInHand(Hand.MAIN_HAND, new ItemStack(knife));
        }
        clear(skinner);
        if (knife != null) {
            skinner.setStackInHand(Hand.MAIN_HAND, new ItemStack(knife));
        }
        double xpBefore = Metiers.getXp(skinner, Profession.DEPECEUR);
        skinner.setPosition(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D);
        check(report, "le Dépeceur commence (clic droit) : la carcasse lui est réservée",
                () -> SkinningEngine.start(skinner, carcass) && skinner.getUuid().equals(carcass.channeler())
                        && SkinningEngine.channelOf(skinner) != null);
        check(report, "un second clic ne relance pas la canalisation",
                () -> SkinningEngine.start(skinner, carcass) && SkinningEngine.channelOf(skinner).startedAt == SkinningEngine.channelOf(skinner).startedAt);
        offset[0] += entry.channelMillis() / 2;
        SkinningEngine.tick(world.getServer());
        check(report, "à mi-parcours : toujours en cours, rien reçu", () -> SkinningEngine.channelOf(skinner) != null
                && carcass.isAlive() && !carcass.isClaimed() && skinner.getInventory().count(Items.RABBIT_HIDE) == 0);
        offset[0] += entry.channelMillis() / 2 + 100L;
        SkinningEngine.tick(world.getServer());
        check(report, "au bout de " + entry.channelSeconds() + " s : dépecé, la carcasse a disparu",
                () -> SkinningEngine.channelOf(skinner) == null && carcass.isRemoved() && carcass.isClaimed());
        check(report, "la peau de lapin est dans l'inventaire", () -> skinner.getInventory().count(Items.RABBIT_HIDE) == 1);
        check(report, "XP du Dépeceur : +" + (int) entry.xp(),
                () -> Metiers.getXp(skinner, Profession.DEPECEUR) == xpBefore + entry.xp());
        check(report, "le Chasseur, lui, n'a rien reçu du dépeçage", () -> !Metiers.hasProfession(hunter, Profession.DEPECEUR)
                && hunter.getInventory().count(Items.RABBIT_HIDE) == 0);

        Metiers.setLevel(skinner, Profession.DEPECEUR, 30);
        RabbitEntity easy = spawn(world, EntityType.RABBIT, base, SpawnReason.NATURAL);
        CarcassEntity easyCarcass = kill(world, easy, hunter).carcass();
        double before = Metiers.getXp(skinner, Profession.DEPECEUR);
        SkinningEngine.start(skinner, easyCarcass);
        offset[0] += entry.channelMillis() + 100L;
        SkinningEngine.tick(world.getServer());
        check(report, "Dépeceur 30 sur un lapin : la peau, mais 0 XP (décote)",
                () -> easyCarcass.isRemoved() && skinner.getInventory().count(Items.RABBIT_HIDE) == 2
                        && Metiers.getXp(skinner, Profession.DEPECEUR) == before);
        Metiers.setLevel(skinner, Profession.DEPECEUR, 1);
        clear(skinner);
    }

    /** Deux Dépeceurs sur la même carcasse : le premier la réserve, un seul obtient tout. */
    private static void scenarioTwoSkinners(ServerWorld world, ServerPlayerEntity hunter, ServerPlayerEntity skinner,
                                            ServerPlayerEntity other, CreatureProfile rabbit, Item knife, BlockPos base,
                                            long[] offset, List<String> report) {
        section(report, "Carcasses — deux Dépeceurs cliquent ensemble");
        CreatureProfile.SkinningEntry entry = rabbit.skinning().orElseThrow();
        Metiers.learn(other, Profession.DEPECEUR);
        for (ServerPlayerEntity player : List.of(skinner, other)) {
            clear(player);
            if (knife != null) {
                player.setStackInHand(Hand.MAIN_HAND, new ItemStack(knife));
            }
            player.setPosition(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D);
        }
        RabbitEntity prey = spawn(world, EntityType.RABBIT, base, SpawnReason.NATURAL);
        CarcassEntity carcass = kill(world, prey, hunter).carcass();
        check(report, "A commence", () -> SkinningEngine.start(skinner, carcass));
        check(report, "B, au même instant : « déjà en cours »", () -> SkinningEngine.check(other, carcass) == Refusal.DEJA_EN_COURS
                && !SkinningEngine.start(other, carcass) && SkinningEngine.channelOf(other) == null);
        offset[0] += entry.channelMillis() + 100L;
        SkinningEngine.tick(world.getServer());
        check(report, "A obtient tout, B rien", () -> skinner.getInventory().count(Items.RABBIT_HIDE) == 1
                && other.getInventory().count(Items.RABBIT_HIDE) == 0 && carcass.isRemoved());

        check(report, "le verrou de réclamation ne cède qu'une fois", () -> {
            CarcassEntity fresh = CarcassEntity.create(world, EntityType.RABBIT, Vec3d.ofBottomCenter(base), 60);
            boolean first = fresh.claim(skinner.getUuid());
            boolean second = fresh.claim(other.getUuid());
            fresh.discard();
            return first && !second;
        });

        // A commence puis s'en va : B peut alors s'y mettre.
        RabbitEntity prey2 = spawn(world, EntityType.RABBIT, base, SpawnReason.NATURAL);
        CarcassEntity carcass2 = kill(world, prey2, hunter).carcass();
        SkinningEngine.start(skinner, carcass2);
        skinner.setPosition(base.getX() + 0.5D, base.getY(), base.getZ() + 6.5D);
        SkinningEngine.tick(world.getServer());
        check(report, "A s'éloigne : sa canalisation s'annule, la carcasse est libérée",
                () -> SkinningEngine.channelOf(skinner) == null && carcass2.channeler() == null && carcass2.isAlive());
        check(report, "B peut alors commencer", () -> SkinningEngine.start(other, carcass2));
        offset[0] += entry.channelMillis() + 100L;
        SkinningEngine.tick(world.getServer());
        check(report, "et c'est B qui l'obtient", () -> other.getInventory().count(Items.RABBIT_HIDE) == 1 && carcass2.isRemoved());
        skinner.setPosition(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D);
        clear(skinner);
        clear(other);
    }

    /** Bouger, changer d'outil, perdre la carcasse : tout annule, sans rien donner. */
    private static void scenarioCancel(ServerWorld world, ServerPlayerEntity hunter, ServerPlayerEntity skinner, Item knife,
                                       BlockPos base, long[] offset, List<String> report) {
        section(report, "Carcasses — interruptions");
        if (knife != null) {
            skinner.setStackInHand(Hand.MAIN_HAND, new ItemStack(knife));
        }
        skinner.setPosition(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D);

        RabbitEntity prey = spawn(world, EntityType.RABBIT, base, SpawnReason.NATURAL);
        CarcassEntity carcass = kill(world, prey, hunter).carcass();
        SkinningEngine.start(skinner, carcass);
        skinner.setPosition(base.getX() + 1.5D, base.getY(), base.getZ() + 0.5D);
        SkinningEngine.tick(world.getServer());
        check(report, "un pas de côté (1 bloc) : interrompu, rien reçu", () -> SkinningEngine.channelOf(skinner) == null
                && carcass.isAlive() && skinner.getInventory().count(Items.RABBIT_HIDE) == 0);
        skinner.setPosition(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D);

        SkinningEngine.start(skinner, carcass);
        skinner.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.STICK));
        SkinningEngine.tick(world.getServer());
        check(report, "changer d'objet en main : interrompu", () -> SkinningEngine.channelOf(skinner) == null && carcass.isAlive());
        skinner.setStackInHand(Hand.MAIN_HAND, knife != null ? new ItemStack(knife) : ItemStack.EMPTY);

        SkinningEngine.start(skinner, carcass);
        carcass.discard();
        SkinningEngine.tick(world.getServer());
        check(report, "la carcasse disparaît sous ses mains : interrompu", () -> SkinningEngine.channelOf(skinner) == null);

        RabbitEntity prey2 = spawn(world, EntityType.RABBIT, base, SpawnReason.NATURAL);
        CarcassEntity carcass2 = kill(world, prey2, hunter).carcass();
        SkinningEngine.start(skinner, carcass2);
        offset[0] += 1_000L;
        SkinningEngine.tick(world.getServer());
        check(report, "tenir en place et garder l'outil : la canalisation continue", () -> SkinningEngine.channelOf(skinner) != null);
        SkinningEngine.clearChannels();
        check(report, "annulée par l'administration : la carcasse est libérée", () -> carcass2.channeler() == null && carcass2.isAlive());
        carcass2.discard();
        clear(skinner);
    }

    /** Une carcasse expire à l'heure de sa fiche, et une canalisation en cours s'arrête avec elle. */
    private static void scenarioExpiry(ServerWorld world, BlockPos base, long[] offset, List<String> report) {
        section(report, "Carcasses — expiration");
        CarcassEntity carcass = CarcassEntity.create(world, EntityType.RABBIT, Vec3d.ofBottomCenter(base), 60);
        carcass.tick();
        check(report, "avant l'heure : toujours là", () -> carcass.isAlive());
        offset[0] += 59_000L;
        carcass.tick();
        check(report, "à 59 s : toujours là", () -> carcass.isAlive());
        offset[0] += 2_000L;
        carcass.tick();
        check(report, "à 61 s : disparue", () -> carcass.isRemoved());
    }

    /** Une zone ne s'encombre pas : au-delà du plafond, la plus ancienne s'en va. */
    private static void scenarioLimits(ServerWorld world, BlockPos base, List<String> report) {
        section(report, "Carcasses — plafond par zone");
        int max = MetiersConfig.get().carcasses_max_par_zone;
        CarcassEntity oldest = CarcassEntity.create(world, EntityType.RABBIT, Vec3d.ofBottomCenter(base), 60);
        for (int i = 0; i < max; i++) {
            CarcassEntity.create(world, EntityType.RABBIT, Vec3d.ofBottomCenter(base.east(i % 3)), 60);
        }
        List<CarcassEntity> alive = world.getEntitiesByClass(CarcassEntity.class, new Box(base).expand(8.0D), Entity::isAlive);
        check(report, "au plus " + max + " carcasses dans la zone", () -> alive.size() <= max);
        check(report, "la plus ancienne a cédé sa place", () -> oldest.isRemoved());
        alive.forEach(Entity::discard);
    }

    private static void scenarioFeedback(CreatureProfile rabbit, List<String> report) {
        section(report, "Carcasses — messages");
        check(report, "un refus a un message", () -> SkinningFeedback.refusal(Refusal.NIVEAU_INSUFFISANT, null).getString().contains("1"));
        check(report, "une interruption a un message", () -> !SkinningFeedback.cancel(Cancel.A_BOUGE).getString().isEmpty());
        SkinningEngine.Outcome outcome = new SkinningEngine.Outcome(rabbit.skinning().orElseThrow(), 5.0D, 0,
                List.of(new ItemStack(Items.RABBIT_HIDE)));
        check(report, "un dépeçage réussi énumère la matière et l'XP", () -> {
            String text = SkinningFeedback.describe(outcome).getString();
            return text.contains("+5 XP") && text.contains("×1");
        });
    }

    // ------------------------------------------------------------------

    private static <T extends Entity> T spawn(ServerWorld world, EntityType<T> type, BlockPos base, SpawnReason reason) {
        T entity = type.spawn(world, base, reason);
        if (entity == null) {
            throw new IllegalStateException("apparition impossible : " + type);
        }
        return entity;
    }

    private static HuntEngine.Outcome kill(ServerWorld world, LivingEntity prey, ServerPlayerEntity killer) {
        CreatureProfile profile = HuntEngine.profileOf(prey);
        return HuntEngine.kill(world, prey, killer, profile, OriginMarker.resolve(prey));
    }

    private static void clear(ServerPlayerEntity player) {
        player.getInventory().getMainStacks().replaceAll(stack -> ItemStack.EMPTY);
    }

    private static int count(ServerPlayerEntity player, Identifier item) {
        return Registries.ITEM.containsId(item) ? player.getInventory().count(Registries.ITEM.get(item)) : 0;
    }

    private static ServerPlayerEntity testPlayer(MinecraftServer server, ServerWorld world, String name) {
        // Un FakePlayer : d'autres mods du pack envoient des paquets au joueur qui frappe, mange ou
        // réapparaît (zones musicales, attributs, accessoires) — un joueur en mémoire ordinaire ne peut pas les recevoir.
        return DiagnosticPlayer.create(world, name);
    }

    private static void section(List<String> report, String title) {
        report.add("--- " + title);
    }

    private static void check(List<String> report, String label, Check condition) {
        boolean ok;
        try {
            ok = condition.test();
        } catch (Exception e) {
            report.add("  FAIL  " + label + "  (" + e + ")");
            return;
        }
        report.add((ok ? "  PASS  " : "  FAIL  ") + label);
    }

    @FunctionalInterface
    private interface Check {
        boolean test() throws Exception;
    }
}
