package net.hautecapitale.metiers.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.hautecapitale.metiers.creature.CreatureEnums;
import net.hautecapitale.metiers.creature.CreatureProfile;
import net.hautecapitale.metiers.creature.DropEntry;
import net.hautecapitale.metiers.data.DataRegistry;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.data.LoadReport;
import net.hautecapitale.metiers.util.Vocabulary;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Commandes de lecture des données du MMO.
 *
 * <p>Le contenu vit dans des fichiers ; ces commandes disent ce que le serveur a
 * réellement retenu de ces fichiers. C'est la contrepartie indispensable du
 * pilotage par données : sans elles, une faute de frappe dans un datapack se
 * traduirait en jeu par « ça ne marche pas », sans explication.
 */
public final class DataCommands {

    private static final DynamicCommandExceptionType FICHE_INCONNUE =
            new DynamicCommandExceptionType(id -> Text.literal(
                    "Aucune fiche de créature pour " + id
                            + " — voir « /metiers inspecter liste »."));

    private static final SuggestionProvider<ServerCommandSource> CREATURES =
            (context, builder) -> CommandSource.suggestIdentifiers(HcmData.CREATURES.ids(), builder);

    private static final SuggestionProvider<ServerCommandSource> CATEGORIES =
            (context, builder) -> CommandSource.suggestMatching(
                    Arrays.stream(CreatureEnums.Category.values())
                            .map(CreatureEnums.Category::asString).toList(),
                    builder);

    private DataCommands() {
    }

