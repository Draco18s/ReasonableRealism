package com.draco18s.hardlib.api.recipe;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

public class RecipeUtils {
	public static Map<String, Ingredient> keyFromJson(JsonObject p_44211_) {
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

	public static NonNullList<Ingredient> dissolvePattern(String[] p_44203_, Map<String, Ingredient> p_44204_, int p_44205_, int p_44206_) {
		NonNullList<Ingredient> nonnulllist = NonNullList.withSize(p_44205_ * p_44206_, Ingredient.EMPTY);
		Set<String> set = Sets.newHashSet(p_44204_.keySet());
		set.remove(" ");

		for(int i = 0; i < p_44203_.length; ++i) {
			for(int j = 0; j < p_44203_[i].length(); ++j) {
				String s = p_44203_[i].substring(j, j + 1);
				Ingredient ingredient = p_44204_.get(s);
				if (ingredient == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
				}

				set.remove(s);
				nonnulllist.set(j + p_44205_ * i, ingredient);
			}
		}

		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		} else {
			return nonnulllist;
		}
	}
}