package net.hautecapitale.dialogue.session;

/** Pourquoi une session s'est fermée. Dans le journal, dans le paquet, dans l'overlay. */
public enum CloseReason {
    /** Fermeture normale, sans précision. */
    FERMEE,
    /** L'écran de dialogue a été refermé (dernier bouton, action CLOSE_DIALOG). */
    ECRAN_FERME,
    /** Le joueur a appuyé sur Échap. */
    ESC,
    /** Le joueur a choisi une réponse de congé. */
    ADIEU,
    /** Joueur et personnage trop éloignés. */
    DISTANCE,
    /** Le personnage est mort, déchargé ou retiré. */
    PNJ_DISPARU,
    /** Le joueur est mort. */
    MORT,
    /** Le joueur a été téléporté. */
    TELEPORTATION,
    /** Le joueur a changé de dimension. */
    DIMENSION,
    /** Une cinématique a pris la caméra. */
    CINEMATIQUE,
    /** Plus rien ne s'est passé pendant trop longtemps. */
    INACTIVITE,
    /** Un administrateur a fermé la session. */
    ADMIN,
    /** Le joueur s'est déconnecté. */
    DECONNEXION,
    /** Une autre session a pris la place. */
    REMPLACEE,
    /** Le serveur a refusé l'ouverture. */
    REFUSEE,
    /** Le véhicule du personnage s'est mis en route et la conversation n'y est pas permise. */
    MOUVEMENT
}
