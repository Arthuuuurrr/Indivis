package net.hautecapitale.party.client;

import net.hautecapitale.party.network.HudPayload;

/**
 * Le dernier instantane recu. C'est tout l'etat du client.
 *
 * <p>Ecrit par le fil reseau via {@code ClientPlayNetworking}, lu par le fil de
 * rendu : d'ou {@code volatile} sur une reference immuable, plutot qu'un verrou.
 * Un instantane vide ou une deconnexion effacent l'affichage.
 */
public final class HudModel {

    private static volatile HudPayload current = null;
    private static volatile long receivedAt = 0L;

    private HudModel() {
    }

    public static HudPayload current() {
        return current;
    }

    public static long receivedAt() {
        return receivedAt;
    }

    public static void accept(HudPayload payload) {
        current = payload == null || payload.isEmpty() ? null : payload;
        receivedAt = System.currentTimeMillis();
    }

    public static void clear() {
        current = null;
        receivedAt = 0L;
    }
}
