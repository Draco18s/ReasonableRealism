package com.draco18s.hardlib.api.interfaces;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IHardOres {
	/**
	 * Returns true if the block passed is a HardOre block
	 * @param b
	 * @return
	 */
	boolean isHardOre(BlockState b);
	
	/**
	 * Mines the hard ore block at (x,y,z) and returns the resulting ArrayList<ItemStack> drops.<br/>
	 * The replacement block and meta are placed into the world when the ore being mined is fully depleted.
	 * @param world
	 * @param pos
	 * @param fortune - fortune enchantment level for miner
	 * @param replacement (optional) - The blockstate to place if completely mined.  Default: air
	 * @return drops
	 */
	public List<ItemStack> mineHardOreOnce(Level world, ServerPlayer entityPlayer, BlockPos pos, ItemStack stack, BlockState replacement);
	
	/**
	 * Mines the hard ore block at (x,y,z) and returns the resulting ArrayList<ItemStack> drops.<br/>
	 * The replacement block and meta are placed into the world when the ore being mined is fully depleted.
	 * @param world
	 * @param pos
	 * @param fortune - fortune enchantment level for miner
	 * @param replacement (optional) - The blockstate to place if completely mined.  Default: air
	 * @return drops
	 */
	public List<ItemStack> mineHardOreOnce(Level world, ServerPlayer entityPlayer, BlockPos pos, ItemStack stack);

	/**
	 * Gets the drops for the block as if it was mined once.
	 * @param world
	 * @param pos
	 * @param fortune - fortune enchantment level for miner
	 * @return
	 */
	public List<ItemStack> getHardOreDropsOnce(Level world, BlockPos pos, ItemStack stack);
}