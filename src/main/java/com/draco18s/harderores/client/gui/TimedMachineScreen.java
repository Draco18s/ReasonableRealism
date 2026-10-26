package com.draco18s.harderores.client.gui;


import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.level.ItemLike;

import com.draco18s.harderores.inventory.MachineRecipeBookMenu;

/**
 * A machine GUI with a downward progress arrow that fills as the machine's timer counts down to 0
 * (the sifter's texture layout: arrow at 79,31, source at 176,0, 17x24), and a recipe book (as the furnace's).
 */
public abstract class TimedMachineScreen<T extends MachineRecipeBookMenu<?>> extends AbstractRecipeBookScreen<T> {
	private final Identifier texture;
	private final int totalTime;

	/**
	 * @param filterName The recipe book's tooltip for showing only what can be made.
	 * @param icon The recipe book tab's icon, for its one category.
	 */
	protected TimedMachineScreen(T menu, Inventory inventory, Component title, Identifier texture, int totalTime, Component filterName, ItemLike icon, RecipeBookCategory category) {
		super(menu, new MachineRecipeBookComponent(menu, filterName, List.of(new RecipeBookComponent.TabInfo(icon.asItem(), category))), inventory, title);
		this.texture = texture;
		this.totalTime = totalTime;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	/** Ticks left on the machine's current job; 0 when idle. */
	protected abstract int getTime();

	@Override
	protected ScreenPosition getRecipeBookButtonPosition() {
		return new ScreenPosition(this.leftPos + 20, this.height / 2 - 49);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

		int time = getTime();
		int progress = time == 0 ? 0 : Math.round(24 * (totalTime - time) / (float)totalTime);
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos + 79, this.topPos + 31, 176, 0, 17, progress, 256, 256);
	}
}
