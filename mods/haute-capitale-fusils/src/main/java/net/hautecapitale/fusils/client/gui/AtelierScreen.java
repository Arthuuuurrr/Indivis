package net.hautecapitale.fusils.client.gui;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.gun.GunModification;
import net.hautecapitale.fusils.menu.AtelierMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** Écran de l'atelier : trois emplacements nommés au-dessus de l'inventaire, l'arme affichée en médaillon. */
public class AtelierScreen extends AbstractContainerScreen<AtelierMenu> {
	private static final Identifier BACKGROUND = FusilsIds.id("textures/gui/atelier.png");

	public AtelierScreen(AtelierMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
		graphics.renderItem(this.menu.gun(), this.leftPos + 152, this.topPos + 6);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, this.title, this.titleLabelX, 6, 0xFF3F3F3F, false);
		String canon = Component.translatable(GunModification.Category.CANON.translationKey()).getString();
		String meca = Component.translatable(GunModification.Category.MECANISME.translationKey()).getString();
		String muni = Component.translatable("categorie.haute_capitale_fusils.munition").getString();
		drawLabel(graphics, canon, AtelierMenu.SLOT_POS[0][0] + 8, 56);
		drawLabel(graphics, meca, AtelierMenu.SLOT_POS[1][0] + 8, 56);
		drawLabel(graphics, muni, AtelierMenu.SLOT_POS[2][0] + 8, 56);
		graphics.drawString(this.font, this.playerInventoryTitle, 8, 72, 0xFF3F3F3F, false);
	}

	private void drawLabel(GuiGraphics graphics, String text, int centerX, int y) {
		graphics.drawString(this.font, text, centerX - this.font.width(text) / 2, y, 0xFF5A4A3A, false);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
}
