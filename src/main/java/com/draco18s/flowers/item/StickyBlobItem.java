package com.draco18s.flowers.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

/** Aveloz sap, milled out of the plant. Burns. */
public class StickyBlobItem extends Item {

	public StickyBlobItem() {
		super(new Item.Properties());
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		return 150;
	}
}
