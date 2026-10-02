package net.hautecapitale.dialogue.client.screen;

/**
 * Ce que les deux écrans de dialogue ont en commun, vu de l'extérieur :
 * choisir une réponse par son rang, et dire quel dialogue ils montrent.
 */
public interface EcranDialogue {

    /** Choisit la {@code index}-ième réponse visible. Sans effet si elle est verrouillée. */
    void choisir(int index);

    /** Étiquette du dialogue courant (label Easy NPC, ou « capture »). */
    String libelleDialogue();

    /** Les réponses telles qu'affichées, avec leur nuance et leur verrou, pour l'overlay de debug. */
    default java.util.List<String> reponsesDebug() {
        return java.util.List.of();
    }
}
