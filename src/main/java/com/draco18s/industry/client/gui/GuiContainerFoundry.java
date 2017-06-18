package com.draco18s.industry.client.gui;

import com.draco18s.industry.entities.TileEntityFoundry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiContainerFoundry extends GuiContainer {
	TileEntityFoundry tileEntity;
	Container container;
	private static ResourceLocation FOUNDRY_GUI_TEXTURE;

	public GuiContainerFoundry(Container inventorySlotsIn, TileEntityFoundry tileEntity2) {
		super(inventorySlotsIn);
		tileEntity = tileEntity2;
		FOUNDRY_GUI_TEXTURE = new ResourceLocation("expindustry:textures/gui/foundry.png");
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRendererObj.drawString(new TextComponentTranslation("container.expindustry:foundry", new Object[0]).getUnformattedText(), 8, 6, 4210752);
		this.fontRendererObj.drawString(new TextComponentTranslation("container.inventory", new Object[0]).getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		this.mc.getTextureManager().bindTexture(FOUNDRY_GUI_TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		int progress = Math.round(24 * (1600 - tileEntity.getTime()) / 1600.0f);
		if(tileEntity.getTime() == 0) {
			progress = 0;
		}
		this.drawTexturedModalRect(x+79, y+31, 176, 0, 18, progress);
		//bottom up:
		/*int progress = Math.round(24 * (tileEntity.getTime()) / 1600.0f);
		if(tileEntity.getTime() == 0) {
			progress = 24;
		}
		this.drawTexturedModalRect(x+79, y+31+progress, 176, 0+progress, 18, 24-progress);*/
	}
}
