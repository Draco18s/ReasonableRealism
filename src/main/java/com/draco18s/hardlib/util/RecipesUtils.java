package com.draco18s.hardlib.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.draco18s.hardlib.HardLib;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * RecipesUtils supplies several methods for easing the process of removing existing recipes,
 * as well as a helper method for making a 9x9 grid of the same item (nuggets to ingots, etc)
 * 
 * @author Draco18s
 *
 */
public class RecipesUtils {

	/**
	 * Remove a crafting recipe
	 * @param resultItem - the recipe's output Item
	 * @param stacksize - the recipe's output stack size
	 * @param meta - the recipe's output metadata
	 * @param modID - for the mod doing the removing (for logging)
	 */
	public static void RemoveRecipe(Item resultItem, int stacksize, int meta, String modID) {
		ItemStack resultStack = new ItemStack(resultItem, stacksize, meta);
		RemoveRecipe(resultStack, modID);
	}
	/**
	 * Remove a crafting recipe
	 * @param resultStack - the output result stack, including metadata and size
	 * @param modID - for the mod doing the removing (for logging)
	 */
	public static void RemoveRecipe(ItemStack resultStack, String modID) {
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
	
	/**
	 * Remove a smelting recipe
	 * @param resultItem - the smelting output Item
	 * @param stacksize - the smelting output stack size
	 * @param meta - the smelting output metadata
	 * @param modID - for the mod doing the removing (for logging)
	 */
	public static void RemoveSmelting(Item resultItem, int stacksize, int meta, String modID) {
		ItemStack resultStack = new ItemStack(resultItem, stacksize, meta);
		RemoveSmelting(resultStack, modID);
	}
	
	/**
	 * Remove a smelting recipe
	 * @param resultStack - the output result stack, including metadata and size
	 * @param modID - for the mod doing the removing (for logging)
	 */
	public static void RemoveSmelting(ItemStack resultStack, String modID) {
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

	/**
	 * Finds the first recipe in the recipe list that produces a given output.<br>
	 * Generally speaking this should return the basic recipe, rather than a repair recipe.
	 * @param resultStack - the recipe output
	 * @return
	 */
	@Nullable
	public static IRecipe getRecipeWithOutput(ItemStack resultStack) {
		ItemStack recipeResult = null;
		ArrayList<IRecipe> recipes = (ArrayList) CraftingManager.getInstance().getRecipeList();
		Iterator<IRecipe> iterator = recipes.iterator();
		while(iterator.hasNext()) {
			IRecipe tmpRecipe = iterator.next();
			recipeResult = tmpRecipe.getRecipeOutput();
			if (ItemStack.areItemStacksEqual(resultStack, recipeResult)) {
				return tmpRecipe;
			 }
		}
		return null;
	}
	
	/**
	 * Attempts to locate a similar recipe using a different material.<br>
	 * Largely speaking this will only ever match tools and armor (picks, swords, armor, etc.) 
	 * @param toMatch - an existing recipe
	 * @param material - the variant to search for
	 * @return
	 */
	@Nullable
	public static IRecipe getSimilarRecipeWithGivenInput(IRecipe toMatch, ItemStack material) {
		material.stackSize = 1;
		ItemStack recipeResult = null;
		ArrayList<IRecipe> recipes = (ArrayList) CraftingManager.getInstance().getRecipeList();
		Iterator<IRecipe> iterator = recipes.iterator();
		while(iterator.hasNext()) {
			IRecipe tmpRecipe = iterator.next();
			if(tmpRecipe == toMatch) continue;
			if(tmpRecipe instanceof ShapedRecipes && toMatch instanceof ShapedRecipes) {
				ShapedRecipes test = (ShapedRecipes)tmpRecipe;
				ShapedRecipes template = (ShapedRecipes)toMatch;
				if(test.recipeItems.length == template.recipeItems.length) {
					boolean doesNotMatch = false;
					for(int i = 0; i < test.recipeItems.length; i++) {
						if(test.recipeItems[i] != null && template.recipeItems[i] != null) {
							if(!(test.recipeItems[i].isItemEqual(material) || test.recipeItems[i].isItemEqual(template.recipeItems[i]))) {
								doesNotMatch = true;
								break;
							}
						}
						else {
							doesNotMatch = true;
							break;
						}
					}
					if(doesNotMatch) {
						continue;
					}
					return test;
				}
			}
			else if(tmpRecipe instanceof ShapedOreRecipe && toMatch instanceof ShapedOreRecipe) {
				ShapedOreRecipe test = (ShapedOreRecipe)tmpRecipe;
				ShapedOreRecipe template = (ShapedOreRecipe)toMatch;
				if(test.getRecipeSize() == template.getRecipeSize()) {
					boolean doesNotMatch = false;
					Object[] testIn = test.getInput();
					Object[] templateIn = template.getInput();
					for(int i = 0; i < testIn.length; i++) {
						if(testIn[i] instanceof ItemStack && templateIn[i] instanceof ItemStack) {
							ItemStack testItem = (ItemStack)testIn[i];
							if(!(testItem.isItemEqual(material) || testItem.isItemEqual((ItemStack)templateIn[i]))) {
								doesNotMatch = true;
								break;
							}
						}
						else if(testIn[i] instanceof List && templateIn[i] instanceof List) {
							List<ItemStack> testList = (List<ItemStack>)testIn[i];
							List<ItemStack> templateList = (List<ItemStack>)templateIn[i];
							if(testList.size() == templateList.size()) {
								boolean didAnyMatch = false;
								for(ItemStack testItem : testList) {
									for(ItemStack templateItem : templateList) {
										if(testItem.isItemEqual(material) || testItem.isItemEqual(templateItem)) {
											didAnyMatch = true;
											break;
										}
									}
									if(didAnyMatch) {
										break;
									}
								}
								if(!didAnyMatch) {
									doesNotMatch = true;
								}
								if(doesNotMatch) {
									break;
								}
							}
							else {
								doesNotMatch = true;
								break;
							}
						}
						else if(testIn[i] == null && templateIn[i] == null) { }
						else {
							doesNotMatch = true;
							break;
						}
					}
					if(doesNotMatch) {
						continue;
					}
					return test;
				}
			}
			else if(tmpRecipe instanceof ShapedOreRecipe && toMatch instanceof ShapedRecipes) {
				ShapedOreRecipe test = (ShapedOreRecipe)tmpRecipe;
				ShapedRecipes template = (ShapedRecipes)toMatch;
				if(test.getRecipeSize() == template.getRecipeSize()) {
					Object[] testIn = test.getInput();
					boolean doesNotMatch = false;
					for(int i = 0; i < template.recipeItems.length; i++) {
						ItemStack templateItem = template.recipeItems[i];
						Object ti = testIn[i];
						if(ti instanceof ItemStack) {
							ItemStack testItem = (ItemStack)testIn[i];
							if(!(testItem.isItemEqual(material) || testItem.isItemEqual(templateItem))) {
								doesNotMatch = true;
								break;
							}
						}
						else if(testIn[i] instanceof List) {
							List<ItemStack> testList = (List<ItemStack>)testIn[i];
							boolean didAnyMatch = false;
							for(int j = 0; j < testList.size(); j++) {
								ItemStack testItem = testList.get(j);
								if(testItem.isItemEqual(material) || testItem.isItemEqual(templateItem)) {
									didAnyMatch = true;
									break;
								}
							}
							if(!didAnyMatch) {
								doesNotMatch = true;
							}
							if(doesNotMatch) {
								break;
							}
						}
					}
					if(doesNotMatch) {
						continue;
					}
					return test;
				}
			}
		}
		return null;
	}

	public static void craftNineOf(ItemStack input, ItemStack output) {
		GameRegistry.addRecipe(output,"xxx","xxx","xxx",'x',input);
	}
}
