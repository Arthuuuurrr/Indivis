package net.hautecapitale.party.request;

import java.util.UUID;

/**
 * Le compte a rebours du chef (regle §6).
 *
 * <p>Le temps est compte en ticks serveur, jamais en horloge murale : c'est le
 * serveur qui rythme, et les cinq joueurs voient donc le meme chiffre au meme
 * moment, quelle que soit leur latence.
 *
 * <p>Il appartient au <em>groupe</em>, pas a la personne qui l'a lance : si le chef
 * change en cours de decompte, le decompte continue. Il ne declenche rien non plus
 * — c'est un signal de synchronisation, pas un depart automatique.
 */
public final class Countdown {

    private static final int TICKS_PER_SECOND = 20;

    private final UUID partyId;
    private final int totalSeconds;
    private int remainingTicks;
    private int lastAnnounced;

    public Countdown(UUID partyId, int seconds) {
        this.partyId = partyId;
        this.totalSeconds = seconds;
        this.remainingTicks = seconds * TICKS_PER_SECOND;
        // -1 et non `seconds` : le premier passage doit annoncer la valeur de depart.
        this.lastAnnounced = -1;
    }

    public UUID partyId() {
        return this.partyId;
    }

    public int totalSeconds() {
        return this.totalSeconds;
    }

    /** Secondes restantes, arrondies vers le haut. */
    public int secondsLeft() {
        return (this.remainingTicks + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND;
    }

    public boolean isFinished() {
        return this.remainingTicks <= 0;
    }

    /**
     * Avance d'un tick.
     *
     * @return la seconde a annoncer, ou -1 s'il n'y a rien a dire ce tick-ci.
     *         Zero est une valeur d'annonce valide : c'est le « partez ».
     */
    public int tick() {
        if (this.remainingTicks > 0) {
            this.remainingTicks--;
        }
        int now = secondsLeft();
        if (now != this.lastAnnounced) {
            this.lastAnnounced = now;
            return now;
        }
        return -1;
    }
}
