package net.tompsen.nexuscharacters;

public final class BeardPreviewVisibilitySupport {
    private BeardPreviewVisibilitySupport() {}

    public static void render(Object drawContext, Object dto, Object skinWidget,
                              int x, int y, int width, int height,
                              int mouseX, int mouseY, float delta) {
        // PRE16: hair must render for every camera angle. The beard remains
        // front-only exactly as before.
        WorldlessHairWidgetRenderer.render(
                drawContext, dto, skinWidget,
                x, y, width, height, mouseX, mouseY, delta);
        if (isBackFacing()) return;
        WorldlessBeardWidgetRenderer.render(
                drawContext, dto, skinWidget,
                x, y, width, height, mouseX, mouseY, delta);
    }

    public static boolean isBackFacing() {
        float yaw = 30.0f + PreviewDragInput.getYawDegrees();
        yaw %= 360.0f;
        if (yaw > 180.0f) yaw -= 360.0f;
        if (yaw < -180.0f) yaw += 360.0f;
        return Math.abs(yaw) > 90.0f;
    }
}
