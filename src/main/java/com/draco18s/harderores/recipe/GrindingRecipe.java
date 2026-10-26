package com.draco18s.harderores.recipe;

import com.draco18s.harderores.HarderOres;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

/** What the millstone grinds. */
public class GrindingRecipe extends MachineRecipe {
	public static final RecipeSerializer<GrindingRecipe> SERIALIZER = MachineRecipe.serializer(GrindingRecipe::new);

	public GrindingRecipe(Ingredient in, int qntIn, Ingredient out, int outputCount) {
		super(in, qntIn, out, outputCount);
	}

	@Override
	public RecipeType<GrindingRecipe> getType() {
		return HarderOres.RecipeTypes.GRINDING;
	}

	@Override
	public RecipeSerializer<GrindingRecipe> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return HarderOres.RecipeBookCategories.GRINDING;
	}

	@Override
	protected ItemLike getCraftingStation() {
		return HarderOres.ModBlocks.machine_millstone;
	}
}
