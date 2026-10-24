package com.draco18s.harderores.client.gui;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

import com.draco18s.harderores.inventory.MachineRecipeBookMenu;

/**
 * A machine GUI with a downward progress arrow that fills as the machine's timer counts down to 0
 * (the sifter's texture layout: arrow at 79,31, source at 176,0, 17x24), and a recipe book (as the furnace's).
 */
public abstract class TimedMachineScreen<T extends MachineRecipeBookMenu<?>> extends AbstractContainerScreen<T> implements MenuAccess<T>, RecipeUpdateListener {
	private final ResourceLocation texture;
	private final int totalTime;
	private final MachineRecipeBookComponent recipeBookComponent;
	private boolean widthTooNarrow;

	/** @param filterName The recipe book's tooltip for showing only what can be made. */
	protected TimedMachineScreen(T menu, Inventory inventory, Component title, ResourceLocation texture, int totalTime, Component filterName) {
		super(menu, inventory, title);
		this.recipeBookComponent = new MachineRecipeBookComponent(filterName);
		this.texture = texture;
		this.totalTime = totalTime;
		this.imageHeight = 166;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	/** Ticks left on the machine's current job; 0 when idle. */
	protected abstract int getTime();

	@Override
	public void init() {
		super.init();
		this.widthTooNarrow = this.width < 379;
		this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
		this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
		this.addRenderableWidget(new ImageButton(this.leftPos + 20, this.height / 2 - 49, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, button -> {
			this.recipeBookComponent.toggleVisibility();
			this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
			button.setPosition(this.leftPos + 20, this.height / 2 - 49);
		}));
	}

	@Override
	public void containerTick() {
		super.containerTick();
		this.recipeBookComponent.tick();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		if(this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
			this.renderBackground(graphics, mouseX, mouseY, partialTick);
			this.recipeBookComponent.render(graphics, mouseX, mouseY, partialTick);
		}
		else {
			super.render(graphics, mouseX, mouseY, partialTick);
			this.recipeBookComponent.render(graphics, mouseX, mouseY, partialTick);
			this.recipeBookComponent.renderGhostRecipe(graphics, this.leftPos, this.topPos, true, partialTick);
		}
		this.renderTooltip(graphics, mouseX, mouseY);
		this.recipeBookComponent.renderTooltip(graphics, this.leftPos, this.topPos, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

		int time = getTime();
		int progress = time == 0 ? 0 : Math.round(24 * (totalTime - time) / (float)totalTime);
		graphics.blit(texture, this.leftPos + 79, this.topPos + 31, 176, 0, 17, progress);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(this.recipeBookComponent.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}
		return this.widthTooNarrow && this.recipeBookComponent.isVisible() || super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
		super.slotClicked(slot, slotId, mouseButton, type);
		this.recipeBookComponent.slotClicked(slot);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.recipeBookComponent.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
		boolean outside = mouseX < left || mouseY < top || mouseX >= left + this.imageWidth || mouseY >= top + this.imageHeight;
		return this.recipeBookComponent.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, button) && outside;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		return this.recipeBookComponent.charTyped(codePoint, modifiers) || super.charTyped(codePoint, modifiers);
	}

	@Override
	public void recipesUpdated() {
		this.recipeBookComponent.recipesUpdated();
	}

	@Override
	public RecipeBookComponent getRecipeBookComponent() {
		return this.recipeBookComponent;
	}
}
