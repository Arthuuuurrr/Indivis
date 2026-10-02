package net.hautecapitale.metiers.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.CraftEngine;
import net.hautecapitale.metiers.craft.CraftFeedback;
import net.hautecapitale.metiers.craft.CraftRecipe;
import net.hautecapitale.metiers.craft.Mastery;
import net.hautecapitale.metiers.craft.MasteryAttachment;
import net.hautecapitale.metiers.craft.XpFalloff;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.npc.RoleGate;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Map;

/**
 * Commandes autour des recettes.
 *
 * <p>{@code /metiers fabriquer} fait exactement ce que ferait le clic dans
 * l'atelier — même moteur, mêmes vérifications, même message — sans l'écran.
 * C'est l'outil de test du serveur, et le moyen pour un administrateur de
 * vérifier une recette qu'il vient d'écrire.
 */
public final class RecipeCommands {

    private static final SuggestionProvider<ServerCommandSource> RECIPES =
            (context, builder) -> CommandSource.suggestIdentifiers(HcmData.RECIPES.ids(), builder);

    private static final SuggestionProvider<ServerCommandSource> PROFESSIONS =
            (context, builder) -> CommandSource.suggestMatching(Profession.ids(), builder);

    private RecipeCommands() {
    }

    /** {@code /metiers fabriquer <joueur> <recette> [<fois>]} */
    public static LiteralArgumentBuilder<ServerCommandSource> fabriquer() {
        return CommandManager.literal("fabriquer")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("joueur", EntityArgumentType.player())
                        .then(CommandManager.argument("recette", IdentifierArgumentType.identifier())
                                .suggests(RECIPES)
                                .executes(ctx -> craft(ctx, 1))
                                .then(CommandManager.argument("fois", IntegerArgumentType.integer(1, 10))
                                        .executes(ctx -> craft(ctx, IntegerArgumentType.getInteger(ctx, "fois"))))));
    }

    /** Le sous-arbre {@code /metiers inspecter recettes…}, greffé par {@link DataCommands}. */
    public static LiteralArgumentBuilder<ServerCommandSource> inspectRecipes() {
        return CommandManager.literal("recettes")
                .executes(ctx -> list(ctx.getSource(), null))
                .then(CommandManager.argument("metier", StringArgumentType.word())
                        .suggests(PROFESSIONS)
                        .executes(ctx -> list(ctx.getSource(), StringArgumentType.getString(ctx, "metier"))));
    }

    /** {@code /metiers inspecter recette <id>} */
    public static LiteralArgumentBuilder<ServerCommandSource> inspectRecipe() {
        return CommandManager.literal("recette")
                .then(CommandManager.argument("id", IdentifierArgumentType.identifier())
                        .suggests(RECIPES)
                        .executes(ctx -> show(ctx.getSource(),
                                IdentifierArgumentType.getIdentifier(ctx, "id"))));
    }

    // ------------------------------------------------------------------

    private static int craft(CommandContext<ServerCommandSource> ctx, int times) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "recette");
        CraftRecipe recipe = HcmData.RECIPES.get(id);
        if (recipe == null) {
            ctx.getSource().sendError(Text.literal("Recette inconnue : " + id
                    + " — voir « /metiers inspecter recettes »."));
            return 0;
        }

        CraftEngine.Outcome outcome = CraftEngine.craft(player, id, times);
        Text feedback = CraftFeedback.describe(recipe, outcome);
        RoleGate.tell(player, feedback);
        RoleGate.refresh(player);

        ServerCommandSource source = ctx.getSource();
        if (source.getPlayer() != player) {
            source.sendFeedback(() -> Text.literal(player.getName().getString() + " : ").append(feedback), false);
        }
        return outcome.crafted();
    }

    private static int list(ServerCommandSource source, String professionId) {
        Profession wanted = null;
        if (professionId != null) {
            wanted = Profession.byId(professionId);
            if (wanted == null) {
                source.sendError(Text.literal("Métier inconnu : " + professionId));
                return 0;
            }
        }

        int shown = 0;
        String scope = wanted == null ? "" : " du métier " + wanted.getId();
        source.sendFeedback(() -> Text.literal("Recettes" + scope + " :").formatted(Formatting.GOLD), false);

        for (Map.Entry<Identifier, CraftRecipe> entry : HcmData.RECIPES.all().entrySet()) {
            CraftRecipe recipe = entry.getValue();
            if (wanted != null && recipe.profession() != wanted) {
                continue;
            }
            shown++;
            String emptyTags = emptyFamilies(recipe);
            source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                    "  %s — %s niv. %d, %.0f XP, %d M%s",
                    entry.getKey(), recipe.profession().getId(), recipe.level(), recipe.xp(), recipe.cost(),
                    emptyTags.isEmpty() ? "" : "  [familles vides : " + emptyTags + "]"))
                    .formatted(emptyTags.isEmpty() ? Formatting.WHITE : Formatting.YELLOW), false);
        }

        int total = shown;
        source.sendFeedback(() -> Text.literal("  " + total + " recette(s)").formatted(Formatting.GRAY), false);
        return shown;
    }

    private static int show(ServerCommandSource source, Identifier id) {
        CraftRecipe recipe = HcmData.RECIPES.get(id);
        if (recipe == null) {
            source.sendError(Text.literal("Recette inconnue : " + id));
            return 0;
        }

        source.sendFeedback(() -> Text.literal(id.toString()).formatted(Formatting.GOLD), false);
        recipe.title().ifPresent(title -> field(source, "Titre", title));
        field(source, "Métier", recipe.profession().getId() + ", niveau " + recipe.level());
        field(source, "XP", String.format(Locale.ROOT, "%.0f à niveau égal", recipe.xp()));
        field(source, "Prix", recipe.cost() == 0 ? "gratuit" : recipe.cost() + " " + MetiersConfig.get().monnaie);
        for (CraftRecipe.Ingredient ingredient : recipe.ingredients()) {
            String members = ingredient.tag().map(tag -> {
                StringBuilder names = new StringBuilder();
                Registries.ITEM.iterateEntries(tag).forEach(member -> {
                    if (!names.isEmpty()) {
                        names.append(", ");
                    }
                    names.append(Registries.ITEM.getId(member.value()));
                });
                return names.isEmpty() ? " — famille VIDE dans cette installation" : " — " + names;
            }).orElse("");
            field(source, "Ingrédient", "×" + ingredient.count() + " " + ingredient.describeSource() + members);
        }
        field(source, "Résultat", "×" + recipe.result().count() + " " + recipe.result().item());
        recipe.comment().ifPresent(comment -> field(source, "Note", comment));

        // La décote, palier par palier, pour cette recette.
        StringBuilder falloff = new StringBuilder();
        for (MetiersConfig.Palier palier : MetiersConfig.get().decote) {
            falloff.append("≤+").append(palier.ecart).append(" : ").append(palier.pourcent).append(" %  ");
        }
        field(source, "Décote", falloff + "au-delà : 0 %");

        if (source.getPlayer() != null) {
            ServerPlayerEntity player = source.getPlayer();
            int crafts = MasteryAttachment.crafts(player, id);
            field(source, "Vous", String.format(Locale.ROOT, "%d fabrication(s), %s, %s",
                    crafts, Mastery.of(crafts).name().toLowerCase(Locale.ROOT),
                    Metiers.hasProfession(player, recipe.profession())
                            ? "XP à votre niveau : " + XpFalloff.describe(
                                    Metiers.getLevel(player, recipe.profession()), recipe.level())
                            : "métier non appris"));
        }
        return 1;
    }

    /** Les familles de cette recette qui n'ont aucun objet ici — la recette serait infabricable. */
    private static String emptyFamilies(CraftRecipe recipe) {
        StringBuilder empty = new StringBuilder();
        for (CraftRecipe.Ingredient ingredient : recipe.ingredients()) {
            ingredient.tag().ifPresent(tag -> {
                if (!Registries.ITEM.iterateEntries(tag).iterator().hasNext()) {
                    if (!empty.isEmpty()) {
                        empty.append(", ");
                    }
                    empty.append('#').append(tag.id().getPath());
                }
            });
        }
        return empty.toString();
    }

    private static void field(ServerCommandSource source, String label, String value) {
        source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT, "  %-12s ", label))
                .formatted(Formatting.GRAY)
                .append(Text.literal(value).formatted(Formatting.WHITE)), false);
    }
}
