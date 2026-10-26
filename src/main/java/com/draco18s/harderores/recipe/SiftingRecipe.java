package com.draco18s.harderores.recipe;

import com.draco18s.harderores.HarderOres;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

/** What the sifter sifts. */
public class SiftingRecipe extends MachineRecipe {
	public static final RecipeSerializer<SiftingRecipe> SERIALIZER = MachineRecipe.serializer(SiftingRecipe::new);

	public SiftingRecipe(Ingredient in, int qntIn, Ingredient out, int outputCount) {
		super(in, qntIn, out, outputCount);
	}

	@Override
	public RecipeType<SiftingRecipe> getType() {
		return HarderOres.RecipeTypes.SIFTING;
	}

	@Override
	public RecipeSerializer<SiftingRecipe> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return HarderOres.RecipeBookCategories.SIFTING;
	}

	@Override
	protected ItemLike getCraftingStation() {
		return HarderOres.ModBlocks.machine_sifter;
	}
}
