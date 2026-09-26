package net.hautecapitale.rpg.content;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.hautecapitale.rpg.api.Classes;
import net.hautecapitale.rpg.config.RpgConfig;
import net.hautecapitale.rpg.rpgclass.RpgClass;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.util.Set;

/**
 * Verrou d'équipement par classe.
 *
 * <p>Deux mécanismes, parce que porter et frapper ne se contrôlent pas de la
 * même façon :
 *
 * <ul>
 *   <li><b>Armure</b> — la pièce interdite est retirée et rendue à l'inventaire.
 *       Les emplacements d'armure sont distincts de l'inventaire principal, donc
 *       la pièce rendue n'y remonte pas toute seule.</li>
 *   <li><b>Arme</b> — l'attaque est refusée, l'arme reste en main. Arracher une
 *       arme ne marche pas : la barre d'action <i>fait partie</i> de l'inventaire,
 *       si bien qu'on la rend dans la case même qu'on vient de vider et qu'elle
 *       revient aussitôt en main. Refuser le coup atteint le but — l'arme est
 *       inutilisable — sans se battre contre l'inventaire du joueur.</li>
 * </ul>
 *
 * <p><b>Désactivé par défaut.</b> Installer le noyau sur un serveur en cours ne
 * change donc rien tant que l'administrateur ne l'a pas décidé.
 *
 * <p>Aucun mixin. L'armure passe par le tick serveur à intervalle réglable —
 * quatre comparaisons de namespace par joueur et par seconde ; l'arme passe par
 * un événement Fabric, donc ne coûte rien tant que personne ne frappe.
 *
 * <p>Limite assumée à ce stade : le lancement de sorts passe par Spell Engine et
 * n'est pas intercepté ici. Un bâton d'une autre classe ne peut pas frapper, mais
 * il peut encore lancer ses sorts. C'est le sujet du palier suivant.
 */
public final class EquipmentLock {

    private static final EquipmentSlot[] ARMURE = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static int compteur;

    private EquipmentLock() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            RpgConfig config = RpgConfig.get();
            if (!config.verrou_equipement) {
                return;
            }
            int intervalle = Math.max(1, config.intervalle_verrou_ticks);
            if (++compteur < intervalle) {
                return;
            }
            compteur = 0;
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                verifierArmure(player);
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!RpgConfig.get().verrou_equipement || world.isClient()) {
                return ActionResult.PASS;
            }
            ItemStack arme = player.getStackInHand(hand);
            RpgClass rpgClass = Classes.of(player).orElse(null);
            if (ClassContent.allowed(arme, rpgClass)) {
                return ActionResult.PASS;
            }
            avertir(player, arme);
            return ActionResult.FAIL;
        });
    }

    // MARK: armure

    private static void verifierArmure(ServerPlayerEntity player) {
        RpgClass rpgClass = Classes.of(player).orElse(null);
        for (EquipmentSlot slot : ARMURE) {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack.isEmpty() || ClassContent.allowed(stack, rpgClass)) {
                continue;
            }
            // On rend la pièce avant de vider l'emplacement : si l'inventaire est
            // plein, elle tombe au sol plutôt que de disparaître.
            ItemStack retire = stack.copy();
            player.equipStack(slot, ItemStack.EMPTY);
            if (!player.getInventory().insertStack(retire)) {
                player.dropItem(retire, false);
            }
            avertir(player, retire);
        }
    }

    // MARK: message

    private static void avertir(net.minecraft.entity.player.PlayerEntity player, ItemStack stack) {
        Set<RpgClass> owners = ClassContent.owners(stack);
        String classes = owners.stream()
                .map(RpgClass::getId)
                .sorted()
                .reduce((a, b) -> a + " ou " + b)
                .orElse("une autre classe");

        player.sendMessage(Text.literal("Réservé à ")
                .append(Text.literal(classes).formatted(Formatting.GOLD))
                .formatted(Formatting.GRAY), true);
    }
}
