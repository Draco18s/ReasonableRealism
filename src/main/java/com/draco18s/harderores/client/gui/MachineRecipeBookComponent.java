package com.draco18s.harderores.client.gui;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.harderores.inventory.MachineRecipeBookMenu;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The recipe book of a one-ingredient machine (MachineRecipeBookMenu): the ghost recipe goes in the first input and
 * the output, which come after the player's slots rather than first as vanilla's component assumes.
 */
public class MachineRecipeBookComponent extends RecipeBookComponent {
	private final Component filterName;

	/** @param filterName The filter button's tooltip, for showing only what can be made ("Showing Siftable"). */
	public MachineRecipeBookComponent(Component filterName) {
		this.filterName = filterName;
	}

	@Override
	protected Component getRecipeFilterName() {
		return filterName;
	}

	@Override
	public void slotClicked(@Nullable Slot slot) {
		if(slot != null && slot.index >= MachineRecipeBookMenu.FIRST_INPUT && slot.index < MachineRecipeBookMenu.OUTPUT) {
			this.ghostRecipe.clear();
			recipesUpdated();
		}
	}

	@Override
	public void setupGhostRecipe(RecipeHolder<?> recipe, List<Slot> slots) {
		this.ghostRecipe.setRecipe(recipe);
		Slot output = slots.get(MachineRecipeBookMenu.OUTPUT);
		this.ghostRecipe.addIngredient(Ingredient.of(recipe.value().getResultItem(this.minecraft.level.registryAccess())), output.x, output.y);
		Slot input = slots.get(MachineRecipeBookMenu.FIRST_INPUT);
		Ingredient ingredient = recipe.value().getIngredients().get(0);
		if(!ingredient.isEmpty()) {
			this.ghostRecipe.addIngredient(ingredient, input.x, input.y);
		}
	}
}
