package com.draco18s.hardlib.api.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class DummyRecipe implements ICraftingRecipe {
	private final ItemStack output;
	private ResourceLocation registryName;
    
	public DummyRecipe(ItemStack outp) {
		output = outp;
	}
	
    public static ICraftingRecipe from(ICraftingRecipe other)
    {
        return new DummyRecipe(other.getRecipeOutput()).setRegistryName(other.getId());
    }

	private ICraftingRecipe setRegistryName(ResourceLocation id) {
		registryName = id;
		return this;
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		return false;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	public ResourceLocation getId() {
		return registryName;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return null;
	}
}