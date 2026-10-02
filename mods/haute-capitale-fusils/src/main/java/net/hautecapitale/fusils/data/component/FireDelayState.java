package net.hautecapitale.fusils.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Délai entre deux coups : progression, durée, index du prochain repère sonore, hauteur. */
public record FireDelayState(int progress, int duration, int cueIndex, float pitchMultiplier) {
	public static final Codec<FireDelayState> CODEC = RecordCodecBuilder.create(b -> b.group(
			Codec.INT.fieldOf("progress").forGetter(FireDelayState::progress),
			Codec.INT.fieldOf("duration").forGetter(FireDelayState::duration),
			Codec.INT.optionalFieldOf("cue", 0).forGetter(FireDelayState::cueIndex),
			Codec.FLOAT.optionalFieldOf("pitch", 1.0F).forGetter(FireDelayState::pitchMultiplier)
	).apply(b, FireDelayState::new));
	public static final StreamCodec<ByteBuf, FireDelayState> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, FireDelayState::progress,
			ByteBufCodecs.VAR_INT, FireDelayState::duration,
			ByteBufCodecs.VAR_INT, FireDelayState::cueIndex,
			ByteBufCodecs.FLOAT, FireDelayState::pitchMultiplier,
			FireDelayState::new);

	public boolean isFinished() {
		return this.progress >= this.duration;
	}

	public float percent() {
		return this.duration <= 0 ? 1.0F : (float) this.progress / this.duration;
	}

	public FireDelayState increment() {
		return new FireDelayState(this.progress + 1, this.duration, this.cueIndex, this.pitchMultiplier);
	}

	public FireDelayState withCue(int cue) {
		return new FireDelayState(this.progress, this.duration, cue, this.pitchMultiplier);
	}
}
