package com.draco18s.harderores.recipe;

import com.draco18s.harderores.HarderOres;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

/** What the pressure packager packs (besides storage recipes, see OreProcessingRecipes). */
public class PackingRecipe extends MachineRecipe {
	public static final RecipeSerializer<PackingRecipe> SERIALIZER = MachineRecipe.serializer(PackingRecipe::new);

	public PackingRecipe(Ingredient in, int qntIn, Ingredient out, int outputCount) {
		super(in, qntIn, out, outputCount);
	}

	@Override
	public RecipeType<PackingRecipe> getType() {
		return HarderOres.RecipeTypes.PACKING;
	}

	@Override
	public RecipeSerializer<PackingRecipe> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return HarderOres.RecipeBookCategories.PACKING;
	}

	@Override
	protected ItemLike getCraftingStation() {
		return HarderOres.ModBlocks.packager;
	}
}
