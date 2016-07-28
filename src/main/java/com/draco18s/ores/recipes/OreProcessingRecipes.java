package com.draco18s.ores.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.interfaces.IBlockMultiBreak;
import com.draco18s.hardlib.interfaces.IHardOreProcessing;
import com.google.common.collect.Maps;

public class OreProcessingRecipes implements IHardOreProcessing {
	private static Map<ItemStack, ItemStack> millRecipes = Maps.<ItemStack, ItemStack>newHashMap();
	private static Map<ItemStack, ItemStack> siftRecipes = Maps.<ItemStack, ItemStack>newHashMap();

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
	public ArrayList<ItemStack> mineHardOreOnce(World world, BlockPos pos, int fortune, IBlockState replacement) {
		IBlockState state = world.getBlockState(pos);
		if(state.getProperties().containsKey(Props.ORE_DENSITY)) {
			int meta = state.getValue(Props.ORE_DENSITY);
			Block block = state.getBlock();
			if(block instanceof IBlockMultiBreak) {
				IBlockMultiBreak bl = (IBlockMultiBreak)block;
				int metaChange = bl.getDensityChangeOnBreak(world, pos, state);
				
				ArrayList<ItemStack> allDrops = (ArrayList<ItemStack>)state.getBlock().getDrops(world, pos, state, fortune);
	
				if(meta > metaChange) {
					world.setBlockState(pos, state.withProperty(Props.ORE_DENSITY, meta-metaChange), 3);
				}
				else {
					world.setBlockState(pos, replacement, 3);
				}
				allDrops.subList(1, allDrops.size()).clear();
				return allDrops;
			}
			/*int metaChange = 1;
			
			ArrayList<ItemStack> allDrops = (ArrayList<ItemStack>)state.getBlock().getDrops(world, pos, state, fortune);
	
			if(meta > metaChange) {
				world.setBlockState(pos, state.withProperty(Props.ORE_DENSITY, meta-metaChange), 3);
			}
			else {
				world.setBlockState(pos, replacement, 3);
			}
			allDrops.subList(1, allDrops.size()).clear();
			return allDrops;*/
		}
		return null;
	}

	@Override
	public ArrayList<ItemStack> mineHardOreOnce(World world, BlockPos pos, int fortune) {
		return mineHardOreOnce(world, pos, fortune, Blocks.AIR.getDefaultState());
	}
}
