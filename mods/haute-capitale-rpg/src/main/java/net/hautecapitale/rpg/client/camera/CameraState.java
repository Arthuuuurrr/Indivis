package net.hautecapitale.rpg.client.camera;

/**
 * Les états de la caméra RPG.
 *
 * <p>Priorité, du plus fort au plus faible : {@link #CINEMATIC_OVERRIDE} —
 * une cinématique tient la caméra et personne ne discute —, puis
 * {@link #NPC_DIALOGUE} — un dialogue cadre un personnage —, puis
 * {@link #GAMEPLAY_RPG}. {@link #DISABLED} vaut « vanilla strict » : le joueur a
 * coupé le module, mais un dialogue peut toujours prendre la caméra le temps
 * d'une conversation.
 */
public enum CameraState {

    /** La caméra RPG applique ses offsets, sa distance et sa collision. */
    GAMEPLAY_RPG("GAMEPLAY_RPG"),

    /**
     * Un dialogue tient la caméra : elle cadre un personnage, via
     * {@link CameraFocus}. Les offsets d'épaule et le zoom continuent d'être
     * calculés pour servir de point de départ et de retour, mais ne s'affichent
     * pas.
     */
    NPC_DIALOGUE("NPC_DIALOGUE"),

    /** Une cinématique tient la caméra. La caméra RPG ne touche plus à rien. */
    CINEMATIC_OVERRIDE("CINEMATIC_OVERRIDE"),

    /** Le joueur a coupé le module : comportement vanilla strict. */
    DISABLED("DISABLED");

    private final String affichage;

    CameraState(String affichage) {
        this.affichage = affichage;
    }

    /** Libellé stable, tel qu'attendu dans l'overlay de debug. */
    public String affichage() {
        return this.affichage;
    }
}
