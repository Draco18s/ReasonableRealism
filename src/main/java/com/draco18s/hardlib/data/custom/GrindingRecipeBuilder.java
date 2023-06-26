package com.draco18s.hardlib.data.custom;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.HardLibAPI;
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

public class GrindingRecipeBuilder implements RecipeBuilder {

	private final Item result;
	private final int count;
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	public GrindingRecipeBuilder(ItemLike p_251897_, int cnt) {
		this.result = p_251897_.asItem();
		this.count = cnt;
	}

	public static GrindingRecipeBuilder grind(ItemLike output, int count) {
		return new GrindingRecipeBuilder(output, count);
	}
	public GrindingRecipeBuilder requires(TagKey<Item> item) {
		return this.requires(Ingredient.of(item));
	}

	public GrindingRecipeBuilder requires(ItemLike item) {
		return this.requires(Ingredient.of(item));
	}

	public GrindingRecipeBuilder requires(Ingredient p_126187_) {
		ingredients.add(p_126187_);
		return this;
	}

	public GrindingRecipeBuilder unlockedBy(String ach, CriterionTriggerInstance trig) {
		this.advancement.addCriterion(ach, trig);
		return this;
	}

	public GrindingRecipeBuilder group(@Nullable String groupName) {
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
			new GrindingRecipeBuilder.Result(regname.withSuffix("_from_grinding").withPrefix(this.count + "_"), this.result, this.count,
				this.ingredients, this.advancement,
				regname.withPrefix("recipes/grinding/")
			)
		);
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final Item result;
		private final int count;
		private final List<Ingredient> ingredients;
		private final Advancement.Builder advancement;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation p_249007_, Item p_248667_, int cnt, List<Ingredient> p_252312_, Advancement.Builder p_249909_, ResourceLocation p_249109_) {
			this.id = p_249007_;
			this.result = p_248667_;
			this.count = cnt;
			this.ingredients = p_252312_;
			this.advancement = p_249909_;
			this.advancementId = p_249109_;
		}

		public void serializeRecipeData(JsonObject p_126230_) {
			JsonArray jsonarray = new JsonArray();

			for(Ingredient ingredient : this.ingredients) {
				jsonarray.add(ingredient.toJson());
			}

			p_126230_.add("ingredients", jsonarray);
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
			if (this.count > 1) {
				jsonobject.addProperty("count", this.count);
			}

			p_126230_.add("result", jsonobject);
		}

		public RecipeSerializer<?> getType() {
			return HardLibAPI.RecipeSerializers.GRINDING;
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
