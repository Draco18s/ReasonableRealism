package com.draco18s.industry.client.gui;

import com.draco18s.industry.entities.TileEntityCaster;
import com.draco18s.ores.entities.TileEntitySifter;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiContainerCaster extends GuiContainer {
	TileEntityCaster tileEntity;
	Container container;
	private static ResourceLocation SIFTER_GUI_TEXTURE;

	public GuiContainerCaster(Container inventorySlotsIn, TileEntityCaster tileEntity2) {
		super(inventorySlotsIn);
		tileEntity = tileEntity2;
		SIFTER_GUI_TEXTURE = new ResourceLocation("expindustry:textures/gui/caster.png");
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRendererObj.drawString(new TextComponentTranslation("container.expindustry:caster", new Object[0]).getUnformattedText(), 8, 6, 4210752);
		this.fontRendererObj.drawString(new TextComponentTranslation("container.inventory", new Object[0]).getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		this.mc.getTextureManager().bindTexture(SIFTER_GUI_TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		int progress = Math.round(24 * (1600 - tileEntity.getTime()) / 1600.0f);
		if(tileEntity.getTime() == 0) {
			progress = 0;
		}
		this.drawTexturedModalRect(x+79, y+31, 176, 0, 17, progress);
	}
}
