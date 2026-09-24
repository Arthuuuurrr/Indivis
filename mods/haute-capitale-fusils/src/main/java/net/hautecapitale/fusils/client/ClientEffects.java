package net.hautecapitale.fusils.client;

import java.util.List;

import net.hautecapitale.fusils.gun.SoundUtil;
import net.hautecapitale.fusils.network.payload.GunshotPayload;
import net.hautecapitale.fusils.network.payload.HitmarkerPayload;
import net.hautecapitale.fusils.network.payload.ImpactPayload;
import net.hautecapitale.fusils.network.payload.LocalSoundPayload;
import net.hautecapitale.fusils.network.payload.MuzzleFlashPayload;
import net.hautecapitale.fusils.network.payload.TrailPayload;
import net.hautecapitale.fusils.registry.FusilsParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Effets visuels et sonores reçus du serveur : flash, traînée, impact, coup de feu, marqueur de touche. */
public final class ClientEffects {
	private static final float TRAIL_DENSITY = 3.0F;
	private static int hitmarkerTicks;
	private static boolean hitmarkerCrit;
	private static boolean hitmarkerKill;

	private ClientEffects() {}

	public static void tick() {
		if (hitmarkerTicks > 0) hitmarkerTicks--;
	}

	public static int hitmarkerTicks() { return hitmarkerTicks; }
	public static boolean hitmarkerCrit() { return hitmarkerCrit; }
	public static boolean hitmarkerKill() { return hitmarkerKill; }

	public static void muzzleFlash(MuzzleFlashPayload p) {
		Minecraft mc = Minecraft.getInstance();
		ClientLevel level = mc.level;
		if (level == null || mc.player == null) return;
		Vec3 pos = p.position().add(p.offset());
		RandomSource r = level.getRandom();
		Vec3 motion = p.entityMotion().scale(0.5).add((r.nextDouble() - 0.5) * 0.04, (r.nextDouble() - 0.5) * 0.04, (r.nextDouble() - 0.5) * 0.04);
		if (level.isFluidAtPosition(BlockPos.containing(pos), s -> s.is(FluidTags.WATER))) {
			for (int i = 0; i < 30; i++) {
				level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE, false, pos.x, pos.y, pos.z, (r.nextDouble() - 0.5) * 3.5, (r.nextDouble() - 0.5) * 3.5, (r.nextDouble() - 0.5) * 3.5);
			}
			return;
		}
		var type = FusilsParticles.flashByName(p.flashType());
		level.addAlwaysVisibleParticle(new FusilsParticles.MuzzleFlashOptions(type, p.tint()), true, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
		for (int i = 0; i < 3; i++) {
			level.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, motion.x + (r.nextDouble() - 0.5) * 0.05, motion.y + 0.02 + r.nextDouble() * 0.03, motion.z + (r.nextDouble() - 0.5) * 0.05);
		}
	}

	public static void trail(TrailPayload p) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return;
		List<Integer> colors = p.colors();
		if (colors.isEmpty()) return;
		Vec3 from = p.from();
		Vec3 to = p.to();
		double distance = from.distanceTo(to);
		int steps = (int) (distance * TRAIL_DENSITY);
		if (steps <= 0) return;
		Vec3 dir = to.subtract(from).normalize();
		double speed = 0.25;
		for (int i = 0; i < steps; i++) {
			float f = (i + 1.0F) / steps;
			Vec3 pos = from.lerp(to, f);
			int color = colors.get(i % colors.size());
			int dark = darken(color);
			level.addAlwaysVisibleParticle(new FusilsParticles.TrailOptions(FusilsParticles.TRAINEE, color, dark, 0.16F, 0.02F, 1.0F - f), true,
					pos.x, pos.y, pos.z, dir.x * speed, dir.y * speed, dir.z * speed);
		}
	}

	private static int darken(int rgb) {
		int r = (rgb >> 16 & 0xFF) / 3, g = (rgb >> 8 & 0xFF) / 3, b = (rgb & 0xFF) / 3;
		return r << 16 | g << 8 | b;
	}

	public static void impact(ImpactPayload p) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return;
		Vec3 dir = p.motion().normalize();
		Vec3 pos = p.position().subtract(dir.scale(0.05));
		Vec3 reflected = dir.subtract(p.normal().scale(2.0 * p.normal().dot(dir)));
		level.addAlwaysVisibleParticle(new FusilsParticles.TrailOptions(FusilsParticles.IMPACT, 0xFFE2A0, 0x6B4A20, 0.12F, 0.0F, 0.0F), true,
				pos.x, pos.y, pos.z, reflected.x * 0.05, reflected.y * 0.05, reflected.z * 0.05);
		BlockState state = Block.stateById(p.blockStateId());
		RandomSource r = level.getRandom();
		float speed = (float) p.motion().length();
		float particleSpeed = (10.0F + speed) * 0.02F;
		int count = 3 + (int) (p.damage() / 2.0F);
		for (int i = 0; i < count; i++) {
			Vec3 m = new Vec3(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1).add(reflected.scale(2.5)).normalize().scale(particleSpeed * 1.5);
			if (!state.isAir()) {
				level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.x, pos.y, pos.z, m.x, m.y, m.z);
			}
		}
		level.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, reflected.x * 0.02, 0.02, reflected.z * 0.02);
	}

	public static void gunshot(GunshotPayload p) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.player == null) return;
		RandomSource random = SoundInstance.createUnseededRandom();
		Vec3 pos = p.position();
		mc.getSoundManager().play(new SimpleSoundInstance(SoundUtil.holder(p.shoot()).value(), SoundSource.PLAYERS, p.shootVolume(), p.shootPitch(), random, pos.x, pos.y, pos.z));
		double distance = mc.player.position().distanceTo(pos);
		int delay = (int) Math.round(distance / 40.0 * 20.0 * 0.15) + 2;
		float echoVolume = (float) Math.max(0.25, Math.min(1.0, 0.35 + distance / 96.0)) * p.echoVolume();
		mc.getSoundManager().playDelayed(new SimpleSoundInstance(SoundUtil.holder(p.echo()).value(), SoundSource.PLAYERS, echoVolume, p.echoPitch(), random, pos.x, pos.y, pos.z), delay);
	}

	public static void localSound(LocalSoundPayload p) {
		Minecraft mc = Minecraft.getInstance();
		RandomSource random = SoundInstance.createUnseededRandom();
		mc.getSoundManager().playDelayed(new SimpleSoundInstance(p.sound(), SoundSource.PLAYERS, p.volume(), p.pitch(), random, false, 0,
				SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true), 2);
	}

	public static void hitmarker(HitmarkerPayload p) {
		hitmarkerTicks = p.kill() ? 12 : 8;
		hitmarkerCrit = p.critical();
		hitmarkerKill = p.kill();
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			float pitch = p.kill() ? 0.6F : p.critical() ? 1.6F : 1.2F;
			mc.player.playSound(SoundEvents.ARROW_HIT_PLAYER, 0.5F, pitch);
		}
	}
}
