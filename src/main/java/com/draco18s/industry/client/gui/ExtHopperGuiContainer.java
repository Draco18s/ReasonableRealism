package com.draco18s.industry.client.gui;

import com.draco18s.hardlib.HardLib;
import com.draco18s.industry.inventory.ExtHopperContainerMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ExtHopperGuiContainer extends AbstractContainerScreen<ExtHopperContainerMenu> {
	private static final ResourceLocation HOPPER_LOCATION = HardLib.parseRL("textures/gui/container/hopper.png");

	public ExtHopperGuiContainer(ExtHopperContainerMenu p_97741_, Inventory p_97742_, Component p_97743_) {
		super(p_97741_, p_97742_, p_97743_);
		inventoryLabelY = (8 + 30);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(HOPPER_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
	}
}
