package net.hautecapitale.fusils.gun;

import java.util.List;

import net.hautecapitale.fusils.data.ShotStats;
import net.minecraft.resources.Identifier;

/** Le résultat de la composition d'un tir pour un objet donné. */
public record ComposedShot(Identifier profileId, GunProfile profile, GunVariant variant, ShotStats stats,
		boolean scope, List<Identifier> installedIds, List<GunModification.Attachment> attachments) {

	public GunProfile.FireMode fireMode() {
		return this.stats.forceAuto() && this.profile.allowAuto() ? GunProfile.FireMode.AUTO : this.profile.fireMode();
	}
}
