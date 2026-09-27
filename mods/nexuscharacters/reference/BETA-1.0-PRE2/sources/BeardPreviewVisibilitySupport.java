package net.tompsen.nexuscharacters;

public final class BeardPreviewVisibilitySupport {
    private BeardPreviewVisibilitySupport() {}

    public static void render(Object graphics, Object dto, Object mainWidget,
                              int x, int y, int width, int height,
                              int mouseX, int mouseY, float delta) {
        if (isBackFacing()) return;
        WorldlessBeardWidgetRenderer.render(graphics, dto, mainWidget,
                x, y, width, height, mouseX, mouseY, delta);
    }

    public static boolean isBackFacing() {
        float effectiveYaw = 30.0f + PreviewDragInput.getYawDegrees();
        effectiveYaw %= 360.0f;
        if (effectiveYaw > 180.0f) effectiveYaw -= 360.0f;
        if (effectiveYaw < -180.0f) effectiveYaw += 360.0f;
        return Math.abs(effectiveYaw) > 90.0f;
    }
}
