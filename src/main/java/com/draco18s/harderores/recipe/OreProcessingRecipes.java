package com.draco18s.harderores.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.interfaces.IHardOreProcessing;
import com.google.common.collect.Maps;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class OreProcessingRecipes implements IHardOreProcessing {
	//private static Map<ItemStack, ItemStack> millRecipes = Maps.<ItemStack, ItemStack>newHashMap();
	//private static Map<Supplier<TagKey<Item>>, ItemStack> millRecipes2 = Maps.<Supplier<TagKey<Item>>, ItemStack>newHashMap();
	//private static Map<ItemStack, ItemStack> siftRecipes = Maps.<ItemStack, ItemStack>newHashMap();
	//private static Map<Tuple<Supplier<TagKey<Item>>, Integer>, ItemStack> siftRecipes2 = Maps.<Tuple<Supplier<TagKey<Item>>, Integer>, ItemStack>newHashMap();
	private List<SiftingRecipe> siftRecipes;
	private List<GrindingRecipe> millRecipes;
	private static Map<ItemStack, ItemStack> packingRecipes = Maps.<ItemStack, ItemStack>newLinkedHashMap();
	private static List<Block> sluiceRecipes = new ArrayList<Block>();
	public static OreProcessingRecipes SERVER_INSTANCE;
	private RegistryAccess registryAccess;

	public OreProcessingRecipes() {
		SERVER_INSTANCE = this;
	}

	public void update(RecipeManager man) {
		siftRecipes = man.getAllRecipesFor(HarderOres.RecipeTypes.SIFTING).stream().map(RecipeHolder::value).toList();
		millRecipes = man.getAllRecipesFor(HarderOres.RecipeTypes.GRINDING).stream().map(RecipeHolder::value).toList();

		registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		updatePackingRecipes(man);
	}

	/**
	 * The pressure packager packs what its own recipes say, plus anything with a storage recipe: a crafting
	 * recipe made of 4 or 9 of one item (9 ingots into a block, 4 sand into sandstone...). That was 1.12's rule,
	 * including its exclusions.
	 */
	private void updatePackingRecipes(RecipeManager man) {
		packingRecipes.clear();
		for(RecipeHolder<PackingRecipe> holder : man.getAllRecipesFor(HarderOres.RecipeTypes.PACKING)) {
			PackingRecipe recipe = holder.value();
			ItemStack result = recipe.getResultItem(registryAccess);
			for(ItemStack in : recipe.getIngredients().get(0).getItems()) {
				packingRecipes.putIfAbsent(in.copyWithCount(recipe.getIngredientQuantity()), result);
			}
		}
		for(RecipeHolder<CraftingRecipe> holder : man.getAllRecipesFor(RecipeType.CRAFTING)) {
			CraftingRecipe recipe = holder.value();
			List<Ingredient> ingredients = recipe.getIngredients().stream().filter(i -> !i.isEmpty()).toList();
			int size = ingredients.size();
			if(size != 4 && size != 9 || size != recipe.getIngredients().size()) continue;
			ItemStack result = recipe.getResultItem(registryAccess);
			if(result.isEmpty() || result.getCount() >= size) continue;
			if(result.is(Items.OAK_TRAPDOOR) || result.is(Items.IRON_TRAPDOOR) || result.is(Items.CRAFTING_TABLE)) continue;
			Set<Item> items = itemsOf(ingredients.get(0));
			if(items.isEmpty() || ingredients.stream().anyMatch(i -> !itemsOf(i).equals(items))) continue;
			for(Item item : items) {
				if(item == Items.RABBIT_HIDE) continue;
				if(packingRecipes.keySet().stream().anyMatch(k -> k.is(item))) continue;
				packingRecipes.put(new ItemStack(item, size), result.copy());
			}
		}
	}

	private static Set<Item> itemsOf(Ingredient ingredient) {
		return Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
	}

	@Override
	public int getSiftAmount(ItemStack stack) {
		Optional<SiftingRecipe> recip = siftRecipes.stream().filter(
				rec -> rec.getIngredients().parallelStream().anyMatch(ing -> ing.test(stack))
				).findAny();
		if(recip.isEmpty()) return 8;
		return recip.get().getIngredientQuantity();
	}

	@Override
	@Nonnull
	public ItemStack getSiftResult(ItemStack stack, boolean checkStackSize) {
		Optional<SiftingRecipe> recip = siftRecipes.stream().filter(
				rec -> rec.getIngredients().stream().anyMatch(ing -> ing.test(stack))
				).findAny();
		if(recip.isEmpty() || (checkStackSize && stack.getCount() < recip.get().getIngredientQuantity()))
			return ItemStack.EMPTY;

		return  recip.get().getResultItem(registryAccess).copy();
	}

	@Override
	@Nonnull
	public ItemStack getMillResult(ItemStack stack) {
		Optional<GrindingRecipe> recip = millRecipes.stream().filter(
				rec -> {
					return rec.getIngredients().stream().anyMatch(ing -> {
						return ing.test(stack);
					});
				}).findAny();
		if(recip.isEmpty())
			return ItemStack.EMPTY;

		return  recip.get().getResultItem(registryAccess).copy();
	}

	private boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
		return compareItemStacks(stack1, stack2, false);
	}

	private boolean compareItemStacks(ItemStack key, ItemStack entry, boolean keyStackBiggerThanEntry) {
		return entry.getItem() == key.getItem() && (!keyStackBiggerThanEntry || key.getCount() >= entry.getCount());
	}

	@Nonnull
	@Override
	public Block getRandomSluiceResult(Random rand, Item item) {
		boolean isDirtOrSand = false;//(item == TileEntityBasicSluice.itemSand || item == TileEntityBasicSluice.itemDirt);
		Block oreAttempt;
		do {
			oreAttempt = getRandomSluiceResult(rand, isDirtOrSand);
		} while(isDirtOrSand && oreAttempt == Blocks.GRAVEL);
		return oreAttempt;
	}

	//private Block getRandomSluiceResult(Random rand) {
	//	return sluiceRecipes.get(rand.nextInt(sluiceRecipes.size()));
	//}

	//we know all gravel is at the end of the array
	//we also know that gravel is ~25% of the total
	//by rounding up we insure that we always catch every possible ore
	//but might catch the first gravel
	private Block getRandomSluiceResult(Random rand, Boolean skipGravel) {
		int v = (int) Math.ceil(sluiceRecipes.size()*(skipGravel?0.75f:1));
		return sluiceRecipes.get(rand.nextInt(v));
	}

	@Override
	public List<Block> getRandomSluiceResults(Random rand, Item item) {
		ArrayList<Block> list = new ArrayList<Block>();
		for(int i = sluiceRecipes.size(); i >= 0; i-=12) {
			list.add(getRandomSluiceResult(rand, item));
		}
		return list;
	}

	@Override
	@Nonnull
	public ItemStack getPressurePackResult(ItemStack stack, boolean checkStackSize) {
		for (Entry<ItemStack, ItemStack> entry : packingRecipes.entrySet()) {
			if (this.compareItemStacks(stack, entry.getKey(), checkStackSize)) {
				return (ItemStack)entry.getValue();
			}
		}
		return ItemStack.EMPTY;
	}

	public int getPressurePackAmount(ItemStack stack) {
		Iterator<Entry<ItemStack, ItemStack>> iterator = packingRecipes.entrySet().iterator();
		Entry<ItemStack, ItemStack> entry;

		do {
			if (!iterator.hasNext()) {
				return 0;
			}
			entry = iterator.next();
			//are we sure we don't have to compare size?
		} while (!compareItemStacks(stack, (ItemStack)entry.getKey()));
		return ((ItemStack)entry.getKey()).getCount();
	}
}