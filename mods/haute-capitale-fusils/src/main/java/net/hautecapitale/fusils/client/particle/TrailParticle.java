package net.hautecapitale.fusils.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.hautecapitale.fusils.registry.FusilsParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/** Traînée de balle : un point lumineux qui rétrécit et s'assombrit en quelques ticks. */
public class TrailParticle extends SingleQuadParticle {
	protected final FusilsParticles.TrailOptions options;

	protected TrailParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites, FusilsParticles.TrailOptions options) {
		super(level, x, y, z, xd, yd, zd, sprites.first());
		this.options = options;
		this.lifetime = 12;
		this.xd = xd;
		this.yd = yd;
		this.zd = zd;
		this.gravity = 0.0F;
		this.hasPhysics = false;
		this.friction = 0.9F;
		update(0.0F);
	}

	protected float lifePercent(float partial) {
		float f = Mth.clamp((this.age + partial + this.options.offset() * this.lifetime) / this.lifetime, 0.0F, 1.0F);
		return 1.0F - (1.0F - f) * (1.0F - f) * (1.0F - f);
	}

	protected void update(float partial) {
		float f = lifePercent(partial);
		int from = this.options.fromColor();
		int to = this.options.toColor();
		this.rCol = Mth.lerp(f * f, (from >> 16 & 0xFF) / 255.0F, (to >> 16 & 0xFF) / 255.0F);
		this.gCol = Mth.lerp(f * f, (from >> 8 & 0xFF) / 255.0F, (to >> 8 & 0xFF) / 255.0F);
		this.bCol = Mth.lerp(f * f, (from & 0xFF) / 255.0F, (to & 0xFF) / 255.0F);
		this.alpha = 1.0F - f * 0.6F;
		this.quadSize = Mth.lerp(f, this.options.fromScale(), this.options.toScale());
	}

	@Override
	public void tick() {
		super.tick();
		update(0.0F);
	}

	@Override
	public float getQuadSize(float partial) {
		return Mth.lerp(lifePercent(partial), this.options.fromScale(), this.options.toScale());
	}

	@Override
	protected int getLightColor(float partial) {
		return 0xF000F0;
	}

	@Override
	protected Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	public static class Provider implements ParticleProvider<FusilsParticles.TrailOptions> {
		private final SpriteSet sprites;

		public Provider(FabricSpriteProvider sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(FusilsParticles.TrailOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
			return new TrailParticle(level, x, y, z, xd, yd, zd, this.sprites, options);
		}
	}
}
