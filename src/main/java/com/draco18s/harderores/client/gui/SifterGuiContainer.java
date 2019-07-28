package com.draco18s.harderores.client.gui;

import com.draco18s.harderores.entity.SifterTileEntity;
import com.draco18s.harderores.inventory.SifterContainer;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SifterGuiContainer extends ContainerScreen<SifterContainer> {
	SifterTileEntity tileEntity;
	Container container;
	private static ResourceLocation SIFTER_GUI_TEXTURE;

	public SifterGuiContainer(SifterContainer screenContainer, PlayerInventory inv, ITextComponent containerName) {
		super(screenContainer, inv, containerName);
		tileEntity = screenContainer.tileEntity;
		SIFTER_GUI_TEXTURE = new ResourceLocation("harderores:textures/gui/sifter.png");
	}

	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground();
		super.render(p_render_1_, p_render_2_, p_render_3_);
		this.renderHoveredToolTip(p_render_1_, p_render_2_);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.font.drawString(new TranslationTextComponent("container.harderores:sifter", new Object[0]).getUnformattedComponentText(), 8, 6, 4210752);
		this.font.drawString(new TranslationTextComponent("container.inventory", new Object[0]).getUnformattedComponentText(), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

		this.minecraft.getTextureManager().bindTexture(SIFTER_GUI_TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.blit(x, y, 0, 0, xSize, ySize);
		if(tileEntity != null) {
			int progress = Math.round(24 * (40 - tileEntity.getTime()) / 40.0f);
			if(tileEntity.getTime() == 0) {
				progress = 0;
			}
			this.blit(x+79, y+31, 176, 0, 17, progress);
		}
	}
}
