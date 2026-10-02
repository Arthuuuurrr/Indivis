package net.hautecapitale.fusils.data.component;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Nombre de coups chargés dans l'arme. */
public record Magazine(int count) {
	public static final Magazine EMPTY = new Magazine(0);
	public static final Codec<Magazine> CODEC = Codec.INT.xmap(Magazine::new, Magazine::count);
	public static final StreamCodec<ByteBuf, Magazine> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(Magazine::new, Magazine::count);

	public boolean isEmpty() {
		return this.count <= 0;
	}

	public int missing(int capacity) {
		return Math.max(0, capacity - this.count);
	}

	public Magazine with(int newCount) {
		return new Magazine(Math.max(0, newCount));
	}

	public Magazine deplete(int n) {
		return with(this.count - n);
	}
}
