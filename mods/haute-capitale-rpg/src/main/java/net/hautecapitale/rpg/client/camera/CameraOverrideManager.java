package net.hautecapitale.rpg.client.camera;

import net.hautecapitale.rpg.HauteCapitaleRpg;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * L'autorité unique sur la suspension de la caméra RPG.
 *
 * <p>Un système de cinématiques appelle {@link #enterCinematicMode(String)} quand
 * son plan commence et {@link #exitCinematicMode(String)} quand il se termine.
 * Entre les deux, la caméra RPG ne touche plus à la caméra du jeu : ni offset, ni
 * zoom, ni collision, ni recentrage, ni interpolation, ni bascule de perspective.
 *
 * <p><b>Pourquoi un compteur et pas un booléen.</b> Une cinématique de boss
 * n'ouvre pas un plan, elle en enchaîne plusieurs sur la même cible : masquage de
 * l'ATH, puis caméra, puis bandes noires, chacun avec son propre début et sa
 * propre fin. Un booléen produirait un clignotement entrée/sortie à chaque
 * transition, donc un à-coup visible. Chaque raison est comptée séparément et
 * l'état ne redescend qu'à la dernière fermeture.
 *
 * <p>Toutes les méthodes sont à appeler depuis le fil client. Elles sont sûres à
 * appeler en double : fermer une raison inconnue ne fait rien.
 */
public final class CameraOverrideManager {

    /** Raison → nombre d'ouvertures encore actives. */
    private static final Map<String, Integer> RAISONS = new LinkedHashMap<>();

    /** Horodatage de la dernière activité, pour le garde-fou. */
    private static long derniereActiviteNanos = 0L;

    private CameraOverrideManager() {
    }

    /**
     * Suspend la caméra RPG.
     *
     * @param raison identifiant court et stable de l'appelant, par exemple
     *               {@code "bossesrise:camera"}. Sert au comptage, au garde-fou et
     *               à l'affichage de debug.
     */
    public static void enterCinematicMode(String raison) {
        String cle = normaliser(raison);
        boolean premiere = RAISONS.isEmpty();
        RAISONS.merge(cle, 1, Integer::sum);
        derniereActiviteNanos = System.nanoTime();
        if (premiere) {
            RpgCameraManager.onCinematicEnter();
            HauteCapitaleRpg.LOGGER.debug("Camera RPG suspendue ({}).", cle);
        }
    }

    /**
     * Rend la caméra RPG au gameplay, si plus aucune raison ne la retient.
     *
     * @param raison la même chaîne que celle passée à
     *               {@link #enterCinematicMode(String)}
     */
    public static void exitCinematicMode(String raison) {
        String cle = normaliser(raison);
        Integer restant = RAISONS.get(cle);
        if (restant == null) {
            return;
        }
        if (restant <= 1) {
            RAISONS.remove(cle);
        } else {
            RAISONS.put(cle, restant - 1);
        }
        derniereActiviteNanos = System.nanoTime();
        if (RAISONS.isEmpty()) {
            RpgCameraManager.onCinematicExit();
            HauteCapitaleRpg.LOGGER.debug("Camera RPG rendue au gameplay ({}).", cle);
        }
    }

    /**
     * Rafraîchit le garde-fou sans changer le comptage.
     *
     * <p>Appelé par la sonde tant qu'elle voit une cinématique réellement en
     * cours : une cinématique longue mais vivante ne doit pas être coupée.
     */
    public static void refresh() {
        derniereActiviteNanos = System.nanoTime();
    }

    /** Ferme toutes les raisons d'un coup. Changement de monde, déconnexion, mort. */
    public static void clear(String motif) {
        if (RAISONS.isEmpty()) {
            return;
        }
        RAISONS.clear();
        RpgCameraManager.onCinematicExit();
        HauteCapitaleRpg.LOGGER.debug("Camera RPG liberee ({}).", motif);
    }

    public static boolean isCinematic() {
        return !RAISONS.isEmpty();
    }

    /** Les raisons actives, pour l'overlay de debug. */
    public static String raisonsAffichees() {
        if (RAISONS.isEmpty()) {
            return "—";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : RAISONS.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(e.getKey());
            if (e.getValue() > 1) {
                sb.append(" ×").append(e.getValue());
            }
        }
        return sb.toString();
    }

    /** Nombre total d'ouvertures en cours. */
    public static int profondeur() {
        int total = 0;
        for (int n : RAISONS.values()) {
            total += n;
        }
        return total;
    }

    /**
     * Garde-fou : libère une suspension qui ne se termine jamais.
     *
     * <p>Ce n'est pas de la paranoïa. Le code de Bosses'Rise le documente : un boss
     * qui sort du champ cesse d'être animé, et une cinématique qui cesse d'être
     * animée n'atteint jamais son keyframe de fin. Sans ce filet, le joueur
     * resterait en caméra vanilla jusqu'à sa prochaine reconnexion, sans rien
     * comprendre.
     */
    static void tickWatchdog() {
        int plafond = CameraSettings.get().watchdog_cinematique_s;
        if (plafond <= 0 || RAISONS.isEmpty()) {
            return;
        }
        long ecoule = System.nanoTime() - derniereActiviteNanos;
        if (ecoule > plafond * 1_000_000_000L) {
            HauteCapitaleRpg.LOGGER.warn(
                    "Cinematique sans fin depuis {} s ({}) — camera RPG rendue au gameplay par securite.",
                    plafond, raisonsAffichees());
            clear("garde-fou");
        }
    }

    private static String normaliser(String raison) {
        return (raison == null || raison.isBlank()) ? "inconnu" : raison.trim();
    }
}
