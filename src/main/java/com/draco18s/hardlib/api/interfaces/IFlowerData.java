package com.draco18s.hardlib.api.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;

public interface IFlowerData {
	/**
	 * Finds surface, attempts to spawn flowers for all OreFlowerData objects mapped to the ore blockstate
	 * @param world
	 * @param pos
	 * @param ore - A blockwrapper state of the ore at the specified BlockPos
	 */
	void trySpawnFlowerCluster(Level world, BlockPos pos, BlockWrapper ore);
	
	/**
	 * Spawns a cluster of flowers (or other block passed) in the given location, radius, and number.
	 * @param world
	 * @param pos
	 * @param ore - the state of the ore at the specified BlockPos
	 * @param radius
	 * @param num
	 * @param clusterRadius
	 * @param canBeTallPlant
	 * @param tallChance - if the flower can be two blocks tall, weighted probability of doing so
	 */
	void doSpawnFlowerCluster(Level world, BlockPos pos, BlockState flowerState, Random r, int num, int clusterRadius, boolean canBeTallPlant);
	
	/**
	 * Registers an ore (with metadata) with its matching indicator plant.<br>
	 * 
	 * @param oretag - Use one of:<br> {@link BlockWrapper#BlockWrapper(Block, PropertyInteger)}<br> {@link BlockWrapper#BlockWrapper(Block, int)}<br>{@link BlockWrapper#BlockWrapper(IBlockState, int)} 
	 * @param dictator
	 * @param data
	 */
	void addOreFlowerData(BlockWrapper oretag, OreFlowerDictator dictator, OreFlowerData data);
	
	/**
	 * Gets the hash of all of the registered ore blocks for flower data.
	 * @return
	 */
	Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> getOreList();

	/**
	 * Get the dictator for the ore
	 * @param ore
	 * @return
	 */
	OreFlowerDictator getDictatorForOre(BlockWrapper ore);
	
	/**
	 * Get all OreFlowerData for a given ore
	 * @param ore
	 * @return
	 */
	@Nullable Iterable<OreFlowerData> getDataForOre(BlockWrapper ore);
}