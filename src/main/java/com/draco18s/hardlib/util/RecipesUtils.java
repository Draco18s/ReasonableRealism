package com.draco18s.hardlib.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.recipes.DummyRecipe;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistryModifiable;

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
	 * @param modID - the mod doing the removing (for logging)
	 */
	@Deprecated
	public static void RemoveRecipe(Item resultItem, int stacksize, int meta, String modID) {
		ItemStack resultStack = new ItemStack(resultItem, stacksize, meta);
		RemoveRecipe(resultStack, modID);
	}
	/**
	 * Remove a crafting recipe
	 * @param resultStack - the output result stack, including metadata and size
	 * @param modID - the mod doing the removing (for logging)
	 */
	@Deprecated
	public static void RemoveRecipe(ItemStack resultStack, String modID) {
		/*ItemStack recipeResult = null;
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
		}*/
	}

	/**
	 * Remove a crafting recipe
	 * @param modRegistry - the recipe registry
	 * @param recipe - resource location of the recipe to remove
	 * @param modID - the mod doing the removing (for logging)
	 */
	public static void RemoveRecipe(IForgeRegistryModifiable modRegistry, ResourceLocation recipe, String modID) {
		IRecipe p = (IRecipe)modRegistry.getValue(recipe);
		
		modRegistry.remove(recipe);
		modRegistry.register(DummyRecipe.from(p));
		
		
		HardLib.instance.logger.log(Level.INFO, modID + " Removed Recipe: " + recipe);
		//This was a nice try, but Advancements are loaded when the world loads.
		/*if(recipe.getResourceDomain().equals("minecraft")) {
			AdvancementUtils.removeAdvancement(new ResourceLocation(recipe.getResourceDomain(),"recipes/" + recipe.getResourcePath()), modID);			
		}
		else {
			HardLib.instance.logger.log(Level.INFO, "Searching for matching recipe advancement...");
			AdvancementUtils.removeAdvancement(recipe, modID);
			AdvancementUtils.removeAdvancement(new ResourceLocation(recipe.getResourceDomain(),"recipe/" + recipe.getResourcePath()), modID);
			AdvancementUtils.removeAdvancement(new ResourceLocation(recipe.getResourceDomain(),"recipes/" + recipe.getResourcePath()), modID);
		}*/
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
		RegistryNamespaced<ResourceLocation, IRecipe> recipes = CraftingManager.REGISTRY;
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
		if(toMatch == null) return null;
		material.setCount(1);
		ItemStack recipeResult = null;
		
		for(Ingredient ingred : toMatch.getIngredients()) {
			if(ingred.test(material)) {
				return toMatch;
			}
		}

		//check toMatch against material first
		//e.g. toMatch is an iron pick and we're looking for a pick shape in material iron
		/*if(toMatch instanceof ShapedRecipes) {
			ShapedRecipes template = (ShapedRecipes)toMatch;
			boolean anySingleMatch = false;
			for(int i = 0; i < template.recipeItems.length; i++) {
				if(template.recipeItems[i] != null) {
					if(template.recipeItems[i].isItemEqual(material)) {
						//we only need one match because we already know that the metal is the important part 
						//there won't be other discontinuities
						anySingleMatch = true;
						break;
					}
				}
			}
			if(anySingleMatch)
				return toMatch;
		}
		else if(toMatch instanceof ShapedOreRecipe) {
			ShapedOreRecipe template = (ShapedOreRecipe)toMatch;
			Object[] templateIn = template.getInput();
			boolean anySingleMatch = false;
			for(int i = 0; i < templateIn.length; i++) {
				if(templateIn[i] instanceof ItemStack) {
					ItemStack testItem = (ItemStack)templateIn[i];
					if(testItem.isItemEqual(material)) {
						anySingleMatch = true;
						break;
					}
				}
				else if(templateIn[i] instanceof List) {
					List<ItemStack> templateList = (List<ItemStack>)templateIn[i];
					boolean didAnyMatch = false;
					for(ItemStack templateItem : templateList) {
						if(templateItem.isItemEqual(material)) {
							didAnyMatch = true;
							break;
						}
					}
					if(didAnyMatch) {
						anySingleMatch = true;
						break;
					}
				}
			}
			if(anySingleMatch)
				return toMatch;
		}*/

		RegistryNamespaced<ResourceLocation, IRecipe> recipes = CraftingManager.REGISTRY;
		Iterator<IRecipe> iterator = recipes.iterator();
		while(iterator.hasNext()) {
			IRecipe tmpRecipe = iterator.next();
			if(tmpRecipe == toMatch) {
				//we already know the material doesn't match toMatch
				//not skipping would inadvertently return a bad recipe
				continue;
			}
			NonNullList<Ingredient> test = toMatch.getIngredients();
			NonNullList<Ingredient> testAgainst = tmpRecipe.getIngredients();
			boolean doesNotMatch = false;
			if(test.size() == testAgainst.size()) {
				for(int i = 0; i < test.size(); i++) {
					if(!(test.get(i).test(material) || test.get(i).equals(testAgainst.get(i)))) {
						doesNotMatch = true;
						break;
					}
				}
				if(doesNotMatch) {
					continue;
				}
				return toMatch;
			}
			
			/*if(tmpRecipe instanceof ShapedRecipes && toMatch instanceof ShapedRecipes) {
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
			}*/
		}
		return null;
	}

	public static void craftNineOf(ItemStack input, ItemStack output) {
		/*GameRegistry.addRecipe(output,"xxx","xxx","xxx",'x',input);*/
	}

	// Replace calls to GameRegistry.addShapeless/ShapedRecipe with these methods, which will dump it to a json in your dir of choice
	// Also works with OD, replace GameRegistry.addRecipe(new ShapedOreRecipe/ShapelessOreRecipe with the same calls

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static File RECIPE_DIR = null;
	private static File ADVANCE_DIR = null;
	private static final Set<String> USED_OD_NAMES = new TreeSet<>();
	private static String DOMAIN = "";

	public static void setupDir(Configuration config) {
		RECIPE_DIR = null;
		if (RECIPE_DIR == null) {
			RECIPE_DIR = config.getConfigFile().toPath().resolve("../recipes/").toFile();
			ADVANCE_DIR = config.getConfigFile().toPath().resolve("../advancements/").toFile();
			String dir = config.getConfigFile().toPath().resolve("../recipes/").toString();
			
			String pattern = "config\\\\(.*)\\.cfg\\\\\\.\\.\\\\recipes";
			Pattern p = Pattern.compile(pattern);
			Matcher matcher = p.matcher(dir);
			while (matcher.find()) {
				DOMAIN = matcher.group(1);
				break;
			}
		}
		if (!RECIPE_DIR.exists()) {
			RECIPE_DIR.mkdir();
		}
		if (!ADVANCE_DIR.exists()) {
			ADVANCE_DIR.mkdir();
		}
	}

	public static void addShapedRecipe(Block result, Object... components) {
		addShapedRecipe(new ItemStack(result), components);
	}
	public static void addShapedRecipe(ItemStack result, Object... components) {
		if(RECIPE_DIR == null) {
			throw new RuntimeException("No recipe directory!");
		}

		// GameRegistry.addShapedRecipe(result, components);

		Map<String, Object> json = new HashMap<>();

		List<String> pattern = new ArrayList<>();
		int i = 0;
		while (i < components.length && components[i] instanceof String) {
			pattern.add((String) components[i]);
			i++;
		}
		json.put("pattern", pattern);

		boolean isOreDict = false;
		Map<String, Map<String, Object>> key = new HashMap<>();
		Character curKey = null;
		for (; i < components.length; i++) {
			Object o = components[i];
			if (o instanceof Character) {
				if (curKey != null)
					throw new IllegalArgumentException("Provided two char keys in a row");
				curKey = (Character) o;
			} else {
				if (curKey == null)
					throw new IllegalArgumentException("Providing object without a char key");
				if (o instanceof String)
					isOreDict = true;
				key.put(Character.toString(curKey), serializeItem(o));
				curKey = null;
			}
		}
		json.put("key", key);
		json.put("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		int copyNum = 0;
		while (f.exists()) {
			if(copyNum == 0) {
				suffix += "_alt";
				copyNum++;
			}
			else {
				copyNum++;
			}
			f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + copyNum + ".json");
		}

		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		writeAdvancement(result.getItem().getRegistryName().getResourcePath() + suffix);
	}

	public static void addShapelessRecipe(Block result, Object... components) {
		addShapelessRecipe(new ItemStack(result), components);
	}
	public static void addShapelessRecipe(ItemStack result, Object... components)
	{
		if(RECIPE_DIR == null) {
			throw new RuntimeException("No recipe directory!");
		}

		// addShapelessRecipe(result, components);

		Map<String, Object> json = new HashMap<>();

		boolean isOreDict = false;
		List<Map<String, Object>> ingredients = new ArrayList<>();
		for (Object o : components) {
			if (o instanceof String)
				isOreDict = true;
			ingredients.add(serializeItem(o));
		}
		json.put("ingredients", ingredients);
		json.put("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		int copyNum = 0;
		while (f.exists()) {
			if(copyNum == 0) {
				suffix += "_alt";
				copyNum++;
			}
			else {
				copyNum++;
			}
			f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + copyNum + ".json");
		}


		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		writeAdvancement(result.getItem().getRegistryName().getResourcePath() + suffix);
	}

	private static void writeAdvancement(String result) {
		if(ADVANCE_DIR == null) {
			throw new RuntimeException("No advancements directory!");
		}
		Map<String, Object> json = new HashMap<>();
		json.put("parent", "minecraft:recipes/root");
		Map<String, Object> rewards = new HashMap<>();
		List<String> recipes = new ArrayList<String>();
		recipes.add(DOMAIN+":"+result);
		rewards.put("recipes",recipes);
		
		Map<String, Map<String, Object>> criteria = new HashMap<>();
		Map<String, Object> has_item = new HashMap<>();
		Map<String, Object> conditions = new HashMap<>();
		Map<String, Object> conditions2 = new HashMap<>();
		Map<String, Object> has_the_recipe = new HashMap<>();
		ArrayList<ArrayList<String>> requirements = new ArrayList<ArrayList<String>>();
		
		has_item.put("trigger", "minecraft:inventory_changed");
		ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> entry = new HashMap<>();
		entry.put("item", "");
		items.add(entry);
		conditions.put("items", items);
		conditions2.put("recipe", DOMAIN+":"+result);
		has_the_recipe.put("trigger", "minecraft:recipe_unlocked");
		has_the_recipe.put("conditions", conditions2);
		
		has_item.put("conditions", conditions);
		criteria.put("has_item", has_item);
		criteria.put("has_the_recipe", has_the_recipe);
		
		ArrayList<String> reqs = new ArrayList<String>();
		reqs.add("has_item");
		reqs.add("has_the_recipe");
		requirements.add(reqs);

		json.put("requirements", requirements);
		json.put("criteria", criteria);
		json.put("rewards", rewards);
		
		String suffix = "";
		File f = new File(ADVANCE_DIR, result + suffix + ".json");

		int copyNum = 0;
		while (f.exists()) {
			if(copyNum == 0) {
				suffix += "_alt";
				copyNum++;
			}
			else {
				copyNum++;
			}
			f = new File(ADVANCE_DIR, result + suffix + copyNum + ".json");
		}

		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Map<String, Object> serializeItem(Object thing) {
		if (thing instanceof Item) {
			return serializeItem(new ItemStack((Item) thing));
		}
		if (thing instanceof Block) {
			return serializeItem(new ItemStack((Block) thing));
		}
		if (thing instanceof ItemStack) {
			ItemStack stack = (ItemStack) thing;
			Map<String, Object> ret = new HashMap<>();
			ret.put("item", stack.getItem().getRegistryName().toString());
			if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
				ret.put("data", stack.getItemDamage());
			}
			if (stack.getCount() > 1) {
				ret.put("count", stack.getCount());
			}

			if (stack.hasTagCompound()) {
				ret.put("type", "minecraft:item_nbt");
				ret.put("nbt", stack.getTagCompound().toString());
			}

			return ret;
		}
		if (thing instanceof String) {
			Map<String, Object> ret = new HashMap<>();
			USED_OD_NAMES.add((String) thing);
			ret.put("type", "forge:ore_dict");
			ret.put("ore", thing);
			//ret.put("item", "#" + ((String) thing).toUpperCase(Locale.ROOT));
			return ret;
		}

		throw new IllegalArgumentException(thing + " was not a block, item, stack, or od name!");
	}

	// Call this after you are done generating
	private static void generateConstants() {
		List<Map<String, Object>> json = new ArrayList<>();
		for (String s : USED_OD_NAMES) {
			Map<String, Object> entry = new HashMap<>();
			entry.put("name", s.toUpperCase(Locale.ROOT));
			entry.put("ingredient", ImmutableMap.of("type", "forge:ore_dict", "ore", s));
			json.add(entry);
		}

		try (FileWriter w = new FileWriter(new File(RECIPE_DIR, "_constants.json"))) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse the input of a shaped recipe.
	 * <p>
	 * Adapted from {@link ShapedOreRecipe#factory}.
	 *
	 * @param context The parsing context
	 * @param json    The recipe's JSON object
	 * @return A ShapedPrimer containing the input specified in the JSON object
	 */
	public static CraftingHelper.ShapedPrimer parseShaped(final JsonContext context, final JsonObject json) {
		final Map<Character, Ingredient> ingredientMap = Maps.newHashMap();
		for (final Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) {
			if (entry.getKey().length() != 1)
				throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			if (" ".equals(entry.getKey()))
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

			ingredientMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
		}

		ingredientMap.put(' ', Ingredient.EMPTY);

		final JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

		if (patternJ.size() == 0)
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

		final String[] pattern = new String[patternJ.size()];
		for (int x = 0; x < pattern.length; ++x) {
			final String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
			if (x > 0 && pattern[0].length() != line.length())
				throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
			pattern[x] = line;
		}

		final CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
		primer.width = pattern[0].length();
		primer.height = pattern.length;
		primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
		primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);

		final Set<Character> keys = Sets.newHashSet(ingredientMap.keySet());
		keys.remove(' ');

		int index = 0;
		for (final String line : pattern) {
			for (final char chr : line.toCharArray()) {
				final Ingredient ing = ingredientMap.get(chr);
				if (ing == null)
					throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
				primer.input.set(index++, ing);
				keys.remove(chr);
			}
		}

		if (!keys.isEmpty())
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);

		return primer;
	}

	/**
	 * Parse the input of a shapeless recipe.
	 * <p>
	 * Adapted from {@link ShapelessOreRecipe#factory}.
	 *
	 * @param context The parsing context
	 * @param json    The recipe's JSON object
	 * @return A NonNullList containing the ingredients specified in the JSON object
	 */
	public static NonNullList<Ingredient> parseShapeless(final JsonContext context, final JsonObject json) {
		final NonNullList<Ingredient> ingredients = NonNullList.create();
		for (final JsonElement element : JsonUtils.getJsonArray(json, "ingredients"))
			ingredients.add(CraftingHelper.getIngredient(element, context));

		if (ingredients.isEmpty())
			throw new JsonParseException("No ingredients for shapeless recipe");

		return ingredients;
	}
}
