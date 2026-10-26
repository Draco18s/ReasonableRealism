package com.draco18s.harderores.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.interfaces.IHardOreProcessing;
import com.google.common.collect.Maps;

import net.minecraft.core.Holder;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
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
	// Recipes are only on the server since 1.21.2; a client of a remote server finds none (an integrated
	// server's are shared with its client).
	private List<SiftingRecipe> siftRecipes = List.of();
	private List<GrindingRecipe> millRecipes = List.of();
	private static Map<ItemStack, ItemStack> packingRecipes = Maps.<ItemStack, ItemStack>newLinkedHashMap();
	private static List<Block> sluiceRecipes = new ArrayList<Block>();
	public static OreProcessingRecipes SERVER_INSTANCE;

	public OreProcessingRecipes() {
		SERVER_INSTANCE = this;
	}

	public void update(RecipeManager man) {
		siftRecipes = man.recipeMap().byType(HarderOres.RecipeTypes.SIFTING).stream().map(RecipeHolder::value).toList();
		millRecipes = man.recipeMap().byType(HarderOres.RecipeTypes.GRINDING).stream().map(RecipeHolder::value).toList();

		// The packing table holds item stacks, which can't be made until items have their components back
		// (after the data load that calls this), so it's built when first needed.
		synchronized(this) {
			pendingPacking = man;
		}
	}

	/** Recipes whose packing table hasn't been built yet. */
	private RecipeManager pendingPacking;

	private synchronized void ensurePackingRecipes() {
		if(pendingPacking != null) {
			updatePackingRecipes(pendingPacking);
			pendingPacking = null;
		}
	}

	/**
	 * The pressure packager packs what its own recipes say, plus anything with a storage recipe: a crafting
	 * recipe made of 4 or 9 of one item (9 ingots into a block, 4 sand into sandstone...). That was 1.12's rule,
	 * including its exclusions.
	 */
	private void updatePackingRecipes(RecipeManager man) {
		packingRecipes.clear();
		for(RecipeHolder<PackingRecipe> holder : man.recipeMap().byType(HarderOres.RecipeTypes.PACKING)) {
			PackingRecipe recipe = holder.value();
			ItemStack result = recipe.getResultItem();
			recipe.getIngredient().items().forEach(in -> packingRecipes.putIfAbsent(new ItemStack(in, recipe.getIngredientQuantity()), result));
		}
		for(RecipeHolder<CraftingRecipe> holder : man.recipeMap().byType(RecipeType.CRAFTING)) {
			CraftingRecipe recipe = holder.value();
			// The ingredients of the filled slots; a recipe with empty slots has more slots than ingredients.
			PlacementInfo placement = recipe.placementInfo();
			List<Ingredient> ingredients = placement.ingredients();
			int size = ingredients.size();
			if(size != 4 && size != 9 || size != placement.slotsToIngredientIndex().size()) continue;
			// Crafting results are only reached through the recipe's display now.
			ItemStack result = recipe.display().stream().findFirst().map(display -> display.result().resolveForFirstStack(NO_CONTEXT)).orElse(ItemStack.EMPTY);
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
		return ingredient.items().map(Holder::value).collect(Collectors.toSet());
	}

	/** Resolving a crafting result needs no level. */
	private static final ContextMap NO_CONTEXT = ContextMap.EMPTY;

	@Override
	public int getSiftAmount(ItemStack stack) {
		Optional<SiftingRecipe> recip = siftRecipes.stream().filter(
				rec -> rec.getIngredient().test(stack)
				).findAny();
		if(recip.isEmpty()) return 8;
		return recip.get().getIngredientQuantity();
	}

	@Override
	@Nonnull
	public ItemStack getSiftResult(ItemStack stack, boolean checkStackSize) {
		Optional<SiftingRecipe> recip = siftRecipes.stream().filter(
				rec -> rec.getIngredient().test(stack)
				).findAny();
		if(recip.isEmpty() || (checkStackSize && stack.getCount() < recip.get().getIngredientQuantity()))
			return ItemStack.EMPTY;

		return  recip.get().getResultItem();
	}

	@Override
	@Nonnull
	public ItemStack getMillResult(ItemStack stack) {
		Optional<GrindingRecipe> recip = millRecipes.stream().filter(
				rec -> {
					return rec.getIngredient().test(stack);
				}).findAny();
		if(recip.isEmpty())
			return ItemStack.EMPTY;

		return  recip.get().getResultItem();
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
		ensurePackingRecipes();
		for (Entry<ItemStack, ItemStack> entry : packingRecipes.entrySet()) {
			if (this.compareItemStacks(stack, entry.getKey(), checkStackSize)) {
				return (ItemStack)entry.getValue();
			}
		}
		return ItemStack.EMPTY;
	}

	public int getPressurePackAmount(ItemStack stack) {
		ensurePackingRecipes();
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