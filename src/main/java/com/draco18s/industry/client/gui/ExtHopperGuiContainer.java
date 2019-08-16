package com.draco18s.industry.client.gui;

import com.draco18s.industry.entity.AbstractHopper;
import com.draco18s.industry.inventory.ExtHopperContainer;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ExtHopperGuiContainer extends ContainerScreen<ExtHopperContainer> {
	AbstractHopper tileEntity;
	Container container;
	private static ResourceLocation HOPPER_GUI_TEXTURE;

	public ExtHopperGuiContainer(ExtHopperContainer screenContainer, PlayerInventory inv, ITextComponent containerName) {
		super(screenContainer, inv, containerName);
		tileEntity = screenContainer.tileEntity;
		HOPPER_GUI_TEXTURE = new ResourceLocation("minecraft:textures/gui/container/hopper.png");
		this.ySize = 133;
	}

	@Override
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground();
		super.render(p_render_1_, p_render_2_, p_render_3_);
		this.renderHoveredToolTip(p_render_1_, p_render_2_);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.font.drawString(tileEntity.getDisplayName().getFormattedText(), 8, 6, 4210752);
		//this.font.drawString(new TranslationTextComponent("container.inventory", new Object[0]).getFormattedText(), 8, this.ySize - 96 + 2, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

		this.minecraft.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.blit(x, y, 0, 0, xSize, ySize);
	}
}
