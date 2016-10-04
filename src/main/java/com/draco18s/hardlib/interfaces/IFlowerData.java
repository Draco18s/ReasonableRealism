package com.draco18s.hardlib.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.draco18s.hardlib.internal.BlockWrapper;
import com.draco18s.hardlib.internal.OreFlowerData;
import com.draco18s.hardlib.internal.OreFlowerDictator;
import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFlowerData {
	/**
	 * Finds surface, attempts to spawn flowers for all OreFlowerData objects mapped to the ore blockstate
	 * @param world
	 * @param pos
	 * @param ore - A blockwrapper state of the ore at the specified BlockPos
	 */
	void trySpawnFlowerCluster(World world, BlockPos pos, BlockWrapper ore);
	
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
	void doSpawnFlowerCluster(World world, BlockPos pos, IBlockState flowerState, Random r, int num, int clusterRadius, boolean canBeTallPlant, int tallChance);
	
	/**
	 * Registers an ore (with metadata) with its matching indicator plant.<br>
	 * 
	 * @param ore - Use one of:<br> {@link BlockWrapper#BlockWrapper(Block, PropertyInteger)}<br> {@link BlockWrapper#BlockWrapper(Block, int)}<br>{@link BlockWrapper#BlockWrapper(IBlockState, int)} 
	 * @param dictator
	 * @param data
	 */
	void addOreFlowerData(BlockWrapper ore, OreFlowerDictator dictator, OreFlowerData data);
	/*void addOreFlowerData(@Nonnull IBlockState ore, @Nonnull OreFlowerDictator dictator, @Nonnull OreFlowerData data);
	
	/**
	 * Registers an ore (wildcard) with its matching indicator plant.<br>
	 * 
	 * @param ore
	 * @param dictator
	 * @param data
	 */
	/*void addOreFlowerData(@Nonnull Block ore, @Nonnull OreFlowerDictator dictator, @Nonnull OreFlowerData data);*/

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
	
	/**
	 * Get the default flower for a given flower type IProperty
	 * @return
	 */
	IBlockState getDefaultFlower(IProperty prop);
}
