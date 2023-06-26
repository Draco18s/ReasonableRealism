package com.draco18s.hardlib.api.recipe;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.draco18s.hardlib.api.HardLibAPI;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class RecipeTagOutput extends ShapedRecipe {
	private static final int MAX_HEIGHT = 3;
	private static final int MAX_WIDTH = 3;
	protected final ResourceLocation resultName;
	protected final Ingredient result;

	public RecipeTagOutput(ResourceLocation idIn, String groupIn, CraftingBookCategory category, final ResourceLocation resultIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn) {
		super(idIn, groupIn, category, recipeWidthIn, recipeHeightIn, recipeItemsIn, ItemStack.EMPTY, false);
		resultName = resultIn;
		result = Ingredient.of(TagKey.create(Registries.ITEM, resultName));
	}

	public RecipeSerializer<?> getSerializer() {
		return HardLibAPI.RecipeSerializers.TAG_OUTPUT;
	}

	@Override
	@Nonnull
	public ItemStack getResultItem(RegistryAccess regAccess) {
		Collection<ItemStack> list = Arrays.asList(result.getItems());
		return list.stream().findFirst().orElse(ItemStack.EMPTY);
	}

	private static int firstNonSpace(String str) {
		int i;
		for(i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {
			;
		}

		return i;
	}

	private static int lastNonSpace(String str) {
		int i;
		for(i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {
			;
		}

		return i;
	}
	
	static String[] shrink(String... p_44187_) {
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;

		for(int i1 = 0; i1 < p_44187_.length; ++i1) {
			String s = p_44187_[i1];
			i = Math.min(i, firstNonSpace(s));
			int j1 = lastNonSpace(s);
			j = Math.max(j, j1);
			if (j1 < 0) {
				if (k == i1) {
					++k;
				}

				++l;
			} else {
				l = 0;
			}
		}

		if (p_44187_.length == l) {
			return new String[0];
		} else {
			String[] astring = new String[p_44187_.length - l - k];

			for(int k1 = 0; k1 < astring.length; ++k1) {
				astring[k1] = p_44187_[k1 + k].substring(i, j + 1);
			}

			return astring;
		}
	}

	static String[] patternFromJson(JsonArray p_44197_) {
		String[] astring = new String[p_44197_.size()];
		if (astring.length > MAX_HEIGHT) {
			throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
		} else if (astring.length == 0) {
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		} else {
			for(int i = 0; i < astring.length; ++i) {
				String s = GsonHelper.convertToString(p_44197_.get(i), "pattern[" + i + "]");
				if (s.length() > MAX_WIDTH) {
					throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
				}

				if (i > 0 && astring[0].length() != s.length()) {
					throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
				}

				astring[i] = s;
			}

			return astring;
		}
	}

	static Map<String, Ingredient> keyFromJson(JsonObject p_44211_) {
		Map<String, Ingredient> map = Maps.newHashMap();

		for(Map.Entry<String, JsonElement> entry : p_44211_.entrySet()) {
			if (entry.getKey().length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}

			if (" ".equals(entry.getKey())) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}

			map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
		}

		map.put(" ", Ingredient.EMPTY);
		return map;
	}

	public static class Serializer implements RecipeSerializer<RecipeTagOutput> {
		public static final ResourceLocation ID = new ResourceLocation("hardlib", "tag_output");

		@Override
		public RecipeTagOutput fromJson(ResourceLocation recipeId, JsonObject json) {
			String group = GsonHelper.getAsString(json, "group", "");
			CraftingBookCategory craftingbookcategory = Objects.requireNonNullElse(CraftingBookCategory.valueOf(GsonHelper.getAsString(json, "category", (String)null)), CraftingBookCategory.MISC);
			Map<String, Ingredient> keyMap = RecipeTagOutput.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
			String[] astring = RecipeTagOutput.shrink(RecipeTagOutput.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
			int i = astring[0].length();
			int j = astring.length;
			NonNullList<Ingredient> ingredientsList = RecipeUtils.dissolvePattern(astring, keyMap, i, j);
			String tagname = GsonHelper.getAsString(GsonHelper.getAsJsonObject(json, "result"), "tag", "minecraft:air");
			return new RecipeTagOutput(recipeId, group, craftingbookcategory, new ResourceLocation(tagname), i, j, ingredientsList);
		}

		@Override
		public @Nullable RecipeTagOutput fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			int i = buffer.readVarInt();
			int j = buffer.readVarInt();
			String group = buffer.readUtf();
			NonNullList<Ingredient> ingredientsList = NonNullList.withSize(i * j, Ingredient.EMPTY);

			for(int k = 0; k < ingredientsList.size(); ++k) {
				ingredientsList.set(k, Ingredient.fromNetwork(buffer));
			}
			CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
			ResourceLocation result = new ResourceLocation(buffer.readUtf());

			return new RecipeTagOutput(recipeId, group, craftingbookcategory, result, i, j, ingredientsList);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, RecipeTagOutput recipe) {
			buffer.writeVarInt(recipe.getWidth());
			buffer.writeVarInt(recipe.getHeight());
			buffer.writeUtf(recipe.getGroup());

			for(Ingredient ingredient : recipe.getIngredients()) {
				ingredient.toNetwork(buffer);
			}
			buffer.writeEnum(recipe.category());
			buffer.writeUtf(recipe.resultName.toString());
		}
	}
}