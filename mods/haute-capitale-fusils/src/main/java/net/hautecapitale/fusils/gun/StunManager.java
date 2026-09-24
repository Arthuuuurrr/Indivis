package net.hautecapitale.fusils.gun;

import net.hautecapitale.fusils.registry.FusilsAttachments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Étourdissement bref : interrompt l'usage d'objet (et donc la canalisation d'un sort Spell Engine,
 * qui passe par {@code isUsingItem}), immobilise via Lenteur forte, coupe la navigation des mobs.
 * L'horodatage est exposé par {@link #isStunned} pour les autres systèmes.
 */
public final class StunManager {
	private StunManager() {}

	public static void stun(LivingEntity target, int ticks) {
		long now = target.level().getGameTime();
		target.setAttached(FusilsAttachments.STUN_UNTIL, now + ticks);
		target.stopUsingItem();
		target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, ticks, 9, false, false, true));
		target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ticks, 1, false, false, true));
		if (target instanceof Mob mob) {
			mob.getNavigation().stop();
			mob.setTarget(null);
		}
		if (target.level() instanceof ServerLevel level) {
			Vec3 c = target.getBoundingBox().getCenter();
			level.sendParticles(ParticleTypes.ENCHANTED_HIT, c.x, c.y + target.getBbHeight() * 0.4, c.z, 10, 0.3, 0.2, 0.3, 0.02);
		}
	}

	public static boolean isStunned(LivingEntity target) {
		Long until = target.getAttached(FusilsAttachments.STUN_UNTIL);
		return until != null && target.level().getGameTime() <= until;
	}
}
