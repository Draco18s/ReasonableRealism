package com.draco18s.hardlib.interfaces;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHardOreProcessing {
	/**
	 * The sifter "autocrafts" tiny dust piles to large dust piles.  Typically the crafting table recipe will be a
	 * full grid (9 tiny dusts) and craft into 1 large dust and the sifter will sift 8 to 1.
	 * @param input ItemStack including metadata and size
	 * @param output ItemStack including metadata and size
	 * @param registerOutput Pass true to register the output stack as a 1:1 sift (this prevents some items which
	 *  can be created normally from clogging the sifter, such as bonemeal).
	 */
	public void addSiftRecipe(ItemStack input, ItemStack output, boolean registerOutput);
	public void addSiftRecipe(ItemStack input, ItemStack output);
	public void addSiftRecipe(String input, int stackSize, ItemStack output, boolean registerOutput);
	public void addSiftRecipe(String input, int stackSize, ItemStack output);
	
	/**
	 * The millstone will grind "raw" materials down into "dust" materials, typically tiny dust piles.
	 * @param input ItemStack to be ground
	 */
	public void addMillRecipe(ItemStack input, ItemStack output);
	
	/**
	 * Returns the number items needed for the sifter recipes (minimum input stack size).
	 * @param itemStack
	 * @return minimum stack size for input in order to sift.
	 */
	public int getSiftAmount(ItemStack itemStack);
	
	/**
	 * Gets the item stack output for a given item stack input.
	 * @param itemstack1
	 * @param checkStackSize - whether or not we care if we have enough input or we just need to know if the input stack is valid
	 * @return an ItemStack sifting result. <b>Must</b> call {@link ItemSack#copy}.
	 */
	public ItemStack getSiftResult(ItemStack itemstack1, boolean checkStackSize);

	/**
	 * Returns the item stack result from milling.
	 * @param itemStack
	 * @return an ItemStack milling result. <b>Must</b> call {@link ItemSack#copy}.
	 */
	public ItemStack getMillResult(ItemStack itemStack);
	
	/**
	 * Mines the hard ore block at (x,y,z) and returns the resulting ArrayList<ItemStack> drops.<br/>
	 * The replacement block and meta are placed into the world when the ore being mined is fully depleted.
	 * @param world
	 * @param pos
	 * @param fortune - fortune enchantment level for miner
	 * @param replacement (optional) - The BlockState to place if completely mined.  Default: air
	 * @return drops
	 */
	public ArrayList<ItemStack> mineHardOreOnce(World world, BlockPos pos, int fortune, IBlockState replacement);
	
	/**
	 * Mines the hard ore block at (x,y,z) and returns the resulting ArrayList<ItemStack> drops.<br/>
	 * The replacement block and meta are placed into the world when the ore being mined is fully depleted.
	 * @param world
	 * @param pos
	 * @param fortune - fortune enchantment level for miner
	 * @param replacement (optional) - The BlockState to place if completely mined.  Default: air
	 * @return drops
	 */
	public ArrayList<ItemStack> mineHardOreOnce(World world, BlockPos pos, int fortune);
}
