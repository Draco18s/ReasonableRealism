package com.draco18s.farming.block;

import java.util.Random;

import com.draco18s.farming.FarmingBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BlockCropWinterWheat extends BlockCrops {
	public BlockCropWinterWheat() {

	}

	protected Item getSeed() {
		return FarmingBase.winterWheatSeeds;
	}

	protected Item getCrop() {
		return Items.WHEAT;
	}

	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		Biome bio = world.getBiome(pos);
		if (world.getLightFromNeighbors(pos.up()) >= 9) {
			int i = this.getAge(state);

			if (i < this.getMaxAge()) {
				if(i == 0) {
					if(bio.getTemperature() <= 0.5) {
						if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, pos, state, true)) {
							world.setBlockState(pos, this.withAge(i + 1), 2);
							net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
						}
					}
				}
				else {
					float f = getGrowthChance(this, world, pos);
	
					if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
						world.setBlockState(pos, this.withAge(i + 1), 2);
						net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
					}
				}
			}
		}
	}
}
