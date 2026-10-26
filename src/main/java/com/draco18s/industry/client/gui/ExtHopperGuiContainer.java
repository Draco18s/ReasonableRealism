package com.draco18s.industry.client.gui;

import com.draco18s.hardlib.HardLib;
import com.draco18s.industry.inventory.ExtHopperContainerMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ExtHopperGuiContainer extends AbstractContainerScreen<ExtHopperContainerMenu> {
	private static final Identifier HOPPER_LOCATION = HardLib.parseRL("textures/gui/container/hopper.png");

	public ExtHopperGuiContainer(ExtHopperContainerMenu p_97741_, Inventory p_97742_, Component p_97743_) {
		super(p_97741_, p_97742_, p_97743_);
		inventoryLabelY = (8 + 30);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(RenderPipelines.GUI_TEXTURED, HOPPER_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
	}
}
