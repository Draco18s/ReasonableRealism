package com.draco18s.harddatagen.custom;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public class SifterRecipeBuilder implements RecipeBuilder {

	private final Item result;
	private final int ingredientQuantity;
	private final int count;
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	public SifterRecipeBuilder(ItemLike p_251897_, int cnt, int ingredCnt) {
		this.result = p_251897_.asItem();
		this.count = cnt;
		this.ingredientQuantity = ingredCnt;
	}

	public static SifterRecipeBuilder sift(ItemLike output, int count, int ingredCnt) {
		return new SifterRecipeBuilder(output, count, ingredCnt);
	}
	public SifterRecipeBuilder requires(TagKey<Item> item) {
		return this.requires(Ingredient.of(item));
	}

	public SifterRecipeBuilder requires(ItemLike item) {
		return this.requires(Ingredient.of(item));
	}

	public SifterRecipeBuilder requires(Ingredient p_126187_) {
		ingredients.add(p_126187_);
		return this;
	}

	public SifterRecipeBuilder unlockedBy(String ach, CriterionTriggerInstance trig) {
		this.advancement.addCriterion(ach, trig);
		return this;
	}

	public SifterRecipeBuilder group(@Nullable String groupName) {
		return this;
	}

	public Item getResult() {
		return this.result;
	}

	public void save(Consumer<FinishedRecipe> finishedRecipe, ResourceLocation regname) {
		this.advancement.parent(ROOT_RECIPE_ADVANCEMENT)
			.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(regname))
			.rewards(AdvancementRewards.Builder.recipe(regname))
			.requirements(RequirementsStrategy.OR);
		finishedRecipe.accept(
			new SifterRecipeBuilder.Result(regname.withSuffix("_from_sifter"), this.result, this.count,
				this.ingredients, this.ingredientQuantity, this.advancement,
				regname.withPrefix("recipes/sifter/")
			)
		);
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final Item result;
		private final int count;
		private final List<Ingredient> ingredients;
		private final int ingredientQuantity;
		private final Advancement.Builder advancement;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation p_249007_, Item p_248667_, int cnt, List<Ingredient> p_252312_, int ingredCount, Advancement.Builder p_249909_, ResourceLocation p_249109_) {
			this.id = p_249007_;
			this.result = p_248667_;
			this.count = cnt;
			this.ingredients = p_252312_;
			this.ingredientQuantity = ingredCount;
			this.advancement = p_249909_;
			this.advancementId = p_249109_;
		}

		public void serializeRecipeData(JsonObject p_126230_) {
			JsonArray jsonarray = new JsonArray();

			for(Ingredient ingredient : this.ingredients) {
				jsonarray.add(ingredient.toJson());
			}

			p_126230_.add("ingredients", jsonarray);
			if (this.ingredientQuantity > 1) {
				p_126230_.addProperty("ingredientQuantity", this.ingredientQuantity);
			}
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
			if (this.count > 1) {
				jsonobject.addProperty("count", this.count);
			}

			p_126230_.add("result", jsonobject);
		}

		public RecipeSerializer<?> getType() {
			return HarderOres.RecipeSerializers.SIFTING;
		}

		public ResourceLocation getId() {
			return this.id;
		}

		@Nullable
		public JsonObject serializeAdvancement() {
			return this.advancement.serializeToJson();
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return this.advancementId;
		}
	}
}
