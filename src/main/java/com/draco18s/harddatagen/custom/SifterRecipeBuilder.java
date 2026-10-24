package com.draco18s.harddatagen.custom;

import com.draco18s.harderores.recipe.MachineRecipe;
import com.draco18s.harderores.recipe.SiftingRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/** Sifter recipes. */
public class SifterRecipeBuilder extends MachineRecipeBuilder<SifterRecipeBuilder> {
	public SifterRecipeBuilder(ItemLike output, int count, int ingredCnt) {
		super(output, count, ingredCnt);
	}

	public static SifterRecipeBuilder sift(ItemLike output, int count, int ingredCnt) {
		return new SifterRecipeBuilder(output, count, ingredCnt);
	}

	@Override
	protected ResourceLocation recipeId(ResourceLocation regname) {
		return regname.withSuffix("_from_sifter");
	}

	@Override
	protected String advancementFolder() {
		return "sifter";
	}

	@Override
	protected MachineRecipe build(Ingredient input, int ingredCnt, Ingredient output, int outputCount) {
		return new SiftingRecipe(input, ingredCnt, output, outputCount);
	}
}
