package net.hautecapitale.metiers.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.npc.EasyNpcBridge;
import net.hautecapitale.metiers.npc.NpcRole;
import net.hautecapitale.metiers.npc.RoleGate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Map;

/**
 * Commandes liées aux rôles de PNJ.
 *
 * <p>{@code /metiers atelier} est le repli prévu au cas où l'API d'Easy NPC
 * changerait : il ouvre exactement le même écran, par le même chemin, sans
 * passer par un PNJ. C'est aussi le moyen le plus rapide de vérifier un rôle
 * qu'on vient d'écrire, sans avoir à poser un personnage.
 */
public final class RoleCommands {

    private static final SuggestionProvider<ServerCommandSource> ROLES =
            (context, builder) -> CommandSource.suggestIdentifiers(HcmData.ROLES.ids(), builder);

    private RoleCommands() {
    }

    /** {@code /metiers atelier <role> [<joueur>]} */
    public static LiteralArgumentBuilder<ServerCommandSource> atelier() {
        return CommandManager.literal("atelier")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("role", IdentifierArgumentType.identifier())
                        .suggests(ROLES)
                        .executes(ctx -> open(ctx, ctx.getSource().getPlayerOrThrow()))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(ctx -> open(ctx, EntityArgumentType.getPlayer(ctx, "joueur")))));
    }

    /** Le sous-arbre {@code /metiers inspecter role…}, greffé par {@link DataCommands}. */
    public static LiteralArgumentBuilder<ServerCommandSource> inspectRoles() {
        return CommandManager.literal("roles")
                .executes(ctx -> listRoles(ctx.getSource()))
                .then(CommandManager.argument("role", IdentifierArgumentType.identifier())
                        .suggests(ROLES)
                        .executes(ctx -> showRole(ctx.getSource(),
                                IdentifierArgumentType.getIdentifier(ctx, "role"))));
    }

    // ------------------------------------------------------------------

    private static int open(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player)
            throws CommandSyntaxException {
        Identifier roleId = IdentifierArgumentType.getIdentifier(ctx, "role");
        boolean opened = RoleGate.open(player, roleId, null);
        if (!opened) {
            ctx.getSource().sendError(Text.literal("Rôle inconnu : " + roleId
                    + " — voir « /metiers inspecter roles »."));
            return 0;
        }
        ctx.getSource().sendFeedback(() -> Text.literal(
                "Interface « " + roleId + " » ouverte pour " + player.getName().getString() + ".")
                .formatted(Formatting.GREEN), false);
        return 1;
    }

    private static int listRoles(ServerCommandSource source) {
        Map<Identifier, NpcRole> roles = HcmData.ROLES.all();
        source.sendFeedback(() -> Text.literal(roles.size() + " rôle(s) de PNJ :")
                .formatted(Formatting.GOLD), false);

        roles.forEach((id, role) -> {
            String metier = role.profession().map(profession -> ", métier " + profession.getId()).orElse("");
            source.sendFeedback(() -> Text.literal(
                    "  " + id + " — interface " + role.screen().asString() + metier)
                    .formatted(Formatting.WHITE), false);
        });

        source.sendFeedback(() -> Text.literal(EasyNpcBridge.isActive()
                ? "  Action Easy NPC active : « " + EasyNpcBridge.ACTION + " <role> »"
                : "  Easy NPC inactif — seules les commandes ouvrent les interfaces.")
                .formatted(EasyNpcBridge.isActive() ? Formatting.GRAY : Formatting.YELLOW), false);

        return roles.size();
    }

    private static int showRole(ServerCommandSource source, Identifier id) {
        NpcRole role = HcmData.ROLES.get(id);
        if (role == null) {
            source.sendError(Text.literal("Rôle inconnu : " + id
                    + " — voir « /metiers inspecter roles »."));
            return 0;
        }

        source.sendFeedback(() -> Text.literal(id.toString()).formatted(Formatting.GOLD), false);
        field(source, "Interface", role.screen().asString());
        field(source, "Métier", role.profession().map(p -> p.getId()).orElse("— (tous)"));
        field(source, "Titre", role.title().orElse("— (déduit du métier)"));
        field(source, "Accueil", role.greeting().orElse("—"));
        field(source, "Enseigne", role.canTeach() ? "oui" : "non");
        field(source, "Action PNJ", EasyNpcBridge.ACTION + " "
                + (id.getNamespace().equals("haute_capitale_metiers") ? id.getPath() : id.toString()));
        role.comment().ifPresent(comment -> field(source, "Note", comment));
        return 1;
    }

    private static void field(ServerCommandSource source, String label, String value) {
        source.sendFeedback(() -> Text.literal(String.format(java.util.Locale.ROOT, "  %-12s ", label))
                .formatted(Formatting.GRAY)
                .append(Text.literal(value).formatted(Formatting.WHITE)), false);
    }
}
