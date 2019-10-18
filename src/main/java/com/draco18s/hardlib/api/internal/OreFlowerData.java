package com.draco18s.hardlib.api.internal;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;

/**
 * Data neccessary to grow ore indicator plants
 * @author Draco18s
 *
 */
public class OreFlowerData {
	public final BlockState flower;
	public final int clusterNum;
	public final int clusterSize;
	public final int highConcentrationThreshold;
	
	/**
	 * Data neccessary to grow ore indicator plants
	 * @param plant - the plant blockstate; if it can be a tall flower, pass .withProperty(Props.FLOWER_STALK,true)
	 * @param clusterQuantity - how many flowers to spawn
	 * @param clusterRadius - radius of the cluster
	 * @param threshold - high concentration threshold, for bonemeal (spawns an extra, guaranteed plants)
	 */
	public OreFlowerData(@Nonnull BlockState plant, int clusterQuantity, int clusterRadius, int threshold) {
		flower = plant;
		highConcentrationThreshold = threshold;
		clusterNum = clusterQuantity;
		clusterSize = clusterRadius;
	}
}