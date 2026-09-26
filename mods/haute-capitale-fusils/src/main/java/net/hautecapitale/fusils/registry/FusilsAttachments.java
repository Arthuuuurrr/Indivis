package net.hautecapitale.fusils.registry;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.gun.HunterMark;
import net.hautecapitale.fusils.gun.RecoilState;

/** Attachements d'entité (non persistants) : recul serveur, marque du Chasseur, étourdissement. */
public final class FusilsAttachments {
	public static final AttachmentType<RecoilState> RECOIL = AttachmentRegistry.createDefaulted(FusilsIds.id("recul"), () -> RecoilState.NONE);
	public static final AttachmentType<HunterMark> MARK = AttachmentRegistry.create(FusilsIds.id("marque"));
	public static final AttachmentType<Long> STUN_UNTIL = AttachmentRegistry.create(FusilsIds.id("etourdi_jusqua"));
	/** Horodatages des derniers tirs (pour l'accélérateur). */
	public static final AttachmentType<long[]> RECENT_SHOTS = AttachmentRegistry.create(FusilsIds.id("tirs_recents"));

	private FusilsAttachments() {}

	public static void init() {}
}
