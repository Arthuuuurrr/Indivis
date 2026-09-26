package net.hautecapitale.dialogue.session;

/** Les drapeaux d'une session, envoyés au client dans un seul entier. */
public final class SessionFlags {

    public static final int VERROU_JOUEUR = 1;
    public static final int AUTORISER_ESC = 1 << 1;
    public static final int MASQUER_HUD = 1 << 2;
    public static final int FORCER_TROISIEME_PERSONNE = 1 << 3;
    public static final int REPRISE_APRES_CINEMATIQUE = 1 << 4;
    public static final int EXCLUSIF = 1 << 5;
    /** Le personnage est porté par un véhicule en mouvement : texte seul, la caméra reste au joueur. */
    public static final int SIMPLIFIE = 1 << 6;
    /** Un commerce ou un atelier ouvert depuis le dialogue rend au dialogue quand il se ferme. */
    public static final int RETOUR_APRES_COMMERCE = 1 << 7;

    private SessionFlags() {
    }

    public static boolean a(int flags, int flag) {
        return (flags & flag) != 0;
    }

    /** Pour le journal et l'overlay de debug. */
    public static String decrire(int flags) {
        StringBuilder sb = new StringBuilder();
        ajouter(sb, flags, VERROU_JOUEUR, "verrou");
        ajouter(sb, flags, AUTORISER_ESC, "esc");
        ajouter(sb, flags, MASQUER_HUD, "hud");
        ajouter(sb, flags, FORCER_TROISIEME_PERSONNE, "3e");
        ajouter(sb, flags, REPRISE_APRES_CINEMATIQUE, "reprise");
        ajouter(sb, flags, EXCLUSIF, "exclusif");
        ajouter(sb, flags, SIMPLIFIE, "simplifie");
        ajouter(sb, flags, RETOUR_APRES_COMMERCE, "retour");
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private static void ajouter(StringBuilder sb, int flags, int flag, String nom) {
        if (a(flags, flag)) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(nom);
        }
    }
}
