package net.hautecapitale.fusils.gun;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Joue des {@link GunProfile.SoundRef} côté serveur (diffusés aux joueurs proches). */
public final class SoundUtil {
	private SoundUtil() {}

	public static Holder<SoundEvent> holder(Identifier id) {
		return BuiltInRegistries.SOUND_EVENT.get(id).<Holder<SoundEvent>>map(h -> h).orElseGet(() -> Holder.direct(SoundEvent.createVariableRangeEvent(id)));
	}

	public static void play(Level level, Vec3 pos, GunProfile.SoundRef ref, SoundSource source, float pitchMultiplier) {
		level.playSound(null, pos.x, pos.y, pos.z, holder(ref.id()), source, ref.volume(), ref.samplePitch(level.getRandom()) * pitchMultiplier);
	}

	public static void play(Level level, Vec3 pos, GunProfile.SoundRef ref, SoundSource source) {
		play(level, pos, ref, source, 1.0F);
	}

	/** Joue les repères dont l'instant est dans ]from, to]. */
	public static void playCuesBetween(List<GunProfile.Cue> cues, Entity owner, double from, double to, float pitchMultiplier) {
		for (GunProfile.Cue cue : cues) {
			if (cue.at() > from && cue.at() <= to) {
				play(owner.level(), owner.position(), cue.sound(), SoundSource.PLAYERS, pitchMultiplier);
			}
		}
	}

	/** Joue les repères de cycle de tir dus (fraction de la progression), retourne le nouvel index. */
	public static int playDueCues(List<GunProfile.Cue> cues, Entity owner, float percent, int index, float pitchMultiplier) {
		while (index < cues.size() && cues.get(index).at() <= percent) {
			play(owner.level(), owner.position(), cues.get(index).sound(), SoundSource.PLAYERS, pitchMultiplier);
			index++;
		}
		return index;
	}
}
