package com.draco18s.hardlib.api.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DummyRecipe implements CraftingRecipe {
	private final ItemStack output;
	private ResourceLocation registryName;
	private CraftingBookCategory category;
    
	public DummyRecipe(ItemStack outp) {
		output = outp;
	}
	
	public static CraftingRecipe from(CraftingRecipe other)
    {
        return new DummyRecipe(other.getResultItem(RegistryAccess.EMPTY)).setRegistryName(other.getId());
    }

	private CraftingRecipe setRegistryName(ResourceLocation id) {
		registryName = id;
		return this;
	}

	@Override
	public boolean matches(CraftingContainer inv, Level world) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess reg) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return false;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess reg) {
		return output;
	}

	@Override
	public ResourceLocation getId() {
		return registryName;
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