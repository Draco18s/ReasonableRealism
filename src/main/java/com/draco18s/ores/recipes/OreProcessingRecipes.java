package com.draco18s.ores.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;
import com.draco18s.hardlib.api.interfaces.IHardOreProcessing;
import com.draco18s.ores.entities.TileEntityBasicSluice;
import com.google.common.collect.Maps;

public class OreProcessingRecipes implements IHardOreProcessing {
	private static Map<ItemStack, ItemStack> millRecipes = Maps.<ItemStack, ItemStack>newHashMap();
	private static Map<ItemStack, ItemStack> siftRecipes = Maps.<ItemStack, ItemStack>newHashMap();
	private static Map<ItemStack, ItemStack> packingRecipes = Maps.<ItemStack, ItemStack>newHashMap();
	private static List<Block> sluiceRecipes = new ArrayList();

	@Override
	public void addSiftRecipe(ItemStack input, ItemStack output, boolean registerOutput) {
		siftRecipes.put(input, output);
		if(registerOutput && input != output) {
			output = output.copy();
			output.stackSize = 1;
			siftRecipes.put(output, output);
		}
	}

	@Override
	public void addSiftRecipe(ItemStack input, ItemStack output) {
		addSiftRecipe(input,output,true);
	}

	@Override
	public void addSiftRecipe(String input, int stackSize, ItemStack output, boolean registerOutput) {
		List<ItemStack> stk = OreDictionary.getOres(input);
		for(ItemStack stack : stk) {
			ItemStack s = stack.copy();
			s.stackSize = stackSize;
			addSiftRecipe(s,output,registerOutput);
		}
	}

	@Override
	public void addSiftRecipe(String input, int stackSize, ItemStack output) {
		addSiftRecipe(input, stackSize, output, true);
	}

	@Override
	public void addMillRecipe(ItemStack input, ItemStack output) {
		millRecipes.put(input, output);
	}

	@Override
	public int getSiftAmount(ItemStack stack) {
		Iterator<Entry<ItemStack, ItemStack>> iterator = siftRecipes.entrySet().iterator();
		Entry<ItemStack, ItemStack> entry;

		do {
			if (!iterator.hasNext()) {
				return 0;
			}
			entry = iterator.next();
			//are we sure we don't have to compare size?
		} while (!compareItemStacks(stack, (ItemStack)entry.getKey()));
		return ((ItemStack)entry.getKey()).stackSize;
	}

	@Override
	public ItemStack getSiftResult(ItemStack stack, boolean checkStackSize) {
		for (Entry<ItemStack, ItemStack> entry : this.siftRecipes.entrySet()) {
			if (this.compareItemStacks(stack, entry.getKey(), checkStackSize)) {
				return (ItemStack)entry.getValue();
			}
		}
		return null;
	}

	@Override
	public ItemStack getMillResult(ItemStack stack) {
		for (Entry<ItemStack, ItemStack> entry : this.millRecipes.entrySet()) {
			if (this.compareItemStacks(stack, (ItemStack)entry.getKey())) {
				return (ItemStack)entry.getValue();
			}
		}
		return null;
	}

	private boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
		//return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
		return compareItemStacks(stack1, stack2, false);
	}

	private boolean compareItemStacks(ItemStack key, ItemStack entry, boolean keyStackBiggerThanEntry) {
		return entry.getItem() == key.getItem() && (entry.getMetadata() == 32767 || entry.getMetadata() == key.getMetadata()) && (!keyStackBiggerThanEntry || key.stackSize >= entry.stackSize);
	}

	@Override
	public void addSluiceRecipe(Block output) {
		sluiceRecipes.add(output);
	}
	
	@Nonnull
	@Override
	public Block getRandomSluiceResult(Random rand, Item item) {
		boolean isDirtOrSand = (item == TileEntityBasicSluice.itemSand || item == TileEntityBasicSluice.itemDirt);
		Block oreAttempt;
		do {
			oreAttempt = getRandomSluiceResult(rand, isDirtOrSand);
		} while(isDirtOrSand && oreAttempt == Blocks.GRAVEL);
		return oreAttempt;
	}

	private Block getRandomSluiceResult(Random rand) {
		return sluiceRecipes.get(rand.nextInt(sluiceRecipes.size()));
	}

	//we know all gravel is at the end of the array
	//we also know that gravel is ~25% of the total
	//by rounding up we insure that we always catch every possible ore
	//but might catch the first gravel
	private Block getRandomSluiceResult(Random rand, Boolean skipGravel) {
		int v = (int) Math.ceil(sluiceRecipes.size()*0.75f);
		return sluiceRecipes.get(rand.nextInt(v));
	}

	@Override
	public List<Block> getRandomSluiceResults(Random rand, Item item) {
		ArrayList<Block> list = new ArrayList();
		for(int i = sluiceRecipes.size(); i >= 0; i-=12) {
			list.add(getRandomSluiceResult(rand, item));
		}
		return list;
	}

	@Override
	public void addPressurePackRecipe(ItemStack input, ItemStack output) {
		packingRecipes.put(input, output);
	}

	@Override
	public ItemStack getPressurePackResult(ItemStack stack, boolean checkStackSize) {
		for (Entry<ItemStack, ItemStack> entry : this.packingRecipes.entrySet()) {
			if (this.compareItemStacks(stack, entry.getKey(), checkStackSize)) {
				return (ItemStack)entry.getValue();
			}
		}
		return null;
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
		return ((ItemStack)entry.getKey()).stackSize;
	}
}
