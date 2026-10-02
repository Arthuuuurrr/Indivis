package net.hautecapitale.fusils.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.hautecapitale.fusils.registry.FusilsParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;

/** Étincelle d'impact : comme la traînée, mais plus vive et plus brève. */
public class ImpactParticle extends TrailParticle {
	protected ImpactParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites, FusilsParticles.TrailOptions options) {
		super(level, x, y, z, xd, yd, zd, sprites, options);
		this.lifetime = 6;
		this.friction = 0.7F;
	}

	public static class Provider implements ParticleProvider<FusilsParticles.TrailOptions> {
		private final SpriteSet sprites;

		public Provider(FabricSpriteProvider sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(FusilsParticles.TrailOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
			return new ImpactParticle(level, x, y, z, xd, yd, zd, this.sprites, options);
		}
	}
}
