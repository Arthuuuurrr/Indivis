package net.hautecapitale.fusils.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Particules du mod : flashs de bouche (3 formes), traînée de balle, éclat d'impact. */
public final class FusilsParticles {
	/** Flash de bouche : teinte RVB, ou -1 pour la couleur de feu de la texture. */
	public record MuzzleFlashOptions(ParticleType<MuzzleFlashOptions> type, int tint) implements ParticleOptions {
		public static MapCodec<MuzzleFlashOptions> codec(ParticleType<MuzzleFlashOptions> type) {
			return Codec.INT.optionalFieldOf("tint", -1).xmap(t -> new MuzzleFlashOptions(type, t), MuzzleFlashOptions::tint);
		}

		public static StreamCodec<RegistryFriendlyByteBuf, MuzzleFlashOptions> streamCodec(ParticleType<MuzzleFlashOptions> type) {
			return ByteBufCodecs.INT.map(t -> new MuzzleFlashOptions(type, t), MuzzleFlashOptions::tint).cast();
		}

		@Override
		public ParticleType<?> getType() {
			return this.type;
		}
	}

	/** Traînée : couleur de départ et d'arrivée, taille de départ et d'arrivée, décalage de vie (0–1). */
	public record TrailOptions(ParticleType<TrailOptions> type, int fromColor, int toColor, float fromScale, float toScale, float offset) implements ParticleOptions {
		public static MapCodec<TrailOptions> codec(ParticleType<TrailOptions> type) {
			return RecordCodecBuilder.mapCodec(b -> b.group(
					Codec.INT.fieldOf("from").forGetter(TrailOptions::fromColor),
					Codec.INT.fieldOf("to").forGetter(TrailOptions::toColor),
					Codec.FLOAT.optionalFieldOf("from_scale", 0.18F).forGetter(TrailOptions::fromScale),
					Codec.FLOAT.optionalFieldOf("to_scale", 0.02F).forGetter(TrailOptions::toScale),
					Codec.FLOAT.optionalFieldOf("offset", 0.0F).forGetter(TrailOptions::offset)
			).apply(b, (f, t, fs, ts, o) -> new TrailOptions(type, f, t, fs, ts, o)));
		}

		public static StreamCodec<RegistryFriendlyByteBuf, TrailOptions> streamCodec(ParticleType<TrailOptions> type) {
			return StreamCodec.composite(
					ByteBufCodecs.INT, TrailOptions::fromColor,
					ByteBufCodecs.INT, TrailOptions::toColor,
					ByteBufCodecs.FLOAT, TrailOptions::fromScale,
					ByteBufCodecs.FLOAT, TrailOptions::toScale,
					ByteBufCodecs.FLOAT, TrailOptions::offset,
					(f, t, fs, ts, o) -> new TrailOptions(type, f, t, fs, ts, o)).cast();
		}

		@Override
		public ParticleType<?> getType() {
			return this.type;
		}
	}

	public static final ParticleType<MuzzleFlashOptions> FLASH_LARGE = flash("flash_large");
	public static final ParticleType<MuzzleFlashOptions> FLASH_TRIANGLE = flash("flash_triangle");
	public static final ParticleType<MuzzleFlashOptions> FLASH_ETOILE = flash("flash_etoile");
	public static final ParticleType<TrailOptions> TRAINEE = Registry.register(BuiltInRegistries.PARTICLE_TYPE, FusilsIds.id("trainee"),
			FabricParticleTypes.complex(true, TrailOptions::codec, TrailOptions::streamCodec));
	public static final ParticleType<TrailOptions> IMPACT = Registry.register(BuiltInRegistries.PARTICLE_TYPE, FusilsIds.id("impact"),
			FabricParticleTypes.complex(true, TrailOptions::codec, TrailOptions::streamCodec));

	private FusilsParticles() {}

	private static ParticleType<MuzzleFlashOptions> flash(String name) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, FusilsIds.id(name),
				FabricParticleTypes.complex(true, MuzzleFlashOptions::codec, MuzzleFlashOptions::streamCodec));
	}

	public static ParticleType<MuzzleFlashOptions> flashByName(String name) {
		return switch (name) {
			case "large" -> FLASH_LARGE;
			case "etoile", "star" -> FLASH_ETOILE;
			default -> FLASH_TRIANGLE;
		};
	}

	public static void init() {}
}
