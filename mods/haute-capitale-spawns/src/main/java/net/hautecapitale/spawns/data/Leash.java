package net.hautecapitale.spawns.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * La laisse MMO d'un mob controle.
 *
 * @param radius distance horizontale maximale depuis le point d'origine avant retour ; 0 = pas de laisse
 * @param returnRadius distance a laquelle le retour est considere accompli
 * @param healOnReturn soigne a fond une fois revenu
 * @param invulnerableWhileReturning ignore les degats pendant le retour (« evade »)
 * @param timeoutSeconds au-dela, le mob est teleporte a son point
 * @param speed multiplicateur de vitesse de navigation pendant le retour
 */
public record Leash(int radius, int returnRadius, boolean healOnReturn, boolean invulnerableWhileReturning,
                    int timeoutSeconds, double speed) {

    public static final Codec<Leash> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("radius").forGetter(Leash::radius),
            Codec.INT.optionalFieldOf("return_radius", 3).forGetter(Leash::returnRadius),
            Codec.BOOL.optionalFieldOf("heal_on_return", true).forGetter(Leash::healOnReturn),
            Codec.BOOL.optionalFieldOf("invulnerable_while_returning", true).forGetter(Leash::invulnerableWhileReturning),
            Codec.INT.optionalFieldOf("timeout_seconds", 30).forGetter(Leash::timeoutSeconds),
            Codec.DOUBLE.optionalFieldOf("speed", 1.2D).forGetter(Leash::speed)
    ).apply(instance, Leash::new));

    public static Leash of(int radius) {
        return new Leash(radius, 3, true, true, 30, 1.2D);
    }

    public Leash withRadius(int v) {
        return new Leash(v, this.returnRadius, this.healOnReturn, this.invulnerableWhileReturning, this.timeoutSeconds, this.speed);
    }

    public Leash withReturnRadius(int v) {
        return new Leash(this.radius, v, this.healOnReturn, this.invulnerableWhileReturning, this.timeoutSeconds, this.speed);
    }

    public Leash withHeal(boolean v) {
        return new Leash(this.radius, this.returnRadius, v, this.invulnerableWhileReturning, this.timeoutSeconds, this.speed);
    }

    public Leash withInvulnerable(boolean v) {
        return new Leash(this.radius, this.returnRadius, this.healOnReturn, v, this.timeoutSeconds, this.speed);
    }

    public Leash withTimeout(int v) {
        return new Leash(this.radius, this.returnRadius, this.healOnReturn, this.invulnerableWhileReturning, v, this.speed);
    }

    public Leash withSpeed(double v) {
        return new Leash(this.radius, this.returnRadius, this.healOnReturn, this.invulnerableWhileReturning, this.timeoutSeconds, v);
    }

    public boolean isEnabled() {
        return this.radius > 0;
    }

    public String describe() {
        if (!this.isEnabled()) {
            return "aucune";
        }
        return this.radius + " blocs (retour a " + this.returnRadius + ", soin " + (this.healOnReturn ? "oui" : "non")
                + ", invulnerable " + (this.invulnerableWhileReturning ? "oui" : "non") + ", delai max " + this.timeoutSeconds + " s)";
    }
}
