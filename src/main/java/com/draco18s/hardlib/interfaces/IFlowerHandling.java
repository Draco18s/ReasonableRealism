package com.draco18s.hardlib.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFlowerHandling {
	void spawnFlowerCluster(World world, BlockPos pos, IBlockState state, int radius, int num, int clusterRadius, boolean canBeTallPlant);
}
