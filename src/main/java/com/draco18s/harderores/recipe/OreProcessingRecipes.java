package com.draco18s.harderores.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnull;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.interfaces.IHardOreProcessing;
import com.google.common.collect.Maps;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class OreProcessingRecipes implements IHardOreProcessing {
	//private static Map<ItemStack, ItemStack> millRecipes = Maps.<ItemStack, ItemStack>newHashMap();
	//private static Map<Supplier<TagKey<Item>>, ItemStack> millRecipes2 = Maps.<Supplier<TagKey<Item>>, ItemStack>newHashMap();
	//private static Map<ItemStack, ItemStack> siftRecipes = Maps.<ItemStack, ItemStack>newHashMap();
	//private static Map<Tuple<Supplier<TagKey<Item>>, Integer>, ItemStack> siftRecipes2 = Maps.<Tuple<Supplier<TagKey<Item>>, Integer>, ItemStack>newHashMap();
	private List<SiftingRecipe> siftRecipes;
	private List<GrindingRecipe> millRecipes;
	private static Map<ItemStack, ItemStack> packingRecipes = Maps.<ItemStack, ItemStack>newHashMap();
	private static List<Block> sluiceRecipes = new ArrayList<Block>();
	public static OreProcessingRecipes SERVER_INSTANCE;
	private RegistryAccess registryAccess;

	public OreProcessingRecipes() {
		SERVER_INSTANCE = this;
	}

	public void update(RecipeManager man) {
		siftRecipes = man.getAllRecipesFor(HarderOres.RecipeTypes.SIFTING);
		millRecipes = man.getAllRecipesFor(HarderOres.RecipeTypes.GRINDING);

		registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
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