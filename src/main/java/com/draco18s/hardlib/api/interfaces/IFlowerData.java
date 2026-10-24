package com.draco18s.hardlib.api.interfaces;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface IFlowerData {
	/**
	 * Finds the surface above an ore block and, by chance, spawns a cluster of each of the ore's indicator plants
	 * nearby. During worldgen (a WorldGenRegion) only the writable part of the region gets flowers.
	 * @param world
	 * @param pos - the ore block
	 * @param ore - the wrapper registered for the ore block at pos
	 */
	void trySpawnFlowerCluster(LevelAccessor world, BlockPos pos, BlockWrapper ore);

	/**
	 * Spawns a cluster of flowers (or other plant) around the given location. Two-tall plants are placed whole.
	 * @param world
	 * @param pos - the cluster's center; plants are placed up to 5 blocks above or below it
	 * @param flowerState
	 * @param r
	 * @param num - how many plants
	 * @param clusterRadius - the width of the cluster
	 */
	void doSpawnFlowerCluster(LevelAccessor world, BlockPos pos, BlockState flowerState, RandomSource r, int num, int clusterRadius);

	/**
	 * Registers an ore with its matching indicator plant.<br>
	 * @param ore - Use one of:<br> {@link BlockWrapper#BlockWrapper(Block, net.minecraft.world.level.block.state.properties.IntegerProperty)}<br> {@link BlockWrapper#BlockWrapper(Block, int)}
	 * @param dictator
	 * @param data
	 */
	void addOreFlowerData(BlockWrapper ore, OreFlowerDictator dictator, OreFlowerData data);

	/**
	 * Gets all of the registered ore blocks with their flower data.
	 */
	Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> getOreList();

	/**
	 * The wrapper registered for a block, if it is a registered ore.
	 */
	@Nullable BlockWrapper getOreWrapper(Block block);

	/**
	 * Get the dictator for the ore
	 */
	@Nullable OreFlowerDictator getDictatorForOre(BlockWrapper ore);

	/**
	 * Get all OreFlowerData for a given ore
	 */
	@Nullable Iterable<OreFlowerData> getDataForOre(BlockWrapper ore);
}
