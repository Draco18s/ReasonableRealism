package com.draco18s.hardlib.api.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.draco18s.hardlib.util.RecipesUtils;
import com.google.gson.JsonObject;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeOreDictOutput extends ShapedOreRecipe {
	protected final String resultName;
	public RecipeOreDictOutput(@Nullable final ResourceLocation group, final String result, final CraftingHelper.ShapedPrimer primer) {
		super(group, ItemStack.EMPTY, primer);
		resultName = result;
	}
	
	@Override
    @Nonnull
    public ItemStack getRecipeOutput() {
		NonNullList<ItemStack> list = OreDictionary.getOres(resultName);
		if(list.size() > 0) {
			return list.get(0);
		}
		return ItemStack.EMPTY;
	}
	
    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1){
    	NonNullList<ItemStack> list = OreDictionary.getOres(resultName);
		if(list.size() > 0) {
			return list.get(0);
		}
		return ItemStack.EMPTY;
    }

	@Override
	public String getGroup() {
		return group == null ? "" : group.toString();
	}

	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(final JsonContext context, final JsonObject json) {
			final String group = JsonUtils.getString(json, "group", "");
			final CraftingHelper.ShapedPrimer primer = RecipesUtils.parseShaped(context, json);
			final String result = JsonUtils.getString(JsonUtils.getJsonObject(json, "result"), "ore");

			return new RecipeOreDictOutput(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
		}
	}
}
