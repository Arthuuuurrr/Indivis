package net.hautecapitale.fusils.gun;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.data.Stat;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

/**
 * Le profil d'une arme : tout ce qui la définit hors apparence, lu depuis
 * {@code data/<ns>/fusil_profil/<id>.json} et rechargeable par {@code /reload}.
 * Les chiffres ici sont la base ; variantes, modifications et munitions s'y ajoutent.
 */
public record GunProfile(
		ArmPoseKind pose,
		int magazine,
		int reloadTicks,
		FireMode fireMode,
		boolean allowAuto,
		Optional<TopLoad> topLoad,
		Map<Stat, Double> stats,
		Recoil recoil,
		Sounds sounds,
		MuzzleFlash muzzleFlash,
		Adjuster adjuster,
		Map<String, HandOccupancy> occupancy,
		Assets assets,
		List<Float> aimOffset,
		float aimFov) {

	public enum ArmPoseKind implements StringRepresentable {
		PISTOL, RIFLE;
		public static final Codec<ArmPoseKind> CODEC = StringRepresentable.fromEnum(ArmPoseKind::values);
		@Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
	}

	public enum FireMode implements StringRepresentable {
		SEMI, AUTO;
		public static final Codec<FireMode> CODEC = StringRepresentable.fromEnum(FireMode::values);
		@Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
	}

	public enum HandOccupancy implements StringRepresentable {
		MAINHAND, BOTH;
		public static final Codec<HandOccupancy> CODEC = StringRepresentable.fromEnum(HandOccupancy::values);
		@Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
	}

	/** Ajustement d'os selon l'état de l'arme (chien abaissé quand vide, magasin qui glisse…). */
	public enum Adjuster implements StringRepresentable {
		NONE, LOWER_HAMMER, DOUBLE_HAMMER, MAGAZINE_SLIDE;
		public static final Codec<Adjuster> CODEC = StringRepresentable.fromEnum(Adjuster::values);
		@Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
	}

	/** Rechargement partiel : la boucle [loopStart, loopEnd] de l'animation charge une chambre par {@code loopDuration}. */
	public record TopLoad(double loopStart, double loopEnd, double loopDuration) {
		public static final Codec<TopLoad> CODEC = RecordCodecBuilder.create(b -> b.group(
				Codec.DOUBLE.fieldOf("loop_start").forGetter(TopLoad::loopStart),
				Codec.DOUBLE.fieldOf("loop_end").forGetter(TopLoad::loopEnd),
				Codec.DOUBLE.fieldOf("loop_duration").forGetter(TopLoad::loopDuration)
		).apply(b, TopLoad::new));

		public double resumeFrom(int roundsToLoad) {
			return this.loopEnd - this.loopDuration * (roundsToLoad - 1);
		}
	}

	/** Recul : amplitude en degrés, part horizontale, fréquence du motif, graine. */
	public record Recoil(float magnitude, float horizontalRatio, float frequency, int seed) {
		public static final Codec<Recoil> CODEC = RecordCodecBuilder.create(b -> b.group(
				Codec.FLOAT.optionalFieldOf("magnitude", 10.0F).forGetter(Recoil::magnitude),
				Codec.FLOAT.optionalFieldOf("horizontal_ratio", 0.33F).forGetter(Recoil::horizontalRatio),
				Codec.FLOAT.optionalFieldOf("frequency", 1.7F).forGetter(Recoil::frequency),
				Codec.INT.optionalFieldOf("seed", 0).forGetter(Recoil::seed)
		).apply(b, Recoil::new));
		public static final Recoil DEFAULT = new Recoil(10.0F, 0.33F, 1.7F, 0);
	}

	/** Un son jouable : identifiant d'événement, volume, plage de hauteur. */
	public record SoundRef(Identifier id, float volume, float pitchMin, float pitchMax) {
		public static final Codec<SoundRef> FULL = RecordCodecBuilder.create(b -> b.group(
				Identifier.CODEC.fieldOf("id").forGetter(SoundRef::id),
				Codec.FLOAT.optionalFieldOf("volume", 1.0F).forGetter(SoundRef::volume),
				Codec.FLOAT.optionalFieldOf("pitch_min", 0.95F).forGetter(SoundRef::pitchMin),
				Codec.FLOAT.optionalFieldOf("pitch_max", 1.05F).forGetter(SoundRef::pitchMax)
		).apply(b, SoundRef::new));
		public static final Codec<SoundRef> CODEC = Codec.either(Identifier.CODEC, FULL).xmap(
				e -> e.map(id -> new SoundRef(id, 1.0F, 0.95F, 1.05F), s -> s),
				s -> com.mojang.datafixers.util.Either.right(s));

		public float samplePitch(net.minecraft.util.RandomSource random) {
			return this.pitchMin + random.nextFloat() * (this.pitchMax - this.pitchMin);
		}
	}

	/** Un repère sonore dans le temps : {@code at} en secondes pour la recharge, en fraction 0–1 pour le cycle de tir. */
	public record Cue(float at, SoundRef sound) {
		public static final Codec<Cue> CODEC = RecordCodecBuilder.create(b -> b.group(
				Codec.FLOAT.fieldOf("at").forGetter(Cue::at),
				SoundRef.CODEC.fieldOf("sound").forGetter(Cue::sound)
		).apply(b, Cue::new));
	}

	public record Sounds(SoundRef shoot, SoundRef echo, SoundRef dry, Optional<SoundRef> equip,
			List<Cue> reloadCues, List<Cue> fireCycleCues) {
		public static final Codec<Sounds> CODEC = RecordCodecBuilder.create(b -> b.group(
				SoundRef.CODEC.optionalFieldOf("shoot", new SoundRef(FusilsIds.id("arme.tir_generique"), 1.0F, 0.9F, 1.1F)).forGetter(Sounds::shoot),
				SoundRef.CODEC.optionalFieldOf("echo", new SoundRef(FusilsIds.id("arme.echo"), 1.0F, 0.9F, 1.1F)).forGetter(Sounds::echo),
				SoundRef.CODEC.optionalFieldOf("dry", new SoundRef(FusilsIds.id("arme.a_vide"), 0.75F, 1.4F, 1.6F)).forGetter(Sounds::dry),
				SoundRef.CODEC.optionalFieldOf("equip").forGetter(Sounds::equip),
				Cue.CODEC.listOf().optionalFieldOf("reload_cues", List.of()).forGetter(Sounds::reloadCues),
				Cue.CODEC.listOf().optionalFieldOf("fire_cycle_cues", List.of()).forGetter(Sounds::fireCycleCues)
		).apply(b, Sounds::new));
		public static final Sounds DEFAULT = new Sounds(
				new SoundRef(FusilsIds.id("arme.tir_generique"), 1.0F, 0.9F, 1.1F),
				new SoundRef(FusilsIds.id("arme.echo"), 1.0F, 0.9F, 1.1F),
				new SoundRef(FusilsIds.id("arme.a_vide"), 0.75F, 1.4F, 1.6F),
				Optional.empty(), List.of(), List.of());
	}

	/** Flash de bouche : distance devant l'œil, formes possibles (large / triangle / etoile). */
	public record MuzzleFlash(float distance, List<String> types) {
		public static final Codec<MuzzleFlash> CODEC = RecordCodecBuilder.create(b -> b.group(
				Codec.FLOAT.optionalFieldOf("distance", 1.5F).forGetter(MuzzleFlash::distance),
				Codec.STRING.listOf().optionalFieldOf("types", List.of("triangle", "etoile")).forGetter(MuzzleFlash::types)
		).apply(b, MuzzleFlash::new));
		public static final MuzzleFlash DEFAULT = new MuzzleFlash(1.5F, List.of("triangle", "etoile"));
	}

	/** Modèle, texture, animations : chemins GeckoLib ({@code geckolib/models/item/<model>.geo.json} etc.). */
	public record Assets(Identifier model, Identifier texture, Identifier animation) {
		public static final Codec<Assets> CODEC = RecordCodecBuilder.create(b -> b.group(
				Identifier.CODEC.fieldOf("model").forGetter(Assets::model),
				Identifier.CODEC.fieldOf("texture").forGetter(Assets::texture),
				Identifier.CODEC.fieldOf("animation").forGetter(Assets::animation)
		).apply(b, Assets::new));

		public static Assets simple(String name) {
			return new Assets(FusilsIds.id("item/" + name), FusilsIds.id("textures/item/" + name + ".png"), FusilsIds.id("item/" + name));
		}
	}

	public static final Codec<GunProfile> CODEC = RecordCodecBuilder.create(b -> b.group(
			ArmPoseKind.CODEC.optionalFieldOf("pose", ArmPoseKind.RIFLE).forGetter(GunProfile::pose),
			Codec.INT.optionalFieldOf("magazine", 1).forGetter(GunProfile::magazine),
			Codec.INT.optionalFieldOf("reload_ticks", 40).forGetter(GunProfile::reloadTicks),
			FireMode.CODEC.optionalFieldOf("fire_mode", FireMode.SEMI).forGetter(GunProfile::fireMode),
			Codec.BOOL.optionalFieldOf("allow_auto", false).forGetter(GunProfile::allowAuto),
			TopLoad.CODEC.optionalFieldOf("top_load").forGetter(GunProfile::topLoad),
			Codec.unboundedMap(Stat.CODEC, Codec.DOUBLE).optionalFieldOf("stats", Map.of()).forGetter(GunProfile::stats),
			Recoil.CODEC.optionalFieldOf("recoil", Recoil.DEFAULT).forGetter(GunProfile::recoil),
			Sounds.CODEC.optionalFieldOf("sounds", Sounds.DEFAULT).forGetter(GunProfile::sounds),
			MuzzleFlash.CODEC.optionalFieldOf("muzzle_flash", MuzzleFlash.DEFAULT).forGetter(GunProfile::muzzleFlash),
			Adjuster.CODEC.optionalFieldOf("adjuster", Adjuster.NONE).forGetter(GunProfile::adjuster),
			Codec.unboundedMap(Codec.STRING, HandOccupancy.CODEC).optionalFieldOf("occupancy", Map.of()).forGetter(GunProfile::occupancy),
			Assets.CODEC.optionalFieldOf("assets", Assets.simple("mousquet")).forGetter(GunProfile::assets),
			Codec.FLOAT.listOf().optionalFieldOf("aim_offset", List.of(0.0F, 0.0F, 0.0F)).forGetter(GunProfile::aimOffset),
			Codec.FLOAT.optionalFieldOf("aim_fov", 0.8F).forGetter(GunProfile::aimFov)
	).apply(b, GunProfile::new));

	/** Profil de secours si les données n'ont pas été chargées (client avant synchronisation). */
	public static GunProfile fallback(String name) {
		return new GunProfile(ArmPoseKind.RIFLE, 1, 60, FireMode.SEMI, false, Optional.empty(), Map.of(), Recoil.DEFAULT,
				Sounds.DEFAULT, MuzzleFlash.DEFAULT, Adjuster.NONE, Map.of(), Assets.simple(name), List.of(0.0F, 0.0F, 0.0F), 0.8F);
	}

	public HandOccupancy defaultOccupancy() {
		return this.pose == ArmPoseKind.RIFLE ? HandOccupancy.BOTH : HandOccupancy.MAINHAND;
	}

	public HandOccupancy occupancyFor(String state) {
		return this.occupancy.getOrDefault(state, defaultOccupancy());
	}
}
