package com.draco18s.hardlib.internal;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class OreFlowerData {
	public final IBlockState flower;
	public final int clusterNum;
	public final int clusterSize;
	public final int highConcentrationThreshold;
	
	/**
	 * Data neccessary to grow ore indicator plants
	 * @param plant - the plant blockstate; if it can be a tall flower, pass .withProperty(Props.FLOWER_STALK,true)
	 * @param clusterQuantity - how many flowers to spawn
	 * @param clusterRadius - radius of the cluster
	 * @param threshold - high concentration threshold, for bonemeal (spawns an extra, guaranteed plant)
	 */
	public OreFlowerData(IBlockState plant, int clusterQuantity, int clusterRadius, int threshold) {
		flower = plant;
		highConcentrationThreshold = threshold;
		clusterNum = clusterQuantity;
		clusterSize = clusterRadius;
	}
}
