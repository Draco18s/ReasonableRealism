package com.draco18s.hardlib.api.recipe;

import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** A crafting recipe that never matches (to stand in for one that has been removed). */
public class DummyRecipe implements CraftingRecipe {
	private final ItemStack output;
	private CraftingBookCategory category = CraftingBookCategory.MISC;

	public DummyRecipe(ItemStack outp) {
		output = outp;
	}

	/** Stands in for the recipe, with its result (read from its display, since results are only reached there now). */
	public static CraftingRecipe from(CraftingRecipe other) {
		ContextMap noContext = ContextMap.EMPTY;
		DummyRecipe recipe = new DummyRecipe(other.display().stream().findFirst().map(display -> display.result().resolveForFirstStack(noContext)).orElse(ItemStack.EMPTY));
		recipe.category = other.category();
		return recipe;
	}

	@Override
	public boolean matches(CraftingInput inv, Level world) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		return ItemStack.EMPTY;
	}

	public ItemStack getResultItem() {
		return output;
	}

	@Override
	public boolean showNotification() {
		return false;
	}

	@Override
	public String group() {
		return "";
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
		return null;
	}

	@Override
	public CraftingBookCategory category() {
		return category;
	}
}
