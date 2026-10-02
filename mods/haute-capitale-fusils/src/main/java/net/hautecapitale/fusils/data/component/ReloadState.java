package net.hautecapitale.fusils.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

/**
 * Recharge en cours : progression sur la ligne de temps de l'animation (secondes), durée, vitesse,
 * coups à charger, et saut de ligne de temps pour les rechargements partiels.
 */
public record ReloadState(double progress, double duration, double speed, int roundsToLoad, double skipAt, double skipTo) {
	public static final Codec<ReloadState> CODEC = RecordCodecBuilder.create(b -> b.group(
			Codec.DOUBLE.fieldOf("progress").forGetter(ReloadState::progress),
			Codec.DOUBLE.fieldOf("duration").forGetter(ReloadState::duration),
			Codec.DOUBLE.fieldOf("speed").forGetter(ReloadState::speed),
			Codec.INT.fieldOf("rounds").forGetter(ReloadState::roundsToLoad),
			Codec.DOUBLE.optionalFieldOf("skip_at", 0.0).forGetter(ReloadState::skipAt),
			Codec.DOUBLE.optionalFieldOf("skip_to", 0.0).forGetter(ReloadState::skipTo)
	).apply(b, ReloadState::new));
	public static final StreamCodec<ByteBuf, ReloadState> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE, ReloadState::progress,
			ByteBufCodecs.DOUBLE, ReloadState::duration,
			ByteBufCodecs.DOUBLE, ReloadState::speed,
			ByteBufCodecs.VAR_INT, ReloadState::roundsToLoad,
			ByteBufCodecs.DOUBLE, ReloadState::skipAt,
			ByteBufCodecs.DOUBLE, ReloadState::skipTo,
			ReloadState::new);

	public boolean hasSkip() {
		return this.skipTo > this.skipAt;
	}

	public boolean isFinished() {
		return this.progress >= this.duration;
	}

	public float pitchMultiplier() {
		return (float) ((this.speed + 2.0) / 3.0);
	}

	public ReloadState increment(int ticks) {
		return new ReloadState(this.progress + ticks * this.speed / 20.0, this.duration, this.speed, this.roundsToLoad, this.skipAt, this.skipTo);
	}

	public ReloadState applySkip() {
		double skipped = applySkipTo(this.progress);
		return skipped == this.progress ? this : new ReloadState(skipped, this.duration, this.speed, this.roundsToLoad, this.skipAt, this.skipTo);
	}

	private double applySkipTo(double t) {
		return hasSkip() && t >= this.skipAt && t < this.skipTo ? this.skipTo : t;
	}

	/** Avancement 0–1 corrigé du saut, pour le réticule et la barre. */
	public float percent(float partialTick) {
		double skip = Math.max(0.0, this.skipTo - this.skipAt);
		double effective = this.duration - skip;
		if (effective <= 0.0) return 1.0F;
		double timeline = applySkipTo(this.progress + partialTick * this.speed / 20.0);
		double t = hasSkip() && timeline >= this.skipTo ? timeline - skip : timeline;
		return Mth.clamp((float) (t / effective), 0.0F, 1.0F);
	}

	public int durationTicks() {
		double skip = Math.max(0.0, this.skipTo - this.skipAt);
		double wall = Math.max(0.0, (this.duration - skip) / Math.max(this.speed, 1.0E-6));
		return Math.max(1, (int) Math.round(wall * 20.0));
	}
}
