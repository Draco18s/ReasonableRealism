package com.draco18s.harddatagen.custom;

import com.draco18s.harderores.recipe.MachineRecipe;
import com.draco18s.harderores.recipe.GrindingRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/** Millstone recipes. */
public class GrindingRecipeBuilder extends MachineRecipeBuilder<GrindingRecipeBuilder> {
	public GrindingRecipeBuilder(ItemLike output, int count) {
		super(output, count, 1);
	}

	public static GrindingRecipeBuilder grind(ItemLike output, int count) {
		return new GrindingRecipeBuilder(output, count);
	}

	@Override
	protected ResourceLocation recipeId(ResourceLocation regname) {
		return regname.withSuffix("_from_grinding").withPrefix(this.count + "_");
	}

	@Override
	protected String advancementFolder() {
		return "grinding";
	}

	@Override
	protected MachineRecipe build(Ingredient input, int ingredCnt, Ingredient output, int outputCount) {
		return new GrindingRecipe(input, ingredCnt, output, outputCount);
	}
}
