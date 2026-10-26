package com.draco18s.harddatagen.custom;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.harderores.recipe.MachineRecipe;
import com.google.common.collect.Lists;

import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

/** Builds a MachineRecipe (sifting, grinding...) and the advancement that unlocks it. */
public abstract class MachineRecipeBuilder<B extends MachineRecipeBuilder<B>> implements RecipeBuilder {
	/** For ingredients that are tags. */
	protected final HolderGetter<Item> items;
	protected final Item result;
	protected final int count;
	protected final int ingredientQuantity;
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final RecipeUnlockAdvancementBuilder advancement = new RecipeUnlockAdvancementBuilder();

	protected MachineRecipeBuilder(HolderGetter<Item> items, ItemLike output, int cnt, int ingredCnt) {
		this.items = items;
		this.result = output.asItem();
		this.count = cnt;
		this.ingredientQuantity = ingredCnt;
	}

	@SuppressWarnings("unchecked")
	private B self() {
		return (B)this;
	}

	public B requires(TagKey<Item> item) {
		return this.requires(Ingredient.of(items.getOrThrow(item)));
	}

	public B requires(ItemLike item) {
		return this.requires(Ingredient.of(item));
	}

	public B requires(Ingredient ingredient) {
		ingredients.add(ingredient);
		return self();
	}

	@Override
	public B unlockedBy(String ach, Criterion<?> trig) {
		advancement.unlockedBy(ach, trig);
		return self();
	}

	@Override
	public B group(@Nullable String groupName) {
		return self();
	}

	/** Named for the result (recipeId then makes the recipe's own id from it). */
	@Override
	public ResourceKey<Recipe<?>> defaultId() {
		return ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(result));
	}

	/** The recipe's id, from the result's. */
	protected abstract Identifier recipeId(Identifier regname);

	/** The folder its unlocking advancement goes in, under recipes/. */
	protected abstract String advancementFolder();

	protected abstract MachineRecipe build(Ingredient input, int ingredCnt, Ingredient output, int outputCount);

	@Override
	public void save(RecipeOutput output, ResourceKey<Recipe<?>> regname) {
		ResourceKey<Recipe<?>> id = ResourceKey.create(Registries.RECIPE, recipeId(regname.identifier()));
		Ingredient input = ingredients.size() == 1 ? ingredients.get(0) : CompoundIngredient.of(ingredients.toArray(Ingredient[]::new));
		output.accept(id, build(input, ingredientQuantity, Ingredient.of(result), count), advancement.build(output, id, advancementFolder()));
	}
}
