package net.hautecapitale.metiers.command;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.CraftEngine;
import net.hautecapitale.metiers.craft.CraftRecipe;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.entity.HcmEntities;
import net.hautecapitale.metiers.gadget.ChanneledTeleportItem;
import net.hautecapitale.metiers.gadget.ChanneledTeleportItem.Refusal;
import net.hautecapitale.metiers.gadget.EscapeRopeItem;
import net.hautecapitale.metiers.gadget.GadgetComponents;
import net.hautecapitale.metiers.gadget.GadgetItems;
import net.hautecapitale.metiers.gadget.Gadgets;
import net.hautecapitale.metiers.gadget.KitItem;
import net.hautecapitale.metiers.gadget.MiningDroneEntity;
import net.hautecapitale.metiers.gadget.MiningDroneItem;
import net.hautecapitale.metiers.gadget.RecallBeaconItem;
import net.hautecapitale.metiers.gadget.RepellentItem;
import net.hautecapitale.metiers.gadget.RopeLauncherItem;
import net.hautecapitale.metiers.gadget.ScannerItem;
import net.hautecapitale.metiers.gadget.ToggleGadgetItem;
import net.hautecapitale.metiers.gadget.WornGadgetItem;
import net.hautecapitale.metiers.hearth.HearthAttachment;
import net.hautecapitale.metiers.hearth.HearthLink;
import net.hautecapitale.metiers.hearth.HearthstoneItem;
import net.hautecapitale.metiers.npc.NpcRole;
import net.hautecapitale.metiers.npc.ProfessionScreenData;
import net.hautecapitale.metiers.npc.RoleGate;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diagnostic serveur des gadgets de l'Ingénieur et de la Pierre de foyer.
 *
 * <p>Le joueur d'essai est ici un {@link FakePlayer} de Fabric : il a un
 * gestionnaire réseau qui avale les paquets, ce qui permet de le téléporter
 * réellement — la Pierre de foyer, la corde d'évasion — là où un joueur en
 * mémoire ordinaire lèverait une exception.
 *
 * <p>Le terrain d'essai est le même genre de dalle en barrières que celui des
 * carcasses, un peu plus loin ; les quelques blocs posés pour les détecteurs
 * sont remis, et la dalle entière est comparée avant/après : aucun gadget ne
 * doit avoir touché à la carte.
 */
final class GadgetDiagnostic {

    private static boolean forcedByUs = false;

    private GadgetDiagnostic() {
    }

