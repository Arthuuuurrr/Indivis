package net.hautecapitale.fusils.data.component;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;

import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.data.StatModifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Retouches propres à un objet unique (récompense de boss, arme nommée) :
 * {@code /give @p haute_capitale_fusils:arquebuse[haute_capitale_fusils:stats={damage:{op:"mul",value:0.3}}]}.
 */
public record StatOverrides(Map<Stat, StatModifier> overrides) {
	public static final StatOverrides EMPTY = new StatOverrides(Map.of());
	public static final Codec<StatOverrides> CODEC = Codec.unboundedMap(Stat.CODEC, StatModifier.CODEC).xmap(StatOverrides::new, StatOverrides::overrides);
	private static final StreamCodec<RegistryFriendlyByteBuf, StatModifier> MOD_STREAM = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, m -> m.op().ordinal(),
			ByteBufCodecs.DOUBLE, StatModifier::value,
			(op, v) -> new StatModifier(StatModifier.Op.values()[op], v));
	public static final StreamCodec<RegistryFriendlyByteBuf, StatOverrides> STREAM_CODEC =
			ByteBufCodecs.map(HashMap::new, ByteBufCodecs.VAR_INT.map(i -> Stat.values()[i], Stat::ordinal), MOD_STREAM)
					.map(StatOverrides::new, s -> new HashMap<>(s.overrides()));

	public StatOverrides {
		overrides = Map.copyOf(overrides);
	}
}
