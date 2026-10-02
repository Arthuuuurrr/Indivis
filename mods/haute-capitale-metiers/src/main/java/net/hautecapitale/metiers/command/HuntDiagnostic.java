package net.hautecapitale.metiers.command;

import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.creature.CreatureEnums.Category;
import net.hautecapitale.metiers.creature.CreatureEnums.Rarity;
import net.hautecapitale.metiers.creature.CreatureEnums.SpawnOrigin;
import net.hautecapitale.metiers.creature.CreatureProfile;
import net.hautecapitale.metiers.creature.DropEntry;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.hunt.HuntEngine;
import net.hautecapitale.metiers.hunt.HuntEngine.Outcome;
import net.hautecapitale.metiers.hunt.HuntEngine.Verdict;
import net.hautecapitale.metiers.hunt.HuntFeedback;
import net.hautecapitale.metiers.hunt.OriginAttachment;
import net.hautecapitale.metiers.hunt.OriginMarker;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Diagnostic serveur du Chasseur et du marquage d'origine.
 *
 * <p>Le lapin vanilla sert de proie : sa fiche est livrée, niveau 1, et il
 * existe sur toute installation. Chaque scénario fait apparaître de vrais
 * lapins par les vrais chemins de Minecraft — apparition naturelle, générateur,
 * œuf, commande, reproduction — puis les abat par de vrais dégâts, pour que
 * ce soit le mixin, l'écouteur de chargement et l'écouteur de mort qui
 * travaillent, pas une simulation.
 */
final class HuntDiagnostic {

    private static final Identifier RABBIT = Identifier.of("minecraft", "rabbit");

    /** Le chunk d'essai a-t-il été forcé par un passage précédent, à déforcer une fois fini ? */
    private static boolean forcedByUs = false;

    private HuntDiagnostic() {
    }

