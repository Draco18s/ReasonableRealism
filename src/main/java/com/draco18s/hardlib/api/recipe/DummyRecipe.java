package com.draco18s.hardlib.api.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** A crafting recipe that never matches (to stand in for one that has been removed). */
public class DummyRecipe implements CraftingRecipe {
	private final ItemStack output;
	private CraftingBookCategory category = CraftingBookCategory.MISC;

	public DummyRecipe(ItemStack outp) {
		output = outp;
	}

	public static CraftingRecipe from(CraftingRecipe other, HolderLookup.Provider registries) {
		DummyRecipe recipe = new DummyRecipe(other.getResultItem(registries));
		recipe.category = other.category();
		return recipe;
	}

	@Override
	public boolean matches(CraftingInput inv, Level world) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return false;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return output;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return null;
	}

	@Override
	public CraftingBookCategory category() {
		return category;
	}
}