    static List<String> run(MinecraftServer server, List<String> report) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) {
            return report;
        }
        scenarioCatalogue(report);

        BlockPos spawn = world.getSpawnPoint().getPos();
        BlockPos base = new BlockPos(spawn.getX(), Math.min(world.getTopYInclusive() - 12, 240), spawn.getZ()).south(14).west(14);
        ChunkPos chunk = new ChunkPos(base);
        report.add("--- Gadgets — terrain d'essai");
        if (!world.isChunkLoaded(chunk.toLong())) {
            if (!world.getForcedChunks().contains(chunk.toLong())) {
                world.setChunkForced(chunk.x, chunk.z, true);
                forcedByUs = true;
            }
            report.add("  WAIT  chunk d'essai forcé en " + chunk + ", ses entités se chargent : relancez"
                    + " « /metiers diagnostic » dans quelques secondes pour la partie Gadgets");
            return report;
        }
        boolean floorFree = true;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                floorFree &= world.isAir(base.down().add(dx, 0, dz));
            }
        }
        boolean ready = floorFree;
        check(report, "chunk d'essai chargé, entités suivies, sol libre en " + base.toShortString(), () -> ready);
        if (!ready) {
            return report;
        }
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                world.setBlockState(base.down().add(dx, 0, dz), Blocks.BARRIER.getDefaultState());
            }
        }
        Map<BlockPos, BlockState> before = snapshot(world, base);

        ServerPlayerEntity player = DiagnosticPlayer.create(world, "GadgetTest");
        Box area = new Box(base).expand(12.0D);
        MetiersConfig.Foyer foyer = MetiersConfig.get().foyer;
        double previousRange = foyer.portee;
        boolean previousOffer = foyer.pierre_offerte;
        try {
            place(player, base);
            scenarioCraft(player, report);
            scenarioHearthLink(world, player, base, report);
            scenarioHearthstone(world, player, base, report);
            scenarioRecall(world, player, base, report);
            scenarioEscape(world, player, base, report);
            scenarioGrapple(world, player, base, report);
            scenarioToggles(world, player, base, report);
            scenarioWorn(world, player, report);
            scenarioScanners(world, player, base, report);
            scenarioKits(world, player, report);
            scenarioRepellent(world, player, base, report);
            scenarioDrone(world, player, base, report);
            scenarioMapUntouched(world, base, before, report);
        } finally {
            foyer.portee = previousRange;
            foyer.pierre_offerte = previousOffer;
            HearthAttachment.clear(player);
            Metiers.forget(player, Profession.INGENIEUR);
            player.clearStatusEffects();
            player.getItemCooldownManager().remove(Registries.ITEM.getId(GadgetItems.PIERRE_DE_FOYER));
            clear(player);
            world.getEntitiesByClass(Entity.class, area, entity -> entity instanceof ItemEntity
                    || entity instanceof MiningDroneEntity || entity instanceof ZombieEntity
                    || entity instanceof CowEntity).forEach(Entity::discard);
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    world.setBlockState(base.down().add(dx, 0, dz), Blocks.AIR.getDefaultState());
                }
            }
            if (forcedByUs) {
                world.setChunkForced(chunk.x, chunk.z, false);
                forcedByUs = false;
            }
        }
        report.add("      le drone qui suit un vrai joueur, et le rendu des gadgets : en jeu, hors de portée d'un diagnostic serveur");
        return report;
    }

    // ------------------------------------------------------------------

    /** Trente et un objets, un onglet, un drone, trente recettes — et pas un bloc. */
    private static void scenarioCatalogue(List<String> report) {
        section(report, "Gadgets — catalogue");
        List<Item> all = GadgetItems.all();
        check(report, "trente et un gadgets enregistrés sous haute_capitale_metiers", () -> all.size() == 31
                && all.stream().allMatch(item -> Registries.ITEM.getId(item).getNamespace().equals("haute_capitale_metiers")));
        check(report, "aucun d'eux n'est un bloc : rien à poser", () -> all.stream().noneMatch(item -> item instanceof BlockItem));
        check(report, "l'onglet créatif des gadgets existe",
                () -> Registries.ITEM_GROUP.containsId(HauteCapitaleMetiers.id("gadgets")));
        check(report, "le drone minier est un type d'entité, ni sauvegardé ni invocable",
                () -> Registries.ENTITY_TYPE.getId(HcmEntities.DRONE).equals(HauteCapitaleMetiers.id("drone_minier"))
                        && !HcmEntities.DRONE.isSaveable() && !HcmEntities.DRONE.isSummonable());
        check(report, "les composants des gadgets sont enregistrés (actif, filtre, position, drone, échéance)",
                () -> Registries.DATA_COMPONENT_TYPE.containsId(HauteCapitaleMetiers.id("actif"))
                        && Registries.DATA_COMPONENT_TYPE.containsId(HauteCapitaleMetiers.id("filtre"))
                        && Registries.DATA_COMPONENT_TYPE.containsId(HauteCapitaleMetiers.id("position"))
                        && Registries.DATA_COMPONENT_TYPE.containsId(HauteCapitaleMetiers.id("drone"))
                        && Registries.DATA_COMPONENT_TYPE.containsId(HauteCapitaleMetiers.id("echeance")));
        check(report, "l'effet « repoussant » est enregistré",
                () -> Registries.STATUS_EFFECT.containsId(HauteCapitaleMetiers.id("repoussant")));
        long recipes = HcmData.RECIPES.ids().stream().filter(id -> id.getPath().startsWith("ingenieur/")).count();
        // Les quatre pièces d'exosquelette demandent l'acier d'Epic Knights : sans lui, elles sont rejetées au chargement.
        int expected = Registries.ITEM.containsId(Identifier.of("magistuarmory", "steel_ingot")) ? 30 : 26;
        check(report, expected + " recettes d'Ingénieur chargées" + (expected == 26 ? " (Epic Knights absent : pas d'exosquelette ici)" : ""),
                () -> recipes == expected);
        check(report, "toutes produisent un gadget du mod", () -> HcmData.RECIPES.ids().stream()
                .filter(id -> id.getPath().startsWith("ingenieur/"))
                .map(HcmData.RECIPES::get)
                .allMatch(recipe -> recipe != null && recipe.profession() == Profession.INGENIEUR
                        && all.contains(Registries.ITEM.get(recipe.result().item()))));
    }

    /** Un gadget par palier, fabriqué pour de vrai dans l'atelier. */
    private static void scenarioCraft(ServerPlayerEntity player, List<String> report) {
        section(report, "Ingénieur — fabrication par palier");
        Metiers.forget(player, Profession.INGENIEUR);
        clear(player);
        Identifier salt = HauteCapitaleMetiers.id("ingenieur/sel_sacre");
        CraftRecipe saltRecipe = HcmData.RECIPES.get(salt);
        if (saltRecipe == null) {
            check(report, "la recette du sel sacré est chargée", () -> false);
            return;
        }
        giveIngredients(player, saltRecipe);
        check(report, "sans le métier Ingénieur : refus", () -> !CraftEngine.craft(player, salt, 1).succeeded());
        Metiers.learn(player, Profession.INGENIEUR);
        check(report, "Ingénieur niveau 1, recette de niveau 6 : refus (niveau insuffisant)",
                () -> CraftEngine.craft(player, salt, 1).refusal() == CraftEngine.Refusal.NIVEAU_INSUFFISANT);
        Metiers.setLevel(player, Profession.INGENIEUR, 50);

        String[][] tiers = {
                {"sel_sacre", "6"}, {"lunettes_nocturnes", "8"}, {"aimant_a_butin", "12"}, {"grappin", "20"},
                {"kit_de_campement", "30"}, {"drone_minier", "40"}, {"pierre_de_foyer_amelioree", "45"}};
        for (String[] tier : tiers) {
            Identifier id = HauteCapitaleMetiers.id("ingenieur/" + tier[0]);
            CraftRecipe recipe = HcmData.RECIPES.get(id);
            Item result = recipe == null ? null : Registries.ITEM.get(recipe.result().item());
            clear(player);
            if (recipe != null) {
                // Au niveau de la recette : l'XP est entière, sans décote « trop facile ».
                Metiers.setLevel(player, Profession.INGENIEUR, recipe.level());
                giveIngredients(player, recipe);
                if (recipe.cost() > 0 && CraftEngine.currency() != null) {
                    give(player, CraftEngine.currency(), recipe.cost());
                }
            }
            CraftEngine.Outcome outcome = recipe == null ? null : CraftEngine.craft(player, id, 1);
            check(report, tier[0] + " (niveau " + tier[1] + ") : fabriqué, ingrédients consommés, XP = 3 × niveau + 12",
                    () -> recipe != null && recipe.level() == Integer.parseInt(tier[1]) && outcome.succeeded()
                            && CraftEngine.count(player.getInventory(), result) == recipe.result().count()
                            && recipe.ingredients().stream().allMatch(ingredient ->
                                    CraftEngine.count(player.getInventory(), ingredient) == 0)
                            && outcome.xpGained() == 3.0D * recipe.level() + 12.0D);
        }
        clear(player);
    }

    /** Parler à l'aubergiste lie le foyer et offre la première pierre. */
    private static void scenarioHearthLink(ServerWorld world, ServerPlayerEntity player, BlockPos base, List<String> report) {
        section(report, "Foyer — liaison chez l'aubergiste");
        Identifier roleId = HauteCapitaleMetiers.id("aubergiste");
        NpcRole role = HcmData.ROLES.get(roleId);
        check(report, "le rôle aubergiste lie le foyer, les autres non", () -> role != null && role.hearth()
                && HcmData.ROLES.ids().stream().map(HcmData.ROLES::get).filter(r -> r != null && r.hearth()).count() == 1);
        if (role == null) {
            return;
        }
        HearthAttachment.clear(player);
        clear(player);
        MetiersConfig.get().foyer.pierre_offerte = true;
        Vec3d inn = Vec3d.ofBottomCenter(base.east(2));
        check(report, "aucun foyer au départ : la pierre refuse (aucune destination)",
                () -> hearthstone(false).check(player, new ItemStack(GadgetItems.PIERRE_DE_FOYER)) == Refusal.AUCUNE_DESTINATION);
        check(report, "ouvrir le registre de l'aubergiste lie le foyer à sa position",
                () -> RoleGate.open(player, roleId, inn) && HearthAttachment.of(player) != null
                        && HearthAttachment.of(player).pos().equals(GlobalPos.create(world.getRegistryKey(), base.east(2)))
                        && HearthAttachment.of(player).name().equals(role.title().orElse("")));
        check(report, "et offre une Pierre de foyer au joueur qui n'en a pas",
                () -> CraftEngine.count(player.getInventory(), GadgetItems.PIERRE_DE_FOYER) == 1);
        check(report, "revenir au même aubergiste : déjà lié, pas de seconde pierre",
                () -> HearthLink.bind(player, role, inn) == HearthLink.Result.DEJA_LA
                        && CraftEngine.count(player.getInventory(), GadgetItems.PIERRE_DE_FOYER) == 1);
        Vec3d other = Vec3d.ofBottomCenter(base.west(2));
        check(report, "un autre aubergiste déplace le foyer",
                () -> HearthLink.bind(player, role, other, "Auberge du Couchant") == HearthLink.Result.LIE
                        && HearthAttachment.of(player).pos().pos().equals(base.west(2)));
        check(report, "les deux auberges sont retenues, la seconde choisie, sous le nom du PNJ",
                () -> HearthAttachment.of(player).inns().size() == 2 && HearthAttachment.of(player).selected() == 1
                        && HearthAttachment.of(player).name().equals("Auberge du Couchant"));
        check(report, "choisir la première auberge y ramène le foyer, sans doublon",
                () -> HearthLink.choose(player, 1) && HearthAttachment.of(player).pos().pos().equals(base.east(2))
                        && HearthAttachment.of(player).inns().size() == 2);
        check(report, "un numéro hors liste est refusé", () -> !HearthLink.choose(player, 3));
        check(report, "revenir chez la seconde la choisit de nouveau, toujours deux auberges",
                () -> HearthLink.bind(player, role, other) == HearthLink.Result.LIE
                        && HearthAttachment.of(player).selected() == 1 && HearthAttachment.of(player).inns().size() == 2);
        check(report, "le registre de l'aubergiste liste les deux auberges, la choisie sans bouton",
                () -> {
                    ProfessionScreenData data = RoleGate.build(player, roleId, role);
                    long inns = data.entries().stream().filter(e -> e.action().map(a -> a.getPath().startsWith("foyer/")).orElse(false)).count();
                    long chosen = data.entries().stream().filter(e -> e.action().map(a -> a.getPath().equals("foyer/1")).orElse(false)
                            && !e.available()).count();
                    return inns == 2 && chosen == 1;
                });
        check(report, "la sauvegarde relit une liste, et une vieille sauvegarde à foyer unique aussi",
                () -> {
                    var ops = com.mojang.serialization.JsonOps.INSTANCE;
                    HearthAttachment current = HearthAttachment.of(player);
                    var roundTrip = HearthAttachment.CODEC.encodeStart(ops, current).flatMap(json -> HearthAttachment.CODEC.parse(ops, json)).result();
                    var legacy = HearthAttachment.CODEC.parse(ops, com.google.gson.JsonParser.parseString(
                            "{\"position\":{\"dimension\":\"minecraft:overworld\",\"pos\":[1,2,3]},\"nom\":\"Vieille auberge\"}")).result();
                    return roundTrip.map(current::equals).orElse(false)
                            && legacy.map(h -> h.inns().size() == 1 && h.name().equals("Vieille auberge")
                                    && h.pos().pos().equals(new BlockPos(1, 2, 3))).orElse(false);
                });
        clear(player);
        give(player, GadgetItems.PIERRE_DE_FOYER_AMELIOREE, 1);
        check(report, "une pierre améliorée dans le sac compte : rien d'offert",
                () -> {
                    HearthLink.bind(player, role, inn);
                    return CraftEngine.count(player.getInventory(), GadgetItems.PIERRE_DE_FOYER) == 0;
                });
        MetiersConfig.get().foyer.pierre_offerte = false;
        clear(player);
        check(report, "pierre_offerte = false : l'aubergiste lie sans rien donner",
                () -> {
                    HearthLink.bind(player, role, other);
                    return HearthLink.hasStone(player) == false && HearthAttachment.of(player).pos().pos().equals(base.west(2));
                });
        MetiersConfig.get().foyer.pierre_offerte = true;
        check(report, "le foyer survit à la mort (attachement copié)",
                () -> HearthAttachment.FOYER.copyOnDeath());
    }

    /** La Pierre : canalisation, annulations, départ, recharge, portée. */
    private static void scenarioHearthstone(ServerWorld world, ServerPlayerEntity player, BlockPos base, List<String> report) {
        section(report, "Foyer — Pierre de foyer");
        MetiersConfig.Foyer config = MetiersConfig.get().foyer;
        HearthstoneItem stone = hearthstone(false);
        HearthstoneItem improved = hearthstone(true);
        BlockPos home = base.west(2);
        HearthAttachment.set(player, new HearthAttachment(GlobalPos.create(world.getRegistryKey(), home), "Auberge d'essai"));
        clear(player);
        ItemStack stack = new ItemStack(GadgetItems.PIERRE_DE_FOYER);
        player.setStackInHand(Hand.MAIN_HAND, stack);
        player.getItemCooldownManager().remove(Registries.ITEM.getId(GadgetItems.PIERRE_DE_FOYER));
        place(player, base.east(3));

        check(report, "à portée, dans la même dimension : aucun refus", () -> stone.check(player, stack) == null);
        check(report, "clic droit : la canalisation commence (" + config.canalisation_secondes + " s, objet en main tenu)",
                () -> stone.use(world, player, Hand.MAIN_HAND) == ActionResult.CONSUME
                        && ChanneledTeleportItem.isChanneling(player) && player.isUsingItem()
                        && stone.getMaxUseTime(stack, player) == config.canalisation_secondes * 20);
        check(report, "bouger d'un bloc pendant la canalisation l'annule", () -> {
            place(player, base.east(4));
            stone.usageTick(world, player, stack, 60);
            return !ChanneledTeleportItem.isChanneling(player) && !player.isUsingItem();
        });
        check(report, "rester immobile la laisse courir", () -> {
            stone.use(world, player, Hand.MAIN_HAND);
            stone.usageTick(world, player, stack, 60);
            stone.usageTick(world, player, stack, 40);
            return ChanneledTeleportItem.isChanneling(player) && player.isUsingItem();
        });
        check(report, "subir des dégâts l'annule (crochet AFTER_DAMAGE)", () -> {
            ServerLivingEntityEvents.AFTER_DAMAGE.invoker().afterDamage(player, world.getDamageSources().generic(), 1.0F, 1.0F, false);
            return !ChanneledTeleportItem.isChanneling(player) && !player.isUsingItem();
        });
        check(report, "relâcher le clic l'annule", () -> {
            stone.use(world, player, Hand.MAIN_HAND);
            boolean started = ChanneledTeleportItem.isChanneling(player);
            player.stopUsingItem();
            stone.onStoppedUsing(stack, world, player, 50);
            return started && !ChanneledTeleportItem.isChanneling(player);
        });
        check(report, "au bout de la canalisation : le joueur est au foyer", () -> {
            stone.use(world, player, Hand.MAIN_HAND);
            stone.finishUsing(stack, world, player);
            return !ChanneledTeleportItem.isChanneling(player)
                    && player.getBlockPos().equals(home)
                    && player.getEntityWorld() == world;
        });
        check(report, "la recharge s'applique (" + config.recharge_secondes + " s)",
                () -> player.getItemCooldownManager().isCoolingDown(stack));
        player.getItemCooldownManager().remove(Registries.ITEM.getId(GadgetItems.PIERRE_DE_FOYER));

        // Portée : le foyer est envoyé loin, la pierre devient « trop faible ».
        BlockPos far = home.east((int) config.portee + 500);
        HearthAttachment.set(player, new HearthAttachment(GlobalPos.create(world.getRegistryKey(), far), "Loin"));
        check(report, "foyer à " + (config.portee + 500) + " blocs : pierre simple trop faible (portée " + config.portee + ")",
                () -> stone.check(player, stack) == Refusal.TROP_LOIN
                        && stone.use(world, player, Hand.MAIN_HAND) == ActionResult.FAIL
                        && !ChanneledTeleportItem.isChanneling(player));
        ItemStack improvedStack = new ItemStack(GadgetItems.PIERRE_DE_FOYER_AMELIOREE);
        check(report, "la pierre améliorée porte × " + config.amelioree_portee_facteur + " : elle y arrive",
                () -> improved.check(player, improvedStack) == null);
        BlockPos veryFar = home.east((int) (config.portee * config.amelioree_portee_facteur) + 500);
        HearthAttachment.set(player, new HearthAttachment(GlobalPos.create(world.getRegistryKey(), veryFar), "Très loin"));
        check(report, "au-delà, même l'améliorée est trop faible",
                () -> improved.check(player, improvedStack) == Refusal.TROP_LOIN);
        check(report, "le message est bien « trop faible »", () -> {
            String key = ((net.minecraft.text.TranslatableTextContent) stone.refusalText(Refusal.TROP_LOIN).getContent()).getKey();
            return key.equals("hcm.foyer.trop_faible");
        });
        config.portee = 0.0D;
        check(report, "portee = 0 : illimitée", () -> stone.check(player, stack) == null);
        config.portee = 4000.0D;
        check(report, "l'améliorée recharge moins longtemps (" + config.amelioree_recharge_secondes + " s < "
                + config.recharge_secondes + " s)", () -> config.amelioree_recharge_secondes < config.recharge_secondes
                && improved.improved() && !stone.improved());

        ServerWorld nether = world.getServer().getWorld(World.NETHER);
        if (nether != null) {
            HearthAttachment.set(player, new HearthAttachment(GlobalPos.create(World.NETHER, home), "Enfer"));
            check(report, "foyer dans une autre dimension : refus (autre_dimension = false)",
                    () -> !config.autre_dimension && stone.check(player, stack) == Refusal.AUTRE_DIMENSION);
        }
        HearthAttachment.set(player, new HearthAttachment(GlobalPos.create(world.getRegistryKey(), home), "Auberge d'essai"));
        clear(player);
    }

    /** La balise de rappel : accroupi pour retenir, puis même canalisation. */
    private static void scenarioRecall(ServerWorld world, ServerPlayerEntity player, BlockPos base, List<String> report) {
        section(report, "Gadgets — balise de rappel");
        clear(player);
        place(player, base);
        ItemStack beacon = new ItemStack(GadgetItems.BALISE_DE_RAPPEL);
        player.setStackInHand(Hand.MAIN_HAND, beacon);
        RecallBeaconItem item = (RecallBeaconItem) GadgetItems.BALISE_DE_RAPPEL;
        check(report, "sans position retenue : refus", () -> item.check(player, beacon) == Refusal.AUCUNE_DESTINATION);
        player.setSneaking(true);
        check(report, "accroupi + clic droit retient la position", () -> item.use(world, player, Hand.MAIN_HAND) == ActionResult.CONSUME
                && base.equals(beacon.get(GadgetComponents.POSITION).pos()));
        player.setSneaking(false);
        place(player, base.east(3));
        check(report, "debout + clic droit canalise (" + Gadgets.config().rappel_canalisation_secondes + " s)",
                () -> item.use(world, player, Hand.MAIN_HAND) == ActionResult.CONSUME && ChanneledTeleportItem.isChanneling(player)
                        && item.getMaxUseTime(beacon, player) == Gadgets.config().rappel_canalisation_secondes * 20);
        check(report, "à l'arrivée, le joueur est sur la position retenue", () -> {
            item.finishUsing(beacon, world, player);
            return player.getBlockPos().equals(base) && player.getItemCooldownManager().isCoolingDown(beacon);
        });
        player.getItemCooldownManager().remove(Registries.ITEM.getId(GadgetItems.BALISE_DE_RAPPEL));
        clear(player);
    }

    /** La corde d'évasion : droit vers le ciel, jamais à travers un mur. */
    private static void scenarioEscape(ServerWorld world, ServerPlayerEntity player, BlockPos base, List<String> report) {
        section(report, "Gadgets — corde d'évasion");
        clear(player);
        ItemStack rope = new ItemStack(GadgetItems.CORDE_D_EVASION);
        player.setStackInHand(Hand.MAIN_HAND, rope);
        check(report, "sur la dalle, à ciel ouvert : nulle part où mener",
                () -> EscapeRopeItem.surfaceAbove(world, base) == null);
        BlockPos under = base.down(6);
        check(report, "sous la dalle : la surface est juste au-dessus d'elle",
                () -> base.equals(EscapeRopeItem.surfaceAbove(world, under)));
        place(player, under);
        int damageBefore = rope.getDamage();
        check(report, "clic droit sous la dalle : le joueur ressort dessus, la corde s'use d'un point", () -> {
            ActionResult result = GadgetItems.CORDE_D_EVASION.use(world, player, Hand.MAIN_HAND);
            return result == ActionResult.CONSUME && player.getBlockPos().equals(base)
                    && player.getItemCooldownManager().isCoolingDown(rope) && rope.getDamage() == damageBefore + 1;
        });
        player.getItemCooldownManager().remove(Registries.ITEM.getId(GadgetItems.CORDE_D_EVASION));
        check(report, "déjà en surface : refus, pas d'usure", () -> {
            int before = rope.getDamage();
            return GadgetItems.CORDE_D_EVASION.use(world, player, Hand.MAIN_HAND) == ActionResult.FAIL && rope.getDamage() == before;
        });
        clear(player);
    }

    /** Le grappin : une impulsion vers le bloc visé, rien d'autre. */
    private static void scenarioGrapple(ServerWorld world, ServerPlayerEntity player, BlockPos base, List<String> report) {
        section(report, "Gadgets — grappin");
        clear(player);
        place(player, base);
        ItemStack hook = new ItemStack(GadgetItems.GRAPPIN);
        player.setStackInHand(Hand.MAIN_HAND, hook);
        player.setPitch(-90.0F);
        check(report, "visée vers le ciel : rien à accrocher, pas d'usure", () -> {
            int before = hook.getDamage();
            return RopeLauncherItem.aim(player) == null
                    && GadgetItems.GRAPPIN.use(world, player, Hand.MAIN_HAND) == ActionResult.FAIL && hook.getDamage() == before;
        });
        check(report, "l'impulsion vers une cible porte le joueur vers elle, et vers le haut", () -> {
            player.setVelocity(Vec3d.ZERO);
            RopeLauncherItem.launch(player, Vec3d.ofCenter(base.east(10).up(4)));
            Vec3d v = player.getVelocity();
            return v.x > 0.5D && v.y > 0.0D && Math.abs(v.z) < 0.1D;
        });
        player.setVelocity(Vec3d.ZERO);
        player.setPitch(0.0F);
        clear(player);
    }

    /** Aimant, compresseur, broyeur : marche/arrêt, et leur effet sur le sac. */
    private static void scenarioToggles(ServerWorld world, ServerPlayerEntity player, BlockPos base, List<String> report) {
        section(report, "Gadgets — aimant, compresseur, broyeur");
        clear(player);
        place(player, base);
        ItemStack magnet = new ItemStack(GadgetItems.AIMANT_A_BUTIN);
        player.setStackInHand(Hand.MAIN_HAND, magnet);
        check(report, "un gadget neuf est éteint ; clic droit l'allume ; un second l'éteint", () -> {
            boolean off = !Gadgets.isOn(magnet);
            GadgetItems.AIMANT_A_BUTIN.use(world, player, Hand.MAIN_HAND);
            boolean on = Gadgets.isOn(magnet);
            GadgetItems.AIMANT_A_BUTIN.use(world, player, Hand.MAIN_HAND);
            return off && on && !Gadgets.isOn(magnet);
        });
        ItemEntity drop = new ItemEntity(world, base.getX() + 4.5D, base.getY() + 0.5D, base.getZ() + 0.5D, new ItemStack(Items.STONE));
        drop.setVelocity(Vec3d.ZERO);
        world.spawnEntity(drop);
        check(report, "l'aimant tire vers le joueur un objet à 4 blocs", () -> {
            ToggleGadgetItem.attract(world, player);
            return drop.getVelocity().x < -0.1D;
        });
        drop.discard();

        clear(player);
        give(player, Items.IRON_INGOT, 9);
        give(player, Items.GOLD_INGOT, 5);
        check(report, "le compresseur : 9 lingots de fer → 1 bloc, 5 lingots d'or restent",
                () -> ToggleGadgetItem.compress(player.getInventory())
                        && CraftEngine.count(player.getInventory(), Items.IRON_INGOT) == 0
                        && CraftEngine.count(player.getInventory(), Items.IRON_BLOCK) == 1
                        && CraftEngine.count(player.getInventory(), Items.GOLD_INGOT) == 5);
        check(report, "plus rien à compresser : il ne fait rien", () -> !ToggleGadgetItem.compress(player.getInventory()));

        clear(player);
        ItemStack grinder = new ItemStack(GadgetItems.BROYEUR_AUTOMATIQUE);
        player.setStackInHand(Hand.MAIN_HAND, grinder);
        player.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.DIRT));
        player.setSneaking(true);
        check(report, "accroupi + clic droit : le broyeur prend l'objet de l'autre main comme filtre", () -> {
            GadgetItems.BROYEUR_AUTOMATIQUE.use(world, player, Hand.MAIN_HAND);
            return Identifier.ofVanilla("dirt").equals(grinder.get(GadgetComponents.FILTRE));
        });
        player.setSneaking(false);
        player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
        give(player, Items.DIRT, 70);
        give(player, Items.COBBLESTONE, 3);
        check(report, "il détruit la terre du sac, pas le reste", () -> ToggleGadgetItem.trash(player.getInventory(), grinder.get(GadgetComponents.FILTRE)) == 70
                && CraftEngine.count(player.getInventory(), Items.DIRT) == 0
                && CraftEngine.count(player.getInventory(), Items.COBBLESTONE) == 3);
        check(report, "sans filtre, il ne détruit rien", () -> ToggleGadgetItem.trash(player.getInventory(), null) == 0);
        clear(player);
    }

    /** Ce qui se porte : lunettes, casque, bottes, exosquelette, et les attributs. */
    private static void scenarioWorn(ServerWorld world, ServerPlayerEntity player, List<String> report) {
        section(report, "Gadgets — portés");
        clear(player);
        player.clearStatusEffects();
        ItemStack goggles = new ItemStack(GadgetItems.LUNETTES_NOCTURNES);
        check(report, "les lunettes nocturnes s'équipent sur la tête", () -> {
            var equippable = goggles.get(DataComponentTypes.EQUIPPABLE);
            return equippable != null && equippable.slot() == EquipmentSlot.HEAD;
        });
        check(report, "portées : vision nocturne ; dans le sac : rien", () -> {
            GadgetItems.LUNETTES_NOCTURNES.inventoryTick(goggles, world, player, EquipmentSlot.MAINHAND);
            boolean none = !player.hasStatusEffect(StatusEffects.NIGHT_VISION);
            GadgetItems.LUNETTES_NOCTURNES.inventoryTick(goggles, world, player, EquipmentSlot.HEAD);
            return none && player.hasStatusEffect(StatusEffects.NIGHT_VISION);
        });
        player.clearStatusEffects();
        check(report, "la lanterne de sac agit depuis le sac, mais seulement dans le noir", () -> {
            ItemStack lantern = new ItemStack(GadgetItems.LANTERNE_DE_SAC);
            GadgetItems.LANTERNE_DE_SAC.inventoryTick(lantern, world, player, EquipmentSlot.MAINHAND);
            boolean dark = Gadgets.isDark(world, player);
            return player.hasStatusEffect(StatusEffects.NIGHT_VISION) == dark;
        });
        player.clearStatusEffects();
        check(report, "les bottes de lave : résistance au feu, aux pieds seulement", () -> {
            ItemStack boots = new ItemStack(GadgetItems.BOTTES_DE_LAVE);
            GadgetItems.BOTTES_DE_LAVE.inventoryTick(boots, world, player, EquipmentSlot.HEAD);
            boolean none = !player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
            GadgetItems.BOTTES_DE_LAVE.inventoryTick(boots, world, player, EquipmentSlot.FEET);
            return none && player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
        });
        player.clearStatusEffects();
        player.equipStack(EquipmentSlot.HEAD, new ItemStack(GadgetItems.EXOSQUELETTE_CASQUE));
        player.equipStack(EquipmentSlot.CHEST, new ItemStack(GadgetItems.EXOSQUELETTE_PLASTRON));
        player.equipStack(EquipmentSlot.LEGS, new ItemStack(GadgetItems.EXOSQUELETTE_JAMBIERES));
        check(report, "trois pièces d'exosquelette : pas de set, pas de bonus", () -> {
            GadgetItems.EXOSQUELETTE_CASQUE.inventoryTick(player.getEquippedStack(EquipmentSlot.HEAD), world, player, EquipmentSlot.HEAD);
            return !WornGadgetItem.fullExosuit(player) && !player.hasStatusEffect(StatusEffects.HASTE);
        });
        player.equipStack(EquipmentSlot.FEET, new ItemStack(GadgetItems.EXOSQUELETTE_BOTTES));
        check(report, "quatre pièces : célérité et résistance", () -> {
            GadgetItems.EXOSQUELETTE_CASQUE.inventoryTick(player.getEquippedStack(EquipmentSlot.HEAD), world, player, EquipmentSlot.HEAD);
            return WornGadgetItem.fullExosuit(player) && player.hasStatusEffect(StatusEffects.HASTE)
                    && player.hasStatusEffect(StatusEffects.RESISTANCE);
        });
        check(report, "l'exosquelette protège comme du fer", () -> {
            AttributeModifiersComponent modifiers = new ItemStack(GadgetItems.EXOSQUELETTE_PLASTRON).get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            return modifiers != null && modifiers.modifiers().stream().anyMatch(entry -> entry.attribute().equals(EntityAttributes.ARMOR)
                    && entry.modifier().value() == 6.0D);
        });
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            player.equipStack(slot, ItemStack.EMPTY);
        }
        player.clearStatusEffects();
        check(report, "les bottes stabilisatrices : recul, chute sans danger, dégâts de chute réduits, aux pieds", () -> {
            AttributeModifiersComponent modifiers = new ItemStack(GadgetItems.BOTTES_STABILISATRICES).get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            return modifiers != null
                    && has(modifiers, EntityAttributes.KNOCKBACK_RESISTANCE, AttributeModifierSlot.FEET)
                    && has(modifiers, EntityAttributes.SAFE_FALL_DISTANCE, AttributeModifierSlot.FEET)
                    && has(modifiers, EntityAttributes.FALL_DAMAGE_MULTIPLIER, AttributeModifierSlot.FEET);
        });
        check(report, "les gants de portée : +2 blocs de portée, en main gauche", () -> {
            AttributeModifiersComponent modifiers = new ItemStack(GadgetItems.GANTS_DE_PORTEE).get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            return modifiers != null
                    && has(modifiers, EntityAttributes.BLOCK_INTERACTION_RANGE, AttributeModifierSlot.OFFHAND)
                    && has(modifiers, EntityAttributes.ENTITY_INTERACTION_RANGE, AttributeModifierSlot.OFFHAND);
        });
        check(report, "en main gauche, la portée passe de 4,5 à 6,5 (et rien en main droite)", () -> {
            AttributeModifiersComponent modifiers = new ItemStack(GadgetItems.GANTS_DE_PORTEE).get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            return modifiers != null
                    && modifiers.applyOperations(EntityAttributes.BLOCK_INTERACTION_RANGE, 4.5D, EquipmentSlot.OFFHAND) == 6.5D
                    && modifiers.applyOperations(EntityAttributes.BLOCK_INTERACTION_RANGE, 4.5D, EquipmentSlot.MAINHAND) == 4.5D;
        });
        clear(player);
    }

    /** Les détecteurs lisent la carte, ils ne la touchent pas. */
    private static void scenarioScanners(ServerWorld world, ServerPlayerEntity player, BlockPos base, List<String> report) {
        section(report, "Gadgets — détecteurs");
        clear(player);
        place(player, base);
        int radius = Gadgets.config().scanner_rayon;
        ScannerItem ore = (ScannerItem) GadgetItems.DETECTEUR_DE_MINERAI;
        ScannerItem resonator = (ScannerItem) GadgetItems.RESONATEUR_DE_CRISTAL;
        ScannerItem geode = (ScannerItem) GadgetItems.CHERCHEUR_DE_GEODE;
        ScannerItem echo = (ScannerItem) GadgetItems.SONDE_A_ECHO;
        check(report, "dans le ciel : aucun minerai à " + radius + " blocs",
                () -> ScannerItem.countOres(world, base, radius) == 0 && key(ore.scan(world, player)).equals("hcm.gadget.minerai.rien"));
        BlockPos orePos = base.up(3).east(2);
        BlockPos diamondPos = base.up(2).west(3);
        world.setBlockState(orePos, Blocks.IRON_ORE.getDefaultState());
        world.setBlockState(diamondPos, Blocks.DIAMOND_ORE.getDefaultState());
        try {
            check(report, "deux minerais posés pour l'essai : le détecteur les compte",
                    () -> ScannerItem.countOres(world, base, radius) == 2 && key(ore.scan(world, player)).equals("hcm.gadget.minerai.trouve"));
            check(report, "le résonateur vibre près du diamant", () -> key(resonator.scan(world, player)).equals("hcm.gadget.resonateur.vibre"));
            check(report, "les lunettes de prospection comptent de même", () -> ScannerItem.countOres(world, player.getBlockPos(), radius) == 2);
        } finally {
            world.setBlockState(orePos, Blocks.AIR.getDefaultState());
            world.setBlockState(diamondPos, Blocks.AIR.getDefaultState());
        }
        check(report, "minerais retirés : le résonateur se tait", () -> key(resonator.scan(world, player)).equals("hcm.gadget.resonateur.rien"));
        check(report, "le chercheur de géode répond (signal ou silence, selon la carte)",
                () -> key(geode.scan(world, player)).startsWith("hcm.gadget.geode."));
        check(report, "la sonde à écho répond (cité, profondeurs, ou silence)",
                () -> key(echo.scan(world, player)).startsWith("hcm.gadget.echo."));
        check(report, "clic droit : réponse et court délai", () -> {
            ItemStack stack = new ItemStack(GadgetItems.DETECTEUR_DE_MINERAI);
            player.setStackInHand(Hand.MAIN_HAND, stack);
            return ore.use(world, player, Hand.MAIN_HAND) == ActionResult.CONSUME && player.getItemCooldownManager().isCoolingDown(stack);
        });
        check(report, "les lunettes de trésor ne trouvent aucun coffre ici", () -> WornGadgetItem.revealChests(world, player) == 0);
        player.getItemCooldownManager().remove(Registries.ITEM.getId(GadgetItems.DETECTEUR_DE_MINERAI));
        clear(player);
    }

    /** Secours, campement, raffinage. */
    private static void scenarioKits(ServerWorld world, ServerPlayerEntity player, List<String> report) {
        section(report, "Gadgets — kits");
        clear(player);
        player.clearStatusEffects();
        player.setHealth(5.0F);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 0));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 0));
        ItemStack aid = new ItemStack(GadgetItems.KIT_DE_SECOURS, 2);
        player.setStackInHand(Hand.MAIN_HAND, aid);
        check(report, "kit de secours : vie pleine, poison retiré, vitesse gardée, un kit consommé, recharge", () -> {
            GadgetItems.KIT_DE_SECOURS.use(world, player, Hand.MAIN_HAND);
            return player.getHealth() == player.getMaxHealth() && !player.hasStatusEffect(StatusEffects.POISON)
                    && player.hasStatusEffect(StatusEffects.SPEED) && aid.getCount() == 1
                    && player.getItemCooldownManager().isCoolingDown(aid);
        });
        player.getItemCooldownManager().remove(Registries.ITEM.getId(GadgetItems.KIT_DE_SECOURS));
        player.clearStatusEffects();
        ItemStack camp = new ItemStack(GadgetItems.KIT_DE_CAMPEMENT, 1);
        player.setStackInHand(Hand.MAIN_HAND, camp);
        check(report, "kit de campement : régénération, résistance, kit consommé", () -> {
            GadgetItems.KIT_DE_CAMPEMENT.use(world, player, Hand.MAIN_HAND);
            return player.hasStatusEffect(StatusEffects.REGENERATION) && player.hasStatusEffect(StatusEffects.RESISTANCE)
                    && player.getMainHandStack().isEmpty();
        });
        player.clearStatusEffects();
        clear(player);
        give(player, Items.RAW_IRON, 2);
        give(player, Items.RAW_COPPER, 1);
        check(report, "kit de raffinage : un brut devient un lingot par passage", () -> KitItem.refineOne(player.getInventory())
                && CraftEngine.count(player.getInventory(), Items.IRON_INGOT) + CraftEngine.count(player.getInventory(), Items.COPPER_INGOT) == 1
                && CraftEngine.count(player.getInventory(), Items.RAW_IRON) + CraftEngine.count(player.getInventory(), Items.RAW_COPPER) == 2);
        check(report, "clic droit : le kit reste, reçoit une échéance et une recharge", () -> {
            ItemStack kit = new ItemStack(GadgetItems.KIT_DE_RAFFINAGE);
            player.setStackInHand(Hand.MAIN_HAND, kit);
            GadgetItems.KIT_DE_RAFFINAGE.use(world, player, Hand.MAIN_HAND);
            return kit.getCount() == 1 && kit.contains(GadgetComponents.ECHEANCE) && player.getItemCooldownManager().isCoolingDown(kit);
        });
        check(report, "rien à raffiner : il le dit", () -> {
            clear(player);
            return !KitItem.refineOne(player.getInventory());
        });
        player.getItemCooldownManager().remove(Registries.ITEM.getId(GadgetItems.KIT_DE_RAFFINAGE));
        clear(player);
    }

    /** Le sel sacré : les monstres qui naîtraient près du porteur sont repoussés. */
    private static void scenarioRepellent(ServerWorld world, ServerPlayerEntity player, BlockPos base, List<String> report) {
        section(report, "Gadgets — repoussants");
        clear(player);
        player.clearStatusEffects();
        place(player, base);
        ItemStack salt = new ItemStack(GadgetItems.SEL_SACRE, 3);
        player.setStackInHand(Hand.MAIN_HAND, salt);
        check(report, "le sel : effet repoussant palier 0, " + Gadgets.config().sel_secondes + " s, un sel consommé", () -> {
            GadgetItems.SEL_SACRE.use(world, player, Hand.MAIN_HAND);
            StatusEffectInstance effect = player.getStatusEffect(RepellentItem.REPOUSSANT);
            return effect != null && effect.getAmplifier() == 0 && effect.getDuration() == Gadgets.config().sel_secondes * 20
                    && salt.getCount() == 2;
        });
        int radius = RepellentItem.radius(0);
        MobEntity near = EntityType.ZOMBIE.create(world, SpawnReason.NATURAL);
        MobEntity far = EntityType.ZOMBIE.create(world, SpawnReason.NATURAL);
        MobEntity cow = EntityType.COW.create(world, SpawnReason.NATURAL);
        if (near == null || far == null || cow == null) {
            check(report, "création des mobs d'essai", () -> false);
            return;
        }
        near.refreshPositionAndAngles(base.getX() + 0.5D + radius - 2, base.getY(), base.getZ() + 0.5D, 0.0F, 0.0F);
        far.refreshPositionAndAngles(base.getX() + 0.5D + radius + 20, base.getY(), base.getZ() + 0.5D, 0.0F, 0.0F);
        cow.refreshPositionAndAngles(base.getX() + 0.5D, base.getY(), base.getZ() + 1.5D, 0.0F, 0.0F);
        List<ServerPlayerEntity> players = List.of(player);
        check(report, "un zombie naissant à " + (radius - 2) + " blocs est repoussé",
                () -> RepellentItem.shouldRepel(near, SpawnReason.NATURAL, players));
        check(report, "à " + (radius + 20) + " blocs, non", () -> !RepellentItem.shouldRepel(far, SpawnReason.NATURAL, players));
        check(report, "une vache, jamais (pas un monstre)", () -> !RepellentItem.shouldRepel(cow, SpawnReason.NATURAL, players));
        check(report, "un zombie d'œuf ou de commande, jamais (ce sont des actes)",
                () -> !RepellentItem.shouldRepel(near, SpawnReason.SPAWN_ITEM_USE, players)
                        && !RepellentItem.shouldRepel(near, SpawnReason.COMMAND, players));
        check(report, "un zombie de générateur, oui", () -> RepellentItem.shouldRepel(near, SpawnReason.SPAWNER, players));
        check(report, "sans joueur protégé : rien", () -> !RepellentItem.shouldRepel(near, SpawnReason.NATURAL, List.of()));
        player.clearStatusEffects();
        ItemStack balm = new ItemStack(GadgetItems.BAUME_SACRE);
        player.setStackInHand(Hand.MAIN_HAND, balm);
        int balmRadius = RepellentItem.radius(2);
        check(report, "le baume porte à " + balmRadius + " blocs : le zombie à " + (radius + 20) + " l'est-il ? " + (radius + 20 <= balmRadius), () -> {
            GadgetItems.BAUME_SACRE.use(world, player, Hand.MAIN_HAND);
            return RepellentItem.shouldRepel(far, SpawnReason.NATURAL, players) == (radius + 20 <= balmRadius)
                    && RepellentItem.shouldRepel(near, SpawnReason.NATURAL, players);
        });
        check(report, "un mob marqué disparaît en entrant dans le monde (crochet ENTITY_LOAD)", () -> {
            RepellentItem.repel(near.getUuid());
            world.spawnEntity(near);
            boolean gone = near.isRemoved();
            near.discard();
            return gone && !RepellentItem.isRepelled(near.getUuid());
        });
        check(report, "un mob non marqué entre normalement", () -> {
            world.spawnEntity(far);
            boolean alive = !far.isRemoved();
            far.discard();
            return alive;
        });
        player.clearStatusEffects();
        clear(player);
    }

    /** Le drone : déployé, il ramasse ; rappelé, il s'en va ; sans maître, il s'éteint. */
    private static void scenarioDrone(ServerWorld world, ServerPlayerEntity player, BlockPos base, List<String> report) {
        section(report, "Gadgets — drone minier");
        clear(player);
        place(player, base);
        ItemStack gadget = new ItemStack(GadgetItems.DRONE_MINIER);
        player.setStackInHand(Hand.MAIN_HAND, gadget);
        check(report, "clic droit déploie un drone au nom du joueur, retenu par le gadget", () -> {
            GadgetItems.DRONE_MINIER.use(world, player, Hand.MAIN_HAND);
            MiningDroneEntity drone = MiningDroneItem.drone(world, gadget);
            return drone != null && drone.isAlive() && player.getUuid().equals(drone.owner())
                    && drone.getUuid().equals(gadget.get(GadgetComponents.DRONE));
        });
        MiningDroneEntity drone = MiningDroneItem.drone(world, gadget);
        if (drone == null) {
            return;
        }
        ItemEntity drop = new ItemEntity(world, drone.getX() + 1.0D, drone.getY(), drone.getZ(), new ItemStack(Items.COBBLESTONE, 5));
        world.spawnEntity(drop);
        check(report, "il ramasse ce qui traîne à portée, dans le sac du maître", () -> drone.collect(world, player) == 5
                && CraftEngine.count(player.getInventory(), Items.COBBLESTONE) == 5 && drop.isRemoved());
        check(report, "il n'est ni attaquable, ni poussable, ni sauvegardé",
                () -> !drone.isAttackable() && !drone.isPushable() && !drone.shouldSave()
                        && !drone.damage(world, world.getDamageSources().generic(), 5.0F) && drone.isAlive());
        check(report, "un second clic le rappelle : entité retirée, gadget libéré", () -> {
            GadgetItems.DRONE_MINIER.use(world, player, Hand.MAIN_HAND);
            return drone.isRemoved() && !gadget.contains(GadgetComponents.DRONE);
        });
        check(report, "un drone dont le maître n'est plus en ligne s'éteint au premier tick", () -> {
            MiningDroneEntity orphan = MiningDroneItem.deploy(world, player);
            orphan.tick();
            return orphan.isRemoved();
        });
        clear(player);
    }

    /** La dalle et son voisinage, avant et après tous les gadgets. */
    private static void scenarioMapUntouched(ServerWorld world, BlockPos base, Map<BlockPos, BlockState> before, List<String> report) {
        section(report, "Gadgets — la carte est intacte");
        Map<BlockPos, BlockState> after = snapshot(world, base);
        List<BlockPos> changed = new ArrayList<>();
        before.forEach((pos, state) -> {
            if (!state.equals(after.get(pos))) {
                changed.add(pos);
            }
        });
        check(report, before.size() + " blocs autour de la dalle : aucun n'a changé", () -> changed.isEmpty());
        if (!changed.isEmpty()) {
            report.add("      changés : " + changed.subList(0, Math.min(5, changed.size())));
        }
    }

    // ------------------------------------------------------------------

    private static Map<BlockPos, BlockState> snapshot(ServerWorld world, BlockPos base) {
        Map<BlockPos, BlockState> states = new HashMap<>();
        for (BlockPos pos : BlockPos.iterate(base.add(-5, -7, -5), base.add(5, 6, 5))) {
            states.put(pos.toImmutable(), world.getBlockState(pos));
        }
        return states;
    }

    private static HearthstoneItem hearthstone(boolean improved) {
        return (HearthstoneItem) (improved ? GadgetItems.PIERRE_DE_FOYER_AMELIOREE : GadgetItems.PIERRE_DE_FOYER);
    }

    private static boolean has(AttributeModifiersComponent modifiers,
                               net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attribute,
                               AttributeModifierSlot slot) {
        return modifiers.modifiers().stream().anyMatch(entry -> entry.attribute().equals(attribute) && entry.slot() == slot);
    }

    private static String key(Text text) {
        return text.getContent() instanceof net.minecraft.text.TranslatableTextContent translatable ? translatable.getKey() : "";
    }

    private static void place(ServerPlayerEntity player, BlockPos pos) {
        player.stopUsingItem();
        player.refreshPositionAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
        player.setVelocity(Vec3d.ZERO);
    }

    private static void giveIngredients(ServerPlayerEntity player, CraftRecipe recipe) {
        for (CraftRecipe.Ingredient ingredient : recipe.ingredients()) {
            Identifier id = ingredient.item().orElse(null);
            if (id != null && Registries.ITEM.containsId(id)) {
                give(player, Registries.ITEM.get(id), ingredient.count());
            }
        }
    }

    private static void give(ServerPlayerEntity player, Item item, int amount) {
        int remaining = amount;
        while (remaining > 0) {
            int size = Math.min(remaining, item.getMaxCount());
            player.getInventory().insertStack(new ItemStack(item, size));
            remaining -= size;
        }
    }

    private static void clear(ServerPlayerEntity player) {
        player.stopUsingItem();
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            player.getInventory().setStack(slot, ItemStack.EMPTY);
        }
        player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
    }

    private static void section(List<String> report, String title) {
        report.add("--- " + title);
    }

    private static void check(List<String> report, String label, Check condition) {
        boolean ok;
        try {
            ok = condition.test();
        } catch (Exception e) {
            StackTraceElement[] trace = e.getStackTrace();
            report.add("  FAIL  " + label + "  (" + e + (trace.length > 0 ? " — " + trace[0] : "") + ")");
            return;
        }
        report.add((ok ? "  PASS  " : "  FAIL  ") + label);
    }

    @FunctionalInterface
    private interface Check {
        boolean test() throws Exception;
    }
}
