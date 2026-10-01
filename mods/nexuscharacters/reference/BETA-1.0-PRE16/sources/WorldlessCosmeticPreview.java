package net.tompsen.nexuscharacters;

/**
 * PRE16 entry point for the worldless PlayerSkinWidget preview.
 *
 * Ears and beard keep their dedicated widget renderers. This hook now also
 * injects 3D Skin Layers meshes into the main preview model so the ordinary
 * outer skin layers keep their voxel depth.
 */
public final class WorldlessCosmeticPreview {
    private WorldlessCosmeticPreview() {}

    public static void apply(Object skinWidget, Object dto) {
        CosmeticColorSupport.setPreviewDto(dto);
        WorldlessSkinLayersPreviewSupport.apply(skinWidget, dto);
    }
}
