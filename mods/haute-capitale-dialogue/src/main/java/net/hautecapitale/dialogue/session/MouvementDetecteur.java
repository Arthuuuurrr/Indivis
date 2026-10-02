package net.hautecapitale.dialogue.session;

/**
 * Un véhicule est-il en mouvement ? Avec une hystérésis.
 *
 * <p>En mouvement dès le premier écart au-dessus du seuil — un ferry qui part
 * doit rendre la caméra tout de suite. À l'arrêt seulement après un temps
 * d'immobilité — un dirigeable qui tangue à quai, une charrette qui s'arrête
 * en deux temps, ne doivent pas faire clignoter le cadrage.
 *
 * <p>Fonction pure, sans entité : vérifiable hors jeu.
 */
public final class MouvementDetecteur {

    /** L'état courant : en mouvement ou non, et depuis combien de ticks c'est calme. */
    public record Etat(boolean enMouvement, int ticksImmobile) {
        public static final Etat ARRET = new Etat(false, 0);
        public static final Etat EN_ROUTE = new Etat(true, 0);
    }

    private MouvementDetecteur() {
    }

    /**
     * @param courant      l'état précédent
     * @param vitesse      vitesse mesurée depuis le dernier contrôle, en blocs par tick
     * @param seuil        au-delà : en mouvement
     * @param ticksArret   immobilité requise, en ticks, pour revenir à l'arrêt
     * @param ticksEcoules ticks depuis le dernier contrôle
     */
    public static Etat suivant(Etat courant, double vitesse, double seuil, int ticksArret, int ticksEcoules) {
        if (vitesse > seuil) {
            return Etat.EN_ROUTE;
        }
        if (!courant.enMouvement()) {
            return Etat.ARRET;
        }
        int immobile = courant.ticksImmobile() + Math.max(1, ticksEcoules);
        return immobile >= ticksArret ? Etat.ARRET : new Etat(true, immobile);
    }
}
