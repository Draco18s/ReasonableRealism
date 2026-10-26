package com.draco18s.harddatagen.custom;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.draco18s.harderores.recipe.MachineRecipe;
import com.google.common.collect.Lists;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

/** Builds a MachineRecipe (sifting, grinding...) and the advancement that unlocks it. */
public abstract class MachineRecipeBuilder<B extends MachineRecipeBuilder<B>> implements RecipeBuilder {
	protected final Item result;
	protected final int count;
	protected final int ingredientQuantity;
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<String, Criterion<?>>();

	protected MachineRecipeBuilder(ItemLike output, int cnt, int ingredCnt) {
		this.result = output.asItem();
		this.count = cnt;
		this.ingredientQuantity = ingredCnt;
	}

	@SuppressWarnings("unchecked")
	private B self() {
		return (B)this;
	}

	public B requires(TagKey<Item> item) {
		return this.requires(Ingredient.of(item));
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
		criteria.put(ach, trig);
		return self();
	}

	@Override
	public B group(@Nullable String groupName) {
		return self();
	}

	@Override
	public Item getResult() {
		return this.result;
	}

	/** The recipe's id, from the result's. */
	protected abstract ResourceLocation recipeId(ResourceLocation regname);

	/** The folder its unlocking advancement goes in, under recipes/. */
	protected abstract String advancementFolder();

	protected abstract MachineRecipe build(Ingredient input, int ingredCnt, Ingredient output, int outputCount);

	@Override
	public void save(RecipeOutput output, ResourceLocation regname) {
		ResourceLocation id = recipeId(regname);
		Advancement.Builder advancement = output.advancement()
			.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
			.rewards(AdvancementRewards.Builder.recipe(id))
			.requirements(AdvancementRequirements.Strategy.OR);
		criteria.forEach(advancement::addCriterion);
		Ingredient input = ingredients.size() == 1 ? ingredients.get(0) : CompoundIngredient.of(ingredients.toArray(Ingredient[]::new));
		output.accept(id, build(input, ingredientQuantity, Ingredient.of(result), count),
				advancement.build(id.withPrefix("recipes/" + advancementFolder() + "/")));
	}
}
