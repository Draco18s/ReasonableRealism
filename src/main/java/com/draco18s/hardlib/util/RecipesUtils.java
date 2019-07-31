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
import com.draco18s.industry.ExpandedIndustryBase;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;
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
		resultStack = resultStack.copy();
		ItemStack recipeResult = null;
		RegistryNamespaced<ResourceLocation, IRecipe> recipes = CraftingManager.REGISTRY;
		Iterator<IRecipe> iterator = recipes.iterator();
		while(iterator.hasNext()) {
			IRecipe tmpRecipe = iterator.next();
			recipeResult = tmpRecipe.getRecipeOutput();
			resultStack.setCount(Math.max(recipeResult.getCount(),1));
			if (ItemStack.areItemStacksEqual(resultStack, recipeResult)) {
				return tmpRecipe;
			}
		}
		return null;
	}
	public static List<IRecipe> getAllStorageRecipes() {
		List<IRecipe> results = new ArrayList<IRecipe>();
		ItemStack recipeResult = null;
		RegistryNamespaced<ResourceLocation, IRecipe> recipes = CraftingManager.REGISTRY;
		Iterator<IRecipe> iterator = recipes.iterator();
		outer:
		while(iterator.hasNext()) {
			IRecipe tmpRecipe = iterator.next();
			if(tmpRecipe.canFit(1, 1)) continue;
			if(tmpRecipe instanceof ShapedRecipes) {
				ShapedRecipes shp = (ShapedRecipes)tmpRecipe;
				if(shp.getWidth() != shp.getHeight()) continue;
			}
			Ingredient obj = null;
			int numIngreds = 0;
			for(Ingredient s : tmpRecipe.getIngredients()) {
				if(s != Ingredient.EMPTY) {
					if(obj == null) obj = s;
					else if(!obj.equals(s)) {
						if(obj.getMatchingStacks().length == s.getMatchingStacks().length) {
							ItemStack[] s1 = obj.getMatchingStacks();
							ItemStack[] s2 = s.getMatchingStacks();
							for(int i = 0; i < s1.length; i++) {
								if(!ItemStack.areItemStacksEqual(s1[i], s2[i]))
									continue outer;
							}
						}
						else {
							continue outer;
						}
					}
					numIngreds++;
				}
			}
			if(numIngreds == 4 || numIngreds == 9)
				results.add(tmpRecipe);
		}
		return results;
	}
	
	public static List<IRecipe> getAllStorageRecipes2() {
		List<IRecipe> results = new ArrayList<IRecipe>();
		ItemStack recipeResult = null;
		RegistryNamespaced<ResourceLocation, IRecipe> recipes = CraftingManager.REGISTRY;
		Iterator<IRecipe> iterator = recipes.iterator();
		outer:
		while(iterator.hasNext()) {
			IRecipe tmpRecipe = iterator.next();
			recipeResult = tmpRecipe.getRecipeOutput();
			
			if (recipeResult.getCount() == 4 || recipeResult.getCount() == 9) {
				if(tmpRecipe instanceof ShapedRecipes) {
					ShapedRecipes shp = (ShapedRecipes)tmpRecipe;
					if(!shp.canFit(1, 1)) continue outer;
				}
				Ingredient obj = null;
				for(Ingredient s : tmpRecipe.getIngredients()) {
					if(s != Ingredient.EMPTY) {
						if(obj == null) obj = s;
						else if(!obj.equals(s)) continue outer;
					}
				}
				IRecipe craftRecip = getRecipeWithOutput(obj.getMatchingStacks()[0]);
				if(craftRecip != null) {
					results.add(tmpRecipe);
				}
			}
		}
		return results;
	}

	/**
	 * Attempts to locate a similar recipe using a different material.<br>
	 * Largely speaking this will only ever match tools and armor (picks, swords, armor, etc.) 
	 * @param template - an existing recipe to match against
	 * @param desiredMaterial - the variant to search for
	 * @return
	 */
	@Nullable
	public static IRecipe getSimilarRecipeWithGivenInput(IRecipe template, ItemStack desiredMaterial) {
		if(template == null) return null;
		if(template.getRecipeOutput().isEmpty())
			return null;
		desiredMaterial.setCount(1);
		ItemStack recipeResult = null;
		for(Ingredient ingred : template.getIngredients()) {
			//if the thing we're trying to match accepts the material we want, its correct
			if(ingred.test(desiredMaterial)) {
				return template;
			}
		}
		RegistryNamespaced<ResourceLocation, IRecipe> recipes = CraftingManager.REGISTRY;
		Iterator<IRecipe> iterator = recipes.iterator();
		while(iterator.hasNext()) {
			IRecipe itrRecipe = iterator.next();
			//itrRecipe = RecipesUtils.getRecipeWithOutput(new ItemStack(Items.IRON_SHOVEL));
			if(itrRecipe == template) {
				//we already know the material doesn't match toMatch
				//not skipping would inadvertently return a bad recipe
				continue;
			}
			NonNullList<Ingredient> templateIngreds = template.getIngredients();
			NonNullList<Ingredient> itrRecipeIngreds = itrRecipe.getIngredients();
			boolean doesNotMatch = false;
			//HardLib.logger.log(Level.WARN, templateIngreds.size() + " ?= " + itrRecipeIngreds.size());
			int twidth = getRecipeWidth(template);
			int theight = getRecipeHeight(template);
			int iwidth = getRecipeWidth(itrRecipe);
			int iheight = getRecipeHeight(itrRecipe);
			
			if(twidth == iwidth && theight == iheight) {
				for (int x = 0; x < twidth && !doesNotMatch; x++) {
					for (int y = 0; y < theight && !doesNotMatch; ++y) {
						//HardLib.logger.log(Level.WARN, (x + y * twidth));
						Ingredient templateIng = templateIngreds.get(x + y * twidth);
						Ingredient iteratorIng = itrRecipeIngreds.get(x + y * twidth);

						if(!(iteratorIng.test(desiredMaterial) || Compare(templateIng,iteratorIng))) {
							doesNotMatch = true;
						}
						else {
							if(IsIngredientIngot(templateIng) != IsIngredientIngot(iteratorIng)) {
								doesNotMatch = true;
							}
						}
					}
				}
				/*for(int i = 0; i < templateIngreds.size(); i++) {
					if(!(itrRecipeIngreds.get(i).test(desiredMaterial) || Compare(templateIngreds.get(i),itrRecipeIngreds.get(i)))) {
						doesNotMatch = true;
						break;
					}
				}*/
				if(doesNotMatch) {
					continue;
				}
				return itrRecipe;
			}
		}
		return null;
	}

	private static boolean IsIngredientIngot(Ingredient ingred) {
		for(ItemStack stack : ingred.getMatchingStacks()) {
			int[] ids = OreDictionary.getOreIDs(stack);
			for(int id : ids) {
				if(OreDictionary.getOreName(id).contains("ingot")) {
					return true;
				}
				if(OreDictionary.getOreName(id).contains("plank")) {
					return true;
				}
				if(OreDictionary.getOreName(id).contains("leather")) {
					return true;
				}
				if(OreDictionary.getOreName(id).contains("gem")) {
					return true;
				}
				if(OreDictionary.getOreName(id).contains("stone")) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static int getRecipeWidth(IRecipe template) {
		if(template instanceof ShapedRecipes) {
			return ((ShapedRecipes)template).getWidth();
		}
		if(template instanceof ShapedOreRecipe) {
			return ((ShapedOreRecipe)template).getWidth();
		}
		return 0;
	}

	private static int getRecipeHeight(IRecipe template) {
		if(template instanceof ShapedRecipes) {
			return ((ShapedRecipes)template).getHeight();
		}
		if(template instanceof ShapedOreRecipe) {
			return ((ShapedOreRecipe)template).getHeight();
		}
		return 0;
	}
	
	private static boolean Compare(Ingredient a, Ingredient b) {
		ItemStack[] ss1 = a.getMatchingStacks();
		ItemStack[] ss2 = b.getMatchingStacks();
		if(ss1.length == 0 && ss2.length == 0) return true;
		for(ItemStack s1 : ss1) {
			for(ItemStack s2 : ss2) {
				if(ItemStack.areItemStacksEqual(s1, s2)) return true;
			}
		}
		return false;
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
	 * @param json	The recipe's JSON object
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
	 * @param json	The recipe's JSON object
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