    /** Le sous-arbre {@code /metiers inspecter …}, greffé par {@link MetiersCommands}. */
    public static LiteralArgumentBuilder<ServerCommandSource> inspecter() {
        return CommandManager.literal("inspecter")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))

                .then(CommandManager.literal("resume")
                        .executes(ctx -> summary(ctx.getSource())))

                .then(CommandManager.literal("erreurs")
                        .executes(ctx -> issues(ctx.getSource())))

                .then(CommandManager.literal("liste")
                        .executes(ctx -> list(ctx.getSource(), null))
                        .then(CommandManager.argument("categorie", StringArgumentType.word())
                                .suggests(CATEGORIES)
                                .executes(ctx -> list(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "categorie")))))

                .then(RoleCommands.inspectRoles())

                .then(RecipeCommands.inspectRecipes())

                .then(RecipeCommands.inspectRecipe())

                .then(NodeCommands.inspectTypes())

                .then(HuntCommands.inspectPrey())

                .then(SkinCommands.inspectSkinning())

                .then(CommandManager.literal("creature")
                        .then(CommandManager.argument("id", IdentifierArgumentType.identifier())
                                .suggests(CREATURES)
                                .executes(DataCommands::creature)));
    }

    // ------------------------------------------------------------------
    // Rechargement

    /**
     * Relit les datapacks comme le ferait {@code /reload}, puis rend compte de ce
     * que les registres du mod y ont trouvé.
     *
     * <p>On réutilise la commande de rechargement de Minecraft plutôt que
     * d'appeler {@code reloadResources} nous-mêmes : elle gère déjà l'échec du
     * rechargement, qui doit laisser le serveur sur les anciennes données plutôt
     * que dans un état à moitié chargé.
     */
    public static void reloadDataPacks(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        ResourcePackManager packs = server.getDataPackManager();
        Collection<String> enabled = enabledAfterScan(packs, server);

        HcmData.reportNextReloadTo(success -> {
            if (!success) {
                source.sendError(Text.literal(
                        "Rechargement des datapacks en échec — les données précédentes restent en place."));
                return;
            }
            summary(source);
        });

        ReloadCommand.tryReloadDataPacks(enabled, source);
    }

    /**
     * Les datapacks à activer après un nouveau balayage du dossier : ceux qui
     * l'étaient déjà, plus ceux qui viennent d'apparaître et que personne n'a
     * désactivés. Même règle que {@code /reload}, pour qu'un datapack déposé à la
     * main soit pris en compte sans redémarrer le serveur.
     */
    private static Collection<String> enabledAfterScan(ResourcePackManager packs, MinecraftServer server) {
        List<String> result = new ArrayList<>(packs.getEnabledIds());
        packs.scanPacks();
        DataPackSettings settings = server.getSaveProperties().getDataConfiguration().dataPacks();
        for (String id : packs.getIds()) {
            if (!settings.getDisabled().contains(id) && !result.contains(id)) {
                result.add(id);
            }
        }
        return result;
    }

    // ------------------------------------------------------------------
    // Affichage

    private static int summary(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("Données Haute Capitale — Métiers")
                .formatted(Formatting.GOLD), false);

        for (DataRegistry<?> registry : HcmData.ALL) {
            source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                    "  %-12s %d fiche(s)", registry.domain(), registry.size()))
                    .formatted(Formatting.WHITE), false);
            detail(source, registry);
        }

        List<LoadReport.Entry> problems = HcmData.allIssues();
        if (problems.isEmpty()) {
            source.sendFeedback(() -> Text.literal("  Chargement sans erreur ni avertissement.")
                    .formatted(Formatting.GREEN), false);
        } else {
            long errors = problems.stream()
                    .filter(entry -> entry.severity() == LoadReport.Severity.ERREUR).count();
            long warnings = problems.size() - errors;
            source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                    "  %d erreur(s), %d avertissement(s) — voir « /metiers inspecter erreurs »",
                    errors, warnings))
                    .formatted(errors > 0 ? Formatting.RED : Formatting.YELLOW), false);
        }

        return HcmData.ALL.stream().mapToInt(DataRegistry::size).sum();
    }

    /** Le détail propre à un domaine, juste sous sa ligne de compte. */
    private static void detail(ServerCommandSource source, DataRegistry<?> registry) {
        if (registry == HcmData.CREATURES) {
            long huntable = HcmData.CREATURES.all().values().stream()
                    .filter(CreatureProfile::isHuntable).count();
            long skinnable = HcmData.CREATURES.all().values().stream()
                    .filter(CreatureProfile::isSkinnable).count();
            source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                    "    dont %d chassable(s), %d dépeçable(s)", huntable, skinnable))
                    .formatted(Formatting.GRAY), false);

            Map<String, Integer> byCategory = new TreeMap<>();
            HcmData.CREATURES.all().values().forEach(profile ->
                    byCategory.merge(profile.category().asString(), 1, Integer::sum));
            if (!byCategory.isEmpty()) {
                source.sendFeedback(() -> Text.literal("    catégories : " + join(byCategory))
                        .formatted(Formatting.GRAY), false);
            }
        } else if (registry == HcmData.ROLES) {
            Map<String, Integer> byScreen = new TreeMap<>();
            HcmData.ROLES.all().values().forEach(role ->
                    byScreen.merge(role.screen().asString(), 1, Integer::sum));
            if (!byScreen.isEmpty()) {
                source.sendFeedback(() -> Text.literal("    interfaces : " + join(byScreen))
                        .formatted(Formatting.GRAY), false);
            }
        }
    }

    private static String join(Map<String, Integer> counts) {
        return counts.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    private static int issues(ServerCommandSource source) {
        List<LoadReport.Entry> problems = HcmData.allIssues();
        if (problems.isEmpty()) {
            source.sendFeedback(() -> Text.literal("Aucun problème au dernier chargement.")
                    .formatted(Formatting.GREEN), false);
            return 0;
        }
        source.sendFeedback(() -> Text.literal("Problèmes du dernier chargement :")
                .formatted(Formatting.GOLD), false);
        for (LoadReport.Entry entry : problems) {
            Formatting color = entry.severity() == LoadReport.Severity.ERREUR
                    ? Formatting.RED : Formatting.YELLOW;
            source.sendFeedback(() -> Text.literal("  " + entry).formatted(color), false);
        }
        return problems.size();
    }

    private static int list(ServerCommandSource source, String categoryFilter) {
        CreatureEnums.Category wanted = null;
        if (categoryFilter != null) {
            for (CreatureEnums.Category candidate : CreatureEnums.Category.values()) {
                if (candidate.asString().equalsIgnoreCase(categoryFilter)) {
                    wanted = candidate;
                    break;
                }
            }
            if (wanted == null) {
                source.sendError(Text.literal("Catégorie inconnue. Valeurs acceptées : "
                        + Vocabulary.accepted(CreatureEnums.Category.values())));
                return 0;
            }
        }

        List<Text> lines = new ArrayList<>();
        for (Map.Entry<Identifier, CreatureProfile> entry : HcmData.CREATURES.all().entrySet()) {
            CreatureProfile profile = entry.getValue();
            if (wanted != null && profile.category() != wanted) {
                continue;
            }
            StringBuilder detail = new StringBuilder();
            profile.hunter().ifPresent(hunter -> detail.append("chasse niv. ").append(hunter.level()));
            profile.skinning().ifPresent(skinning -> {
                if (detail.length() > 0) {
                    detail.append(", ");
                }
                detail.append("dépeçage niv. ").append(skinning.level());
            });
            if (detail.length() == 0) {
                detail.append("aucun métier");
            }
            lines.add(Text.literal("  " + entry.getKey() + " — " + detail).formatted(Formatting.WHITE));
        }

        String scope = wanted == null ? "" : " en catégorie « " + wanted.asString() + " »";
        source.sendFeedback(() -> Text.literal(lines.size() + " fiche(s) de créature" + scope + " :")
                .formatted(Formatting.GOLD), false);
        lines.forEach(line -> source.sendFeedback(() -> line, false));
        return lines.size();
    }

    private static int creature(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "id");
        CreatureProfile profile = HcmData.CREATURES.get(id);
        if (profile == null) {
            throw FICHE_INCONNUE.create(id);
        }
        ServerCommandSource source = ctx.getSource();

        MutableText title = Text.literal(id.toString()).formatted(Formatting.GOLD);
        // Le registre des entités a une valeur par défaut : interrogé sur un
        // identifiant inconnu il répond « cochon » plutôt que rien. Sans ce
        // containsId, une fiche de créature absente s'afficherait sous un faux nom.
        if (Registries.ENTITY_TYPE.containsId(id)) {
            EntityType<?> type = Registries.ENTITY_TYPE.get(id);
            title.append(Text.literal(" — ").formatted(Formatting.DARK_GRAY))
                    .append(type.getName().copy().formatted(Formatting.YELLOW));
        } else {
            title.append(Text.literal(" (type d'entité absent de cette installation)")
                    .formatted(Formatting.RED));
        }
        source.sendFeedback(() -> title, false);

        field(source, "Catégorie", CreatureEnums.pretty(profile.category().asString())
                + "  ·  rareté : " + CreatureEnums.pretty(profile.rarity().asString()));

        if (profile.hunter().isPresent()) {
            CreatureProfile.HunterEntry hunter = profile.hunter().get();
            field(source, "Chasseur", String.format(Locale.ROOT,
                    "niveau %d requis, %.0f XP", hunter.level(), hunter.xp()));
        } else {
            field(source, "Chasseur", "non chassable");
        }

        if (profile.skinning().isPresent()) {
            CreatureProfile.SkinningEntry skinning = profile.skinning().get();
            field(source, "Dépeceur", String.format(Locale.ROOT,
                    "niveau %d requis, %.0f XP, carcasse %d s",
                    skinning.level(), skinning.xp(), skinning.carcassSeconds()));
            field(source, "  matière", describe(skinning.material()));
            if (!skinning.secondary().isEmpty()) {
                field(source, "  secondaires", join(skinning.secondary()));
            }
        } else {
            field(source, "Dépeceur", "non dépeçable");
        }

        profile.meat().ifPresent(meat -> field(source, "Viande", describe(meat)));
        if (!profile.combatLoot().isEmpty()) {
            field(source, "Loot combat", join(profile.combatLoot()));
        }

        field(source, "XP si origine", profile.xpOrigins().stream()
                .map(CreatureEnums.SpawnOrigin::asString)
                .reduce((a, b) -> a + ", " + b).orElse("aucune"));

        profile.comment().ifPresent(comment -> field(source, "Note", comment));

        return profile.skinning().map(CreatureProfile.SkinningEntry::level)
                .orElseGet(() -> profile.hunter().map(CreatureProfile.HunterEntry::level).orElse(0));
    }

    private static void field(ServerCommandSource source, String label, String value) {
        source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT, "  %-14s ", label))
                .formatted(Formatting.GRAY)
                .append(Text.literal(value).formatted(Formatting.WHITE)), false);
    }

    /** Signale au passage les objets manquants : c'est à l'inspection qu'on les voit. */
    private static String describe(DropEntry entry) {
        String text = entry.describe();
        return Registries.ITEM.containsId(entry.item()) ? text : text + "  [objet absent]";
    }

    private static String join(List<DropEntry> entries) {
        return entries.stream().map(DataCommands::describe)
                .reduce((a, b) -> a + " · " + b).orElse("—");
    }
}
