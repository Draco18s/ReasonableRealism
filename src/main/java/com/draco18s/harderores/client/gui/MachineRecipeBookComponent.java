package com.draco18s.harderores.client.gui;

import java.util.List;

import com.draco18s.harderores.inventory.MachineRecipeBookMenu;
import com.draco18s.harderores.recipe.MachineRecipeDisplay;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

/**
 * The recipe book of a one-ingredient machine (MachineRecipeBookMenu): the ghost recipe goes in the first input and
 * the output, which come after the player's slots rather than first as vanilla's furnace book has them.
 */
public class MachineRecipeBookComponent extends RecipeBookComponent<MachineRecipeBookMenu<?>> {
	/** The furnace's filter button. */
	private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
			Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
			Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
			Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
			Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted"));
	private final Component filterName;

	/** @param filterName The filter button's tooltip, for showing only what can be made ("Showing Siftable"). */
	public MachineRecipeBookComponent(MachineRecipeBookMenu<?> menu, Component filterName, List<RecipeBookComponent.TabInfo> tabInfos) {
		super(menu, tabInfos);
		this.filterName = filterName;
	}

	@Override
	protected WidgetSprites getFilterButtonTextures() {
		return FILTER_SPRITES;
	}

	@Override
	protected Component getRecipeFilterName() {
		return filterName;
	}

	/** Clicking these clears the ghost recipe. */
	@Override
	protected boolean isCraftingSlot(Slot slot) {
		return slot.index >= MachineRecipeBookMenu.FIRST_INPUT && slot.index <= MachineRecipeBookMenu.OUTPUT;
	}

	@Override
	protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipe, ContextMap context) {
		ghostSlots.setResult(this.menu.getResultSlot(), context, recipe.result());
		if(recipe instanceof MachineRecipeDisplay machineRecipe) {
			ghostSlots.setInput(this.menu.slots.get(MachineRecipeBookMenu.FIRST_INPUT), context, machineRecipe.ingredient());
		}
	}

	@Override
	protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents stackedContents) {
		collection.selectRecipes(stackedContents, display -> display instanceof MachineRecipeDisplay);
	}
}
