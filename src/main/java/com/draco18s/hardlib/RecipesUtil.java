package com.draco18s.hardlib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Level;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RecipesUtil {

	public static void RemoveRecipe(Item resultItem, int stacksize, int meta, String modID) {
		ItemStack resultStack = new ItemStack(resultItem, stacksize, meta);
		ItemStack recipeResult = null;
		ArrayList<IRecipe> recipes = (ArrayList) CraftingManager.getInstance().getRecipeList();
		Iterator<IRecipe> iterator = recipes.iterator();
		while(iterator.hasNext()) {
			IRecipe tmpRecipe = iterator.next();
			recipeResult = tmpRecipe.getRecipeOutput();
			if (ItemStack.areItemStacksEqual(resultStack, recipeResult)) {
				HardLib.instance.logger.log(Level.INFO, modID + " Removed Recipe: " + tmpRecipe + " -> " + recipeResult);
				//System.out.println(modID + " Removed Recipe: " + tmpRecipe + " -> " + recipeResult);
				iterator.remove();
			 }
		}
	}
	
	public static void RemoveSmelting(Item resultItem, int stacksize, int meta, String modID) {
		ItemStack resultStack = new ItemStack(resultItem, stacksize, meta);
		ItemStack recipeResult = null;
		Map<ItemStack,ItemStack> recipes = FurnaceRecipes.instance().getSmeltingList();
		Iterator<ItemStack> iterator = recipes.keySet().iterator();
		while(iterator.hasNext()) {
			ItemStack tmpRecipe = iterator.next();
			recipeResult = recipes.get(tmpRecipe);
			if (ItemStack.areItemStacksEqual(resultStack, recipeResult)) {
				HardLib.instance.logger.log(Level.INFO,modID + " Removed Recipe: " + tmpRecipe + " -> " + recipeResult);
				//System.out.println(modID + " Removed Recipe: " + tmpRecipe + " -> " + recipeResult);
				iterator.remove();
			 }
		}
	}

	public static void craftNineOf(ItemStack input, ItemStack output) {
		GameRegistry.addRecipe(output,"xxx","xxx","xxx",'x',input);
	}
}
