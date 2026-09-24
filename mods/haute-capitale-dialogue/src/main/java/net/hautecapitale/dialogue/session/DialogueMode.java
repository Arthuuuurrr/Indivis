package net.hautecapitale.dialogue.session;

/**
 * D'où vient le texte de la conversation.
 *
 * <p>{@link #EASYNPC_MENU} : un dialogue Easy NPC natif, présenté par notre
 * écran à la place du sien. {@link #CAPTURE} : les répliques arrivent en
 * messages de jeu (fonctions de datapack, panneaux des transports) et sont
 * capturées côté client. {@link #PANNEAU_SEUL} : aucun écran, seulement la
 * caméra et un panneau, le joueur reste libre.
 */
public enum DialogueMode {
    EASYNPC_MENU,
    CAPTURE,
    PANNEAU_SEUL
}
