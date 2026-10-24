package com.draco18s.harderores.client.gui;

import com.draco18s.hardlib.api.internal.CommonContainerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * A machine GUI with a downward progress arrow that fills as the machine's timer counts down to 0
 * (the sifter's texture layout: arrow at 79,31, source at 176,0, 17x24).
 */
public abstract class TimedMachineScreen<T extends CommonContainerMenu> extends AbstractContainerScreen<T> implements MenuAccess<T> {
	private final ResourceLocation texture;
	private final int totalTime;

	protected TimedMachineScreen(T menu, Inventory inventory, Component title, ResourceLocation texture, int totalTime) {
		super(menu, inventory, title);
		this.texture = texture;
		this.totalTime = totalTime;
		this.passEvents = false;
		this.imageHeight = 166;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	/** Ticks left on the machine's current job; 0 when idle. */
	protected abstract int getTime();

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTick);
		this.renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, texture);
		blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

		int time = getTime();
		int progress = time == 0 ? 0 : Math.round(24 * (totalTime - time) / (float)totalTime);
		blit(poseStack, this.leftPos + 79, this.topPos + 31, 176, 0, 17, progress);
	}
}