    static List<String> run(MinecraftServer server, List<String> report) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) {
            return report;
        }
        CreatureProfile rabbit = HcmData.CREATURES.get(RABBIT);
        if (rabbit == null || rabbit.hunter().isEmpty()) {
            report.add("--- Chasse");
            report.add("  FAIL  fiche de lapin absente ou sans bloc chasseur : " + RABBIT);
            return report;
        }

        BlockPos spawn = world.getSpawnPoint().getPos();
        BlockPos base = new BlockPos(spawn.getX(), Math.min(world.getTopYInclusive() - 12, 240), spawn.getZ()).north(6);
        ChunkPos chunk = new ChunkPos(base);
        report.add("--- Chasse — terrain d'essai");
        // Les entités d'un chunk se chargent à part, de façon asynchrone, et ne
        // sont « suivies » — donc visibles aux événements et aux recherches —
        // qu'une fois ce chargement fini. Sans joueur, aucun chunk n'en est là.
        // On force celui d'essai, comme /forceload, et on demande de revenir.
        if (!world.isChunkLoaded(chunk.toLong())) {
            if (!world.getForcedChunks().contains(chunk.toLong())) {
                world.setChunkForced(chunk.x, chunk.z, true);
                forcedByUs = true;
            }
            report.add("  WAIT  chunk d'essai forcé en " + chunk + ", ses entités se chargent : relancez"
                    + " « /metiers diagnostic » dans quelques secondes pour la partie Chasse");
            return report;
        }
        check(report, "chunk d'essai chargé, entités suivies, en " + base.toShortString(), () -> true);

        ServerPlayerEntity hunter = testPlayer(server, world, "HuntTestA");
        ServerPlayerEntity other = testPlayer(server, world, "HuntTestB");
        hunter.setPosition(Vec3d.ofCenter(base));
        other.setPosition(Vec3d.ofCenter(base));
        Box area = new Box(base).expand(8.0D);

        try {
            scenarioMarking(world, base, report);
            scenarioPersistence(world, base, report);
            scenarioHunt(world, hunter, rabbit, base, report);
            scenarioAntiFarm(world, hunter, rabbit, base, report);
            scenarioCredit(world, hunter, other, rabbit, base, report);
            scenarioLootAndLevels(world, hunter, base, report);
            scenarioFeedback(rabbit, report);
        } finally {
            world.getEntitiesByClass(Entity.class, area, entity ->
                    entity instanceof RabbitEntity || entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity
                            || entity instanceof net.hautecapitale.metiers.entity.CarcassEntity)
                    .forEach(Entity::discard);
            Metiers.forget(hunter, Profession.CHASSEUR);
            Metiers.forget(other, Profession.CHASSEUR);
            if (forcedByUs) {
                world.setChunkForced(chunk.x, chunk.z, false);
                forcedByUs = false;
            }
        }
        return report;
    }

    // ------------------------------------------------------------------

    /** Chaque chemin d'apparition de Minecraft laisse la bonne marque. */
    private static void scenarioMarking(ServerWorld world, BlockPos base, List<String> report) {
        section(report, "Chasse — marquage de l'origine");
        RabbitEntity natural = spawnRabbit(world, base, SpawnReason.NATURAL);
        check(report, "apparition naturelle → naturelle", () -> OriginMarker.resolve(natural) == SpawnOrigin.NATURELLE);
        check(report, "la marque est bien attachée à l'entité (mixin + attachement)", () -> OriginAttachment.isMarked(natural));

        RabbitEntity spawner = spawnRabbit(world, base, SpawnReason.SPAWNER);
        check(report, "générateur → spawner", () -> OriginMarker.resolve(spawner) == SpawnOrigin.SPAWNER);
        RabbitEntity egg = spawnRabbit(world, base, SpawnReason.SPAWN_ITEM_USE);
        check(report, "œuf d'apparition → oeuf", () -> OriginMarker.resolve(egg) == SpawnOrigin.OEUF);
        RabbitEntity command = spawnRabbit(world, base, SpawnReason.COMMAND);
        check(report, "commande → commande", () -> OriginMarker.resolve(command) == SpawnOrigin.COMMANDE);

        // Reproduction : le vrai chemin, AnimalEntity.breed, qui n'initialise pas le petit.
        RabbitEntity mother = spawnRabbit(world, base, SpawnReason.NATURAL);
        RabbitEntity father = spawnRabbit(world, base, SpawnReason.NATURAL);
        int before = world.getEntitiesByClass(RabbitEntity.class, new Box(base).expand(8.0D), Entity::isAlive).size();
        mother.breed(world, father);
        List<RabbitEntity> babies = world.getEntitiesByClass(RabbitEntity.class, new Box(base).expand(8.0D), RabbitEntity::isBaby);
        check(report, "la reproduction fait naître un petit", () -> babies.size() == 1
                && world.getEntitiesByClass(RabbitEntity.class, new Box(base).expand(8.0D), Entity::isAlive).size() == before + 1);
        check(report, "le petit est marqué élevage (déduit au chargement : jamais initialisé)",
                () -> !babies.isEmpty() && OriginMarker.resolve(babies.get(0)) == SpawnOrigin.ELEVAGE);
        check(report, "ses parents restent naturels",
                () -> OriginMarker.resolve(mother) == SpawnOrigin.NATURELLE && OriginMarker.resolve(father) == SpawnOrigin.NATURELLE);

        // Étiquette de commande : le contrat offert aux systèmes du MMO.
        RabbitEntity tagged = spawnRabbit(world, base, SpawnReason.COMMAND);
        tagged.addCommandTag(OriginMarker.TAG_PREFIX + "spawn_mmo");
        check(report, "une étiquette hcm_origine_spawn_mmo l'emporte sur la marque « commande »",
                () -> OriginMarker.resolve(tagged) == SpawnOrigin.SPAWN_MMO && OriginAttachment.of(tagged) == SpawnOrigin.COMMANDE);
        tagged.addCommandTag(OriginMarker.TAG_PREFIX + "dragon");
        check(report, "une étiquette mal écrite est ignorée", () -> OriginMarker.resolve(tagged) == SpawnOrigin.SPAWN_MMO);

        // Une entité jamais initialisée — la faune d'avant le mod, ou un mod
        // qui construit ses entités sans passer par Minecraft.
        RabbitEntity legacy = EntityType.RABBIT.create(world, SpawnReason.LOAD);
        legacy.refreshPositionAndAngles(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D, 0.0F, 0.0F);
        check(report, "avant d'entrer dans le monde : aucune marque", () -> !OriginAttachment.isMarked(legacy));
        world.spawnEntity(legacy);
        check(report, "adulte sans marque à l'entrée dans le monde → origine par défaut (naturelle)",
                () -> OriginMarker.resolve(legacy) == SpawnOrigin.NATURELLE);
        RabbitEntity legacyBaby = EntityType.RABBIT.create(world, SpawnReason.LOAD);
        legacyBaby.setBaby(true);
        legacyBaby.refreshPositionAndAngles(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D, 0.0F, 0.0F);
        world.spawnEntity(legacyBaby);
        check(report, "petit sans marque à l'entrée dans le monde → élevage",
                () -> OriginMarker.resolve(legacyBaby) == SpawnOrigin.ELEVAGE);

        // Un mob de générateur qui se recharge reste un mob de générateur.
        OriginMarker.onInitialize(spawner, SpawnReason.NATURAL);
        check(report, "une marque existante n'est jamais réécrite par une initialisation",
                () -> OriginMarker.resolve(spawner) == SpawnOrigin.SPAWNER);

        for (RabbitEntity entity : List.of(natural, spawner, egg, command, mother, father, tagged, legacy, legacyBaby)) {
            entity.discard();
        }
        babies.forEach(Entity::discard);
    }

    /** La marque survit à l'écriture et la relecture de l'entité — chunk déchargé, redémarrage. */
    private static void scenarioPersistence(ServerWorld world, BlockPos base, List<String> report) {
        section(report, "Chasse — la marque survit à la sauvegarde");
        RabbitEntity spawner = spawnRabbit(world, base, SpawnReason.SPAWNER);
        NbtCompound saved;
        try {
            NbtWriteView write = NbtWriteView.create(ErrorReporter.EMPTY, world.getRegistryManager());
            spawner.writeData(write);
            saved = write.getNbt();
        } catch (Exception e) {
            report.add("  FAIL  écriture de l'entité : " + e);
            spawner.discard();
            return;
        }
        check(report, "l'origine figure dans le NBT de l'entité", () -> saved.toString().contains("haute_capitale_metiers:origine"));

        RabbitEntity reloaded = EntityType.RABBIT.create(world, SpawnReason.LOAD);
        try {
            reloaded.readData(NbtReadView.create(ErrorReporter.EMPTY, world.getRegistryManager(), saved));
        } catch (Exception e) {
            report.add("  FAIL  relecture de l'entité : " + e);
            spawner.discard();
            return;
        }
        check(report, "relue : toujours un mob de générateur", () -> OriginMarker.resolve(reloaded) == SpawnOrigin.SPAWNER);
        reloaded.refreshPositionAndAngles(base.getX() + 0.5D, base.getY(), base.getZ() + 0.5D, 0.0F, 0.0F);
        reloaded.setUuid(UUID.randomUUID());
        world.spawnEntity(reloaded);
        check(report, "et l'entrée dans le monde ne la transforme pas en naturelle",
                () -> OriginMarker.resolve(reloaded) == SpawnOrigin.SPAWNER);
        spawner.discard();
        reloaded.discard();
    }

    /** Tuer une proie sauvage donne l'XP de la fiche ; sans le métier, rien. */
    private static void scenarioHunt(ServerWorld world, ServerPlayerEntity hunter, CreatureProfile rabbit,
                                     BlockPos base, List<String> report) {
        section(report, "Chasse — une proie sauvage");
        double xpRabbit = rabbit.hunter().orElseThrow().xp();

        RabbitEntity first = spawnRabbit(world, base, SpawnReason.NATURAL);
        check(report, "sans le métier : la proie meurt, aucune XP nulle part", () -> {
            slay(world, first, hunter);
            return first.isDead() && Metiers.getXp(hunter, Profession.CHASSEUR) == 0.0D
                    && !Metiers.hasProfession(hunter, Profession.CHASSEUR);
        });

        Metiers.learn(hunter, Profession.CHASSEUR);
        RabbitEntity second = spawnRabbit(world, base, SpawnReason.NATURAL);
        double before = Metiers.getXp(hunter, Profession.CHASSEUR);
        slay(world, second, hunter);
        check(report, "Chasseur niveau 1, lapin sauvage : +" + (int) xpRabbit + " XP (par l'écouteur de mort)",
                () -> second.isDead() && Metiers.getXp(hunter, Profession.CHASSEUR) == before + xpRabbit);

        RabbitEntity third = spawnRabbit(world, base, SpawnReason.NATURAL);
        third.addCommandTag(OriginMarker.TAG_PREFIX + "spawn_mmo");
        double beforeThird = Metiers.getXp(hunter, Profession.CHASSEUR);
        slay(world, third, hunter);
        check(report, "proie étiquetée spawn_mmo : XP aussi",
                () -> Metiers.getXp(hunter, Profession.CHASSEUR) == beforeThird + xpRabbit);

        Metiers.setLevel(hunter, Profession.CHASSEUR, 40);
        RabbitEntity fourth = spawnRabbit(world, base, SpawnReason.NATURAL);
        double beforeFourth = Metiers.getXp(hunter, Profession.CHASSEUR);
        slay(world, fourth, hunter);
        check(report, "Chasseur 40 sur une proie de niveau 1 : XP nulle (décote), mais la proie meurt",
                () -> fourth.isDead() && Metiers.getXp(hunter, Profession.CHASSEUR) == beforeFourth);
        Metiers.setLevel(hunter, Profession.CHASSEUR, 1);
    }

    /** Élevage, œuf, générateur, commande : rien. */
    private static void scenarioAntiFarm(ServerWorld world, ServerPlayerEntity hunter, CreatureProfile rabbit,
                                         BlockPos base, List<String> report) {
        section(report, "Chasse — anti-farm");
        double xpRabbit = rabbit.hunter().orElseThrow().xp();

        RabbitEntity mother = spawnRabbit(world, base, SpawnReason.NATURAL);
        RabbitEntity father = spawnRabbit(world, base, SpawnReason.NATURAL);
        mother.breed(world, father);
        List<RabbitEntity> babies = world.getEntitiesByClass(RabbitEntity.class, new Box(base).expand(8.0D), RabbitEntity::isBaby);
        double before = Metiers.getXp(hunter, Profession.CHASSEUR);
        check(report, "faire naître un petit et le tuer : aucune XP", () -> {
            if (babies.isEmpty()) {
                return false;
            }
            slay(world, babies.get(0), hunter);
            return babies.get(0).isDead() && Metiers.getXp(hunter, Profession.CHASSEUR) == before;
        });
        check(report, "mais tuer un de ses parents sauvages en donne", () -> {
            slay(world, mother, hunter);
            return Metiers.getXp(hunter, Profession.CHASSEUR) == before + xpRabbit;
        });
        father.discard();

        for (SpawnReason reason : List.of(SpawnReason.SPAWN_ITEM_USE, SpawnReason.COMMAND, SpawnReason.SPAWNER)) {
            RabbitEntity farmed = spawnRabbit(world, base, reason);
            double start = Metiers.getXp(hunter, Profession.CHASSEUR);
            slay(world, farmed, hunter);
            check(report, "proie de " + reason.name().toLowerCase(java.util.Locale.ROOT) + " : aucune XP",
                    () -> farmed.isDead() && Metiers.getXp(hunter, Profession.CHASSEUR) == start);
        }
    }

    /** Un seul crédité : le coup fatal, ou à défaut le dernier joueur à avoir frappé. */
    private static void scenarioCredit(ServerWorld world, ServerPlayerEntity hunter, ServerPlayerEntity other,
                                       CreatureProfile rabbit, BlockPos base, List<String> report) {
        section(report, "Chasse — deux joueurs sur la même proie");
        double xpRabbit = rabbit.hunter().orElseThrow().xp();
        Metiers.learn(other, Profession.CHASSEUR);

        RabbitEntity prey = spawnRabbit(world, base, SpawnReason.NATURAL);
        double a = Metiers.getXp(hunter, Profession.CHASSEUR);
        double b = Metiers.getXp(other, Profession.CHASSEUR);
        prey.damage(world, world.getDamageSources().playerAttack(hunter), 0.5F);
        check(report, "A frappe : la proie vit encore", () -> !prey.isDead());
        slay(world, prey, other);
        check(report, "B porte le coup fatal : B crédité, A non",
                () -> Metiers.getXp(other, Profession.CHASSEUR) == b + xpRabbit
                        && Metiers.getXp(hunter, Profession.CHASSEUR) == a);

        RabbitEntity fallen = spawnRabbit(world, base, SpawnReason.NATURAL);
        double a2 = Metiers.getXp(hunter, Profession.CHASSEUR);
        fallen.damage(world, world.getDamageSources().playerAttack(hunter), 0.5F);
        fallen.damage(world, world.getDamageSources().fall(), 1000.0F);
        check(report, "frappée par A puis tuée par une chute : A crédité (dernier assaillant)",
                () -> fallen.isDead() && Metiers.getXp(hunter, Profession.CHASSEUR) == a2 + xpRabbit);

        RabbitEntity alone = spawnRabbit(world, base, SpawnReason.NATURAL);
        double a3 = Metiers.getXp(hunter, Profession.CHASSEUR);
        double b3 = Metiers.getXp(other, Profession.CHASSEUR);
        alone.damage(world, world.getDamageSources().generic(), 1000.0F);
        check(report, "morte sans joueur : personne n'est crédité",
                () -> alone.isDead() && Metiers.getXp(hunter, Profession.CHASSEUR) == a3
                        && Metiers.getXp(other, Profession.CHASSEUR) == b3);
    }

    /** Butin de combat, viande, niveau requis et passage de niveau — sur une fiche choisie. */
    private static void scenarioLootAndLevels(ServerWorld world, ServerPlayerEntity hunter, BlockPos base, List<String> report) {
        section(report, "Chasse — butin, viande, niveau requis");
        Identifier martin = Identifier.of("capitale_currency", "martin_dor");
        boolean currency = Registries.ITEM.containsId(martin);
        Identifier lootItem = currency ? martin : Identifier.of("minecraft", "emerald");
        CreatureProfile orcLike = new CreatureProfile(Category.ORC, Rarity.RARE,
                Optional.of(new CreatureProfile.HunterEntry(10, 50.0D)),
                Optional.empty(),
                Optional.of(new DropEntry(Identifier.of("minecraft", "beef"), 2, 2, 1.0D)),
                List.of(new DropEntry(lootItem, 3, 3, 1.0D)),
                List.of(SpawnOrigin.NATURELLE, SpawnOrigin.SPAWN_MMO),
                Optional.empty());
        report.add("      butin d'essai : " + lootItem + (currency ? " (la monnaie du serveur)" : " (monnaie absente ici)"));

        Metiers.setLevel(hunter, Profession.CHASSEUR, 1);
        RabbitEntity low = spawnRabbit(world, base, SpawnReason.NATURAL);
        Outcome tooLow = HuntEngine.kill(world, low, hunter, orcLike, SpawnOrigin.NATURELLE);
        check(report, "niveau 1 sur une proie de niveau 10 : niveau insuffisant, aucune XP",
                () -> tooLow.verdict() == Verdict.NIVEAU_INSUFFISANT && tooLow.xpGained() == 0.0D);
        check(report, "mais le butin de combat tombe quand même (3 pièces)",
                () -> tooLow.loot().size() == 1 && tooLow.loot().get(0).getCount() == 3
                        && countDropped(world, base, lootItem) >= 3);
        check(report, "et la viande aussi", () -> tooLow.meat().size() == 1 && tooLow.meat().get(0).getCount() == 2);
        low.discard();

        Metiers.setLevel(hunter, Profession.CHASSEUR, 10);
        double before = Metiers.getXp(hunter, Profession.CHASSEUR);
        RabbitEntity even = spawnRabbit(world, base, SpawnReason.NATURAL);
        Outcome full = HuntEngine.kill(world, even, hunter, orcLike, SpawnOrigin.NATURELLE);
        check(report, "niveau 10 sur niveau 10 : +50 XP, verdict XP",
                () -> full.verdict() == Verdict.XP && full.xpGained() == 50.0D
                        && Metiers.getXp(hunter, Profession.CHASSEUR) == before + 50.0D);
        even.discard();

        RabbitEntity farmed = spawnRabbit(world, base, SpawnReason.NATURAL);
        int droppedBefore = countDropped(world, base, lootItem);
        Outcome elevage = HuntEngine.kill(world, farmed, hunter, orcLike, SpawnOrigin.ELEVAGE);
        check(report, "origine élevage : ni XP ni butin de combat",
                () -> elevage.verdict() == Verdict.ORIGINE_NON_ELIGIBLE && elevage.xpGained() == 0.0D
                        && elevage.loot().isEmpty() && countDropped(world, base, lootItem) == droppedBefore);
        check(report, "mais la viande, si : élever pour manger est légitime", () -> elevage.meat().size() == 1);
        farmed.discard();

        Metiers.setLevel(hunter, Profession.CHASSEUR, 22);
        RabbitEntity easy = spawnRabbit(world, base, SpawnReason.NATURAL);
        Outcome easyKill = HuntEngine.kill(world, easy, hunter, orcLike, SpawnOrigin.NATURELLE);
        check(report, "niveau 22 sur niveau 10 (écart 12) : 25 % → 12,5 XP", () -> easyKill.xpGained() == 12.5D);
        easy.discard();

        Metiers.setLevel(hunter, Profession.CHASSEUR, 10);
        Metiers.addXp(hunter, Profession.CHASSEUR, Metiers.getXpToNextLevel(hunter, Profession.CHASSEUR) - 10.0D);
        RabbitEntity last = spawnRabbit(world, base, SpawnReason.NATURAL);
        Outcome levelUp = HuntEngine.kill(world, last, hunter, orcLike, SpawnOrigin.NATURELLE);
        check(report, "une proie fait passer le niveau 10 → 11",
                () -> levelUp.levelsGained() == 1 && Metiers.getLevel(hunter, Profession.CHASSEUR) == 11);
        last.discard();
        Metiers.setLevel(hunter, Profession.CHASSEUR, 1);
    }

    /** Les messages : un Chasseur sait pourquoi ; un autre joueur ne voit que le butin. */
    private static void scenarioFeedback(CreatureProfile rabbit, List<String> report) {
        section(report, "Chasse — messages");
        Text name = Text.literal("Lapin");
        Outcome xp = new Outcome(rabbit, SpawnOrigin.NATURELLE, true, Verdict.XP, 3.0D, 0, List.of(), List.of(), null);
        check(report, "XP gagnée : « Proie abattue … +3 XP »", () -> {
            String text = HuntFeedback.describe(name, xp).getString();
            return text.contains("Lapin") && text.contains("+3 XP");
        });
        Outcome farmed = new Outcome(rabbit, SpawnOrigin.ELEVAGE, false, Verdict.ORIGINE_NON_ELIGIBLE, 0.0D, 0, List.of(), List.of(), null);
        check(report, "élevage : le message nomme l'origine",
                () -> HuntFeedback.describe(name, farmed).getString().contains(HuntFeedback.origin(SpawnOrigin.ELEVAGE).getString()));
        Outcome tooLow = new Outcome(rabbit, SpawnOrigin.NATURELLE, true, Verdict.NIVEAU_INSUFFISANT, 0.0D, 0, List.of(), List.of(), null);
        check(report, "niveau insuffisant : le message donne le niveau requis",
                () -> HuntFeedback.describe(name, tooLow).getString().contains(" 1 "));
        Outcome silent = new Outcome(rabbit, SpawnOrigin.NATURELLE, true, Verdict.METIER_NON_APPRIS, 0.0D, 0, List.of(), List.of(), null);
        check(report, "sans le métier et sans butin : silence", () -> HuntFeedback.describe(name, silent) == null);
        Outcome levelUp = new Outcome(rabbit, SpawnOrigin.NATURELLE, true, Verdict.XP, 3.0D, 1, List.of(), List.of(), null);
        check(report, "passage de niveau annoncé", () -> HuntFeedback.describe(name, levelUp).getString().contains(
                Text.translatableWithFallback("hcm.chasse.niveau_gagne", "  Niveau supérieur !").getString().trim()));
    }

    // ------------------------------------------------------------------

    private static RabbitEntity spawnRabbit(ServerWorld world, BlockPos base, SpawnReason reason) {
        RabbitEntity rabbit = EntityType.RABBIT.spawn(world, base, reason);
        if (rabbit == null) {
            throw new IllegalStateException("le lapin n'a pas pu apparaître (" + reason + ")");
        }
        return rabbit;
    }

    private static void slay(ServerWorld world, LivingEntity entity, ServerPlayerEntity killer) {
        DamageSource source = world.getDamageSources().playerAttack(killer);
        entity.damage(world, source, 1000.0F);
    }

    private static int countDropped(ServerWorld world, BlockPos base, Identifier item) {
        return world.getEntitiesByClass(ItemEntity.class, new Box(base).expand(8.0D),
                        entity -> Registries.ITEM.getId(entity.getStack().getItem()).equals(item))
                .stream().mapToInt(entity -> entity.getStack().getCount()).sum();
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
