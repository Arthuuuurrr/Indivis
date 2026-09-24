package net.hautecapitale.fusils.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.hautecapitale.fusils.registry.FusilsParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;

/** Flash de bouche : trois images en 3 ticks, miroir aléatoire, toujours éclairé, teintable. */
public class MuzzleFlashParticle extends SingleQuadParticle {
	private final SpriteSet sprites;
	private final boolean mirrorH;
	private final boolean mirrorV;

	protected MuzzleFlashParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites, int tint) {
		super(level, x, y, z, xd, yd, zd, sprites.first());
		this.sprites = sprites;
		this.lifetime = 3;
		this.xd = xd;
		this.yd = yd;
		this.zd = zd;
		this.gravity = 0.0F;
		this.hasPhysics = false;
		this.quadSize = 0.9F;
		if (tint >= 0) {
			this.rCol = (tint >> 16 & 0xFF) / 255.0F;
			this.gCol = (tint >> 8 & 0xFF) / 255.0F;
			this.bCol = (tint & 0xFF) / 255.0F;
		}
		RandomSource r = level.getRandom();
		this.mirrorH = r.nextBoolean();
		this.mirrorV = r.nextBoolean();
		this.roll = r.nextInt(4) * (float) (Math.PI / 2);
		this.oRoll = this.roll;
		setSpriteFromAge(sprites);
	}

	@Override
	protected float getU0() { return this.mirrorH ? super.getU1() : super.getU0(); }
	@Override
	protected float getU1() { return this.mirrorH ? super.getU0() : super.getU1(); }
	@Override
	protected float getV0() { return this.mirrorV ? super.getV1() : super.getV0(); }
	@Override
	protected float getV1() { return this.mirrorV ? super.getV0() : super.getV1(); }

	@Override
	public void tick() {
		super.tick();
		setSpriteFromAge(this.sprites);
	}

	@Override
	protected int getLightColor(float partialTick) {
		return 0xF000F0;
	}

	@Override
	protected Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	public static class Provider implements ParticleProvider<FusilsParticles.MuzzleFlashOptions> {
		private final SpriteSet sprites;

		public Provider(FabricSpriteProvider sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(FusilsParticles.MuzzleFlashOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
			return new MuzzleFlashParticle(level, x, y, z, xd, yd, zd, this.sprites, options.tint());
		}
	}
}
