package net.hautecapitale.dialogue.guard;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

/**
 * Ce qu'un joueur en conversation ne peut pas faire, quoi que dise son client.
 *
 * <p>Le client honnête n'envoie déjà rien : un écran ouvert coupe attaques,
 * usages et sorts. Ces gardes existent pour le client qui ne l'est pas, et
 * pour les chemins qui ne passent pas par les touches — le paquet d'attaque de
 * Better Combat, par exemple, aboutit à {@code player.attack()} et donc ici, à
 * la vérification des dégâts.
 *
 * <p>Toutes côté serveur : sur le client, {@code world.isClient()} fait sortir
 * chaque garde à sa première instruction.
 */
public final class DialogueGuards {

    private DialogueGuards() {
    }

    public static void init() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((victime, source, montant) ->
                !(source.getAttacker() instanceof ServerPlayerEntity attaquant && DialogueManager.enSession(attaquant)));

        AttackEntityCallback.EVENT.register((joueur, monde, main, cible, hit) ->
                bloque(joueur) ? ActionResult.FAIL : ActionResult.PASS);

        UseBlockCallback.EVENT.register((joueur, monde, main, hit) ->
                bloque(joueur) ? ActionResult.FAIL : ActionResult.PASS);

        UseItemCallback.EVENT.register((joueur, monde, main) ->
                bloque(joueur) ? ActionResult.FAIL : ActionResult.PASS);

        // Le personnage de la conversation reste cliquable : c'est ce clic qui
        // ouvre — et rafraîchit — une session de capture, dans la même phase.
        UseEntityCallback.EVENT.register((joueur, monde, main, cible, hit) ->
                bloque(joueur) && !estLePersonnage(joueur, cible) ? ActionResult.FAIL : ActionResult.PASS);
    }

    private static boolean bloque(PlayerEntity joueur) {
        return !joueur.getEntityWorld().isClient() && DialogueManager.enSession(joueur);
    }

    private static boolean estLePersonnage(PlayerEntity joueur, Entity cible) {
        return cible != null && DialogueManager.session(joueur).map(s -> s.pnj.equals(cible.getUuid())).orElse(false);
    }
}
