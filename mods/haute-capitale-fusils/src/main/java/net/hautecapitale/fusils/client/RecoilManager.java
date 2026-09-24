package net.hautecapitale.fusils.client;

import net.hautecapitale.fusils.config.FusilsConfig;
import net.hautecapitale.fusils.gun.ComposedShot;
import net.hautecapitale.fusils.gun.RecoilMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

/**
 * Recul côté client : la part permanente déplace la visée, la part caméra est un décalage qui
 * décroît (×0,75 par tick) et que la caméra suit avec un curseur lissé.
 */
public final class RecoilManager {
	private static float pitch;
	private static float yaw;
	private static float pitchCursor, pitchCursorO;
	private static float yawCursor, yawCursorO;

	private RecoilManager() {}

	public static void apply(ComposedShot composed, int bulletIndex) {
		Vec2 permanent = RecoilMath.permanentRecoil(composed.stats(), composed.profile().recoil(), bulletIndex);
		Vec2 camera = RecoilMath.cameraRecoil(composed.stats(), composed.profile().recoil(), bulletIndex);
		applyParts(permanent, camera);
	}

	/** Recul brut envoyé par le serveur (tir de compétence) : réparti comme un tir local. */
	public static void applyRaw(float fullPitch, float fullYaw) {
		Vec2 full = new Vec2(fullPitch, fullYaw);
		applyParts(full.scale(RecoilMath.PERMANENT_FRACTION), full.scale(1.0F - RecoilMath.PERMANENT_FRACTION));
	}

	private static void applyParts(Vec2 permanent, Vec2 camera) {
		float scale = (float) FusilsConfig.INSTANCE.camera_recoil_scale;
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.setXRot(player.getXRot() - permanent.x * scale);
			player.setYRot(player.getYRot() + permanent.y * scale);
		}
		pitch += camera.x * scale;
		yaw += camera.y * scale;
	}

	public static void tick() {
		pitchCursorO = pitchCursor;
		yawCursorO = yawCursor;
		pitch = decay(pitch);
		yaw = decay(yaw);
		pitchCursor = Mth.lerp(0.9F, pitchCursor, pitch);
		yawCursor = Mth.lerp(0.9F, yawCursor, yaw);
		if (Math.abs(pitchCursor) < 0.01F) pitchCursor = 0.0F;
		if (Math.abs(yawCursor) < 0.01F) yawCursor = 0.0F;
	}

	private static float decay(float v) {
		float d = RecoilMath.decay(v, 1);
		return Math.abs(d) < 0.01F ? 0.0F : d;
	}

	public static float cameraPitch(float partialTick) {
		return Mth.lerp(partialTick, pitchCursorO, pitchCursor);
	}

	public static float cameraYaw(float partialTick) {
		return Mth.lerp(partialTick, yawCursorO, yawCursor);
	}

	public static float magnitude() {
		return Mth.sqrt(yaw * yaw + pitch * pitch);
	}
}
