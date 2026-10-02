package net.hautecapitale.party.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

/**
 * Dessine ce que {@link HudLayout} a calcule. Rien de plus.
 *
 * <p>Toute la mise en page est ailleurs ; ici on translate, on met a l'echelle, et
 * on parcourt deux listes. C'est volontairement le fichier le plus bete du mod :
 * c'est celui qu'on ne peut pas tester sans fenetre.
 */
public final class HudRenderer implements HudElement {

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        HudSettings settings = HudSettings.get();
        if (!settings.enabled) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) {
            return;
        }
        var payload = HudModel.current();
        if (payload == null || payload.isEmpty()) {
            return;
        }

        TextRenderer font = client.textRenderer;
        HudLayout.Layout layout = HudLayout.build(payload, settings,
                context.getScaledWindowWidth(), context.getScaledWindowHeight(), font::getWidth);
        if (layout.isEmpty()) {
            return;
        }

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(layout.originX(), layout.originY());
        matrices.scale(layout.scale(), layout.scale());

        for (HudLayout.Rect rect : layout.rects()) {
            context.fill(rect.x1(), rect.y1(), rect.x2(), rect.y2(), rect.argb());
        }
        for (HudLayout.Label label : layout.labels()) {
            context.drawText(font, Text.literal(label.text()), label.x(), label.y(), label.argb(), label.shadow());
        }

        matrices.popMatrix();
    }
}
