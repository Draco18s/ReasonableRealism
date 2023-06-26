package com.draco18s.industry.client.gui;

import com.draco18s.industry.inventory.ExtHopperContainerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ExtHopperGuiContainer extends AbstractContainerScreen<ExtHopperContainerMenu> {
	private static final ResourceLocation HOPPER_LOCATION = new ResourceLocation("textures/gui/container/hopper.png");

	public ExtHopperGuiContainer(ExtHopperContainerMenu p_97741_, Inventory p_97742_, Component p_97743_) {
		super(p_97741_, p_97742_, p_97743_);
		inventoryLabelY = (8 + 30);
	}

	@Override
	protected void renderBg(PoseStack p_97787_, float p_97788_, int p_97789_, int p_97790_) {
		RenderSystem.setShaderTexture(0, HOPPER_LOCATION);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		blit(p_97787_, i, j, 0, 0, this.imageWidth, this.imageHeight);
	}
}
