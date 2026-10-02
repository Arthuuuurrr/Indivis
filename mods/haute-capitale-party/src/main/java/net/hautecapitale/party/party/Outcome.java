package net.hautecapitale.party.party;

import net.minecraft.text.Text;

/**
 * Resultat d'une operation de groupe.
 *
 * <p>Un echec porte toujours sa raison, redigee pour le joueur. Rien ne renvoie
 * un booleen nu : l'appelant n'aurait alors rien a afficher, et c'est ainsi qu'on
 * obtient des commandes qui echouent en silence.
 */
public record Outcome(boolean ok, Text message) {

    public static Outcome ok(Text message) {
        return new Outcome(true, message);
    }

    public static Outcome fail(Text message) {
        return new Outcome(false, message);
    }
}
