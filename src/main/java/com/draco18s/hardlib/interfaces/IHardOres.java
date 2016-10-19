package com.draco18s.hardlib.interfaces;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHardOres {
	/**
	 * Returns true if the block passed is a HardOre block
	 * @param b
	 * @return
	 */
	boolean isHardOre(IBlockState b);
	
	/**
	 * Mines the hard ore block at (x,y,z) and returns the resulting ArrayList<ItemStack> drops.<br/>
	 * The replacement block and meta are placed into the world when the ore being mined is fully depleted.
	 * @param world
	 * @param pos
	 * @param fortune - fortune enchantment level for miner
	 * @param replacement (optional) - The blockstate to place if completely mined.  Default: air
	 * @return drops
	 */
	public List<ItemStack> mineHardOreOnce(World world, BlockPos pos, int fortune, IBlockState replacement);
	
	/**
	 * Mines the hard ore block at (x,y,z) and returns the resulting ArrayList<ItemStack> drops.<br/>
	 * The replacement block and meta are placed into the world when the ore being mined is fully depleted.
	 * @param world
	 * @param pos
	 * @param fortune - fortune enchantment level for miner
	 * @param replacement (optional) - The blockstate to place if completely mined.  Default: air
	 * @return drops
	 */
	public List<ItemStack> mineHardOreOnce(World world, BlockPos pos, int fortune);

	/**
	 * Gets the drops for the block as if it was mined once.
	 * @param world
	 * @param pos
	 * @param fortune - fortune enchantment level for miner
	 * @return
	 */
	public List<ItemStack> getHardOreDropsOnce(World world, BlockPos pos, int fortune);
}
