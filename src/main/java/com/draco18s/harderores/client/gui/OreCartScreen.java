package com.draco18s.harderores.client.gui;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.inventory.OreCartMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** The ore cart: its 5x4 slots, and two arrow buttons (at 123,69 and 145,69) for the chute's side. */
public class OreCartScreen extends AbstractContainerScreen<OreCartMenu> {
	private static final Identifier TEXTURE = HarderOres.getRL("textures/gui/ore_cart.png");
	private static final Component DROP_SIDE = Component.translatable("container.harderores.drop_side");
	private static final int BUTTON_SIZE = 22;
	private static final int[] BUTTON_X = {123, 145};
	private static final int BUTTON_Y = 69;

	public OreCartScreen(OreCartMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 176, 184);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
		// The button sheet (at 176,0): left and right arrows, then rows for selected, hovered, and both.
		for(int id = 0; id < BUTTON_X.length; id++) {
			int v = 0;
			if((id == OreCartMenu.BUTTON_RIGHT) == menu.dumpsRight()) v += BUTTON_SIZE;
			if(overButton(id, mouseX, mouseY)) v += 2 * BUTTON_SIZE;
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + BUTTON_X[id], this.topPos + BUTTON_Y, 176 + id * BUTTON_SIZE, v, BUTTON_SIZE, BUTTON_SIZE, 256, 256);
		}
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
		super.extractLabels(graphics, xm, ym);
		graphics.text(this.font, DROP_SIDE, 117, 52, 0xFF404040, false);
	}

	private boolean overButton(int id, double mouseX, double mouseY) {
		double x = mouseX - (this.leftPos + BUTTON_X[id]);
		double y = mouseY - (this.topPos + BUTTON_Y);
		return x >= 0 && y >= 0 && x < BUTTON_SIZE && y < BUTTON_SIZE;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		for(int id = 0; id < BUTTON_X.length; id++) {
			if(overButton(id, event.x(), event.y()) && this.menu.clickMenuButton(this.minecraft.player, id)) {
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
				return true;
			}
		}
		return super.mouseClicked(event, doubleClick);
	}
}
