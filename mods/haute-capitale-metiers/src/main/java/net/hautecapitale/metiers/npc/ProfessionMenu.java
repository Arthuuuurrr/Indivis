package net.hautecapitale.metiers.npc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

/**
 * L'écran de métier, côté serveur.
 *
 * <p>Un seul menu pour les neuf rôles : ils ne diffèrent que par les données
 * qu'on lui donne. Aucun emplacement d'inventaire pour l'instant — rien ne se
 * dépose ni ne se retire à cette étape ; la fabrication de l'étape 5 passera par
 * des boutons vérifiés côté serveur, pas par des objets glissés.
 *
 * <p>Le menu garde de quoi vérifier que le joueur est toujours en droit de le
 * voir : s'il s'éloigne du PNJ, Minecraft ferme l'écran tout seul. C'est la
 * raison d'être de {@code canUse} et c'est pour cela qu'on passe par un
 * {@link ScreenHandler} plutôt que par un simple paquet.
 */
public class ProfessionMenu extends ScreenHandler {

    /** À quelle distance du PNJ l'écran reste ouvert. */
    public static final double REACH = 8.0D;

    private final ProfessionScreenData data;
    private final Predicate<PlayerEntity> usable;

    /** Construction côté client : il n'a rien à vérifier, il affiche. */
    public ProfessionMenu(int syncId, PlayerInventory inventory, ProfessionScreenData data) {
        this(syncId, data, player -> true);
    }

    /** Construction côté serveur, ancrée sur le PNJ. */
    public ProfessionMenu(int syncId, ProfessionScreenData data, Vec3d anchor) {
        this(syncId, data, player -> player.isAlive()
                && player.squaredDistanceTo(anchor) <= REACH * REACH);
    }

    private ProfessionMenu(int syncId, ProfessionScreenData data, Predicate<PlayerEntity> usable) {
        super(HcmScreens.PROFESSION, syncId);
        this.data = data;
        this.usable = usable;
    }

    /** Construction côté serveur sans ancrage — le repli par commande. */
    public static ProfessionMenu anywhere(int syncId, ProfessionScreenData data) {
        return new ProfessionMenu(syncId, data, player -> player.isAlive());
    }

    public ProfessionScreenData data() {
        return data;
    }

    public Identifier role() {
        return data.role();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return usable.test(player);
    }

    /**
     * Aucun emplacement, donc rien à déplacer. Renvoyer une pile vide est la
     * réponse correcte : elle dit à Minecraft que le transfert n'a rien donné.
     */
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }
}
