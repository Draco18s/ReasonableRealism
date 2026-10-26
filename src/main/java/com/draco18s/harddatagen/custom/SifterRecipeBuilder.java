package com.draco18s.harddatagen.custom;

import com.draco18s.harderores.recipe.MachineRecipe;
import com.draco18s.harderores.recipe.SiftingRecipe;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/** Sifter recipes. */
public class SifterRecipeBuilder extends MachineRecipeBuilder<SifterRecipeBuilder> {
	public SifterRecipeBuilder(HolderGetter<Item> items, ItemLike output, int count, int ingredCnt) {
		super(items, output, count, ingredCnt);
	}

	public static SifterRecipeBuilder sift(HolderGetter<Item> items, ItemLike output, int count, int ingredCnt) {
		return new SifterRecipeBuilder(items, output, count, ingredCnt);
	}

	@Override
	protected Identifier recipeId(Identifier regname) {
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
