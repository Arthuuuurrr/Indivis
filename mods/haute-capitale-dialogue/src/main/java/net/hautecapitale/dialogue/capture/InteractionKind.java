package net.hautecapitale.dialogue.capture;

/**
 * Ce qu'un clic droit sur un personnage déclenche, vu du serveur, avant que
 * quiconque n'y touche.
 *
 * <p>La capture n'a de sens que pour les personnages dont la conversation
 * passe par le chat : ceux dont l'interaction lance des commandes. Un
 * personnage à menu est pris en charge par le pont Easy NPC ; un marchand
 * ouvre son commerce ; les autres ne parlent que si on le leur a dit.
 */
public enum InteractionKind {
    /** Ouvre un dialogue Easy NPC : le gestionnaire de menu s'en charge. */
    MENU,
    /** Ouvre un commerce : rien à capturer. */
    TRADE,
    /** Lance des commandes : c'est la source des dialogues de datapack. */
    COMMAND,
    /** Actions d'autres mods (métiers, transports) : capture sur tag explicite seulement. */
    CUSTOM,
    /** Aucune action à l'interaction. */
    NONE,
    /** Pas un personnage Easy NPC (cocher de caravane, villageois…). */
    NOT_EASYNPC
}
