package com.draco18s.harderores.client.gui;

import com.draco18s.harderores.inventory.SifterContainerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SifterScreen extends AbstractContainerScreen<SifterContainerMenu> implements MenuAccess<SifterContainerMenu> {
	private static ResourceLocation SIFTER_GUI_TEXTURE;

	public SifterScreen(SifterContainerMenu p_97741_, Inventory p_97742_, Component p_97743_) {
		super(p_97741_, p_97742_, p_97743_);
		this.passEvents = false;
		this.imageHeight = 166;
		this.inventoryLabelY = this.imageHeight - 94;
		SIFTER_GUI_TEXTURE = new ResourceLocation("harderores:textures/gui/sifter.png");
	}

	public void render(PoseStack p_98807_, int p_98808_, int p_98809_, float p_98810_) {
		this.renderBackground(p_98807_);
		super.render(p_98807_, p_98808_, p_98809_, p_98810_);
		this.renderTooltip(p_98807_, p_98808_, p_98809_);
	}

	protected void renderBg(PoseStack p_98802_, float p_98803_, int p_98804_, int p_98805_) {
		RenderSystem.setShaderTexture(0, SIFTER_GUI_TEXTURE);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		blit(p_98802_, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		int progress = Math.round(24 * (40 - menu.getTime()) / 40.0f);
		if(menu.getTime() == 0) {
			progress = 0;
		}
		i = this.leftPos;
		j = this.topPos;
		blit(p_98802_, i+79, j+31, 176, 0, 17, progress);
	}
}
