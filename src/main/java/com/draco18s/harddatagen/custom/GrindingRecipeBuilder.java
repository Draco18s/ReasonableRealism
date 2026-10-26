package com.draco18s.harddatagen.custom;

import com.draco18s.harderores.recipe.GrindingRecipe;
import com.draco18s.harderores.recipe.MachineRecipe;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/** Millstone recipes. */
public class GrindingRecipeBuilder extends MachineRecipeBuilder<GrindingRecipeBuilder> {
	public GrindingRecipeBuilder(HolderGetter<Item> items, ItemLike output, int count) {
		super(items, output, count, 1);
	}

	public static GrindingRecipeBuilder grind(HolderGetter<Item> items, ItemLike output, int count) {
		return new GrindingRecipeBuilder(items, output, count);
	}

	@Override
	protected Identifier recipeId(Identifier regname) {
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
