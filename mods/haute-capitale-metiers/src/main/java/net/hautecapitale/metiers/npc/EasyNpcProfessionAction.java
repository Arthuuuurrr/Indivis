package net.hautecapitale.metiers.npc;

import de.markusbordihn.easynpc.api.action.ActionRegistry;
import de.markusbordihn.easynpc.data.action.ActionContext;
import de.markusbordihn.easynpc.data.action.ActionDataEntry;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * L'action {@code haute_capitale_metiers:profession <role>} d'Easy NPC.
 *
 * <p>Seule classe du mod qui connaisse Easy NPC. Elle n'est chargée que si le
 * mod est présent — voir {@link EasyNpcBridge}.
 *
 * <p>Elle ne fait rien d'autre que traduire un clic sur un PNJ en une ouverture
 * d'écran : toute la logique de métier vit derrière {@link RoleGate}, partagée
 * avec la commande de repli.
 */
final class EasyNpcProfessionAction {

    private EasyNpcProfessionAction() {
    }

    static void register() {
        ActionRegistry.register(EasyNpcBridge.ACTION, new Executor());
    }

    /**
     * Easy NPC attrape les exceptions de nos actions et les journalise, mais un
     * PNJ muet reste incompréhensible pour le joueur : on préfère lui dire
     * nous-mêmes ce qui ne va pas.
     */
    private static final class Executor implements de.markusbordihn.easynpc.api.action.CustomActionExecutor {

        @Override
        @SuppressWarnings("deprecation") // méthode abstraite héritée, remplacée par la variante à contexte
        public void execute(ActionDataEntry entry, EasyNPC<?> npc,
                            ServerPlayerEntity player, List<String> arguments) {
            open(npc, player, arguments);
        }

        @Override
        public void execute(ActionDataEntry entry, EasyNPC<?> npc,
                            List<String> arguments, ActionContext context) {
            open(npc, context.initiator(), arguments);
        }

        private void open(EasyNPC<?> npc, ServerPlayerEntity player, List<String> arguments) {
            if (player == null) {
                // Action déclenchée sans joueur — un intervalle, une mort. Il n'y
                // a personne à qui montrer un écran.
                return;
            }

            if (arguments.isEmpty()) {
                complain(player, "L'action de ce personnage n'indique aucun rôle. "
                        + "Attendu : « " + EasyNpcBridge.ACTION + " forgeron ».");
                return;
            }

            Identifier roleId = parseRole(arguments.get(0));
            if (roleId == null) {
                complain(player, "Le rôle « " + arguments.get(0)
                        + " » n'est pas un identifiant valide.");
                return;
            }

            RoleGate.open(player, roleId, anchorOf(npc, player), nameOf(npc));
        }

        /** Le nom affiché du PNJ — celui que l'administrateur lui a donné —, ou {@code null}. */
        private static String nameOf(EasyNPC<?> npc) {
            Entity entity = npc == null ? null : npc.getEntity();
            return entity == null || !entity.hasCustomName() ? null : entity.getCustomName().getString();
        }

        /**
         * Un rôle s'écrit {@code forgeron} — le mod complète le domaine — ou
         * {@code un_autre_mod:forgeron} pour un rôle venu d'ailleurs.
         */
        private static Identifier parseRole(String argument) {
            return argument.indexOf(':') < 0
                    ? Identifier.tryParse(HauteCapitaleMetiers.MOD_ID + ":" + argument)
                    : Identifier.tryParse(argument);
        }

        /** Où se tient le PNJ, pour que l'écran se ferme si le joueur s'éloigne. */
        private static Vec3d anchorOf(EasyNPC<?> npc, ServerPlayerEntity player) {
            Entity entity = npc == null ? null : npc.getEntity();
            return entity == null ? player.getEntityPos() : entity.getEntityPos();
        }

        private static void complain(ServerPlayerEntity player, String message) {
            player.sendMessage(Text.literal(message).formatted(Formatting.RED), false);
        }
    }
}
