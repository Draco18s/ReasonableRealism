package com.draco18s.hardlib.internal;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class OreFlowerData {
	public final IBlockState flower;
	public final int clusterNum;
	public final int clusterSize;
	public final int highConcentrationThreshold;
	public final int twoBlockChance;
	
	/**
	 * Data neccessary to grow ore indicator plants
	 * @param plant - the plant blockstate; if it can be a tall flower, pass .withProperty(Props.FLOWER_STALK,true)
	 * @param clusterQuantity - how many flowers to spawn
	 * @param clusterRadius - radius of the cluster
	 * @param threshold - high concentration threshold, for bonemeal (spawns an extra, guaranteed plants)
	 */
	public OreFlowerData(@Nonnull IBlockState plant, int clusterQuantity, int clusterRadius, int threshold) {
		flower = plant;
		highConcentrationThreshold = threshold;
		clusterNum = clusterQuantity;
		clusterSize = clusterRadius;
		twoBlockChance = 0;
	}

	public OreFlowerData(@Nonnull IBlockState plant, int clusterQuantity, int clusterRadius, int threshold, int twoTallWeight) {
		flower = plant;
		highConcentrationThreshold = threshold;
		clusterNum = clusterQuantity;
		clusterSize = clusterRadius;
		twoBlockChance = twoTallWeight;
	}
}
