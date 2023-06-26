package com.draco18s.farming.block;

import com.draco18s.farming.HarderFarming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CropWinterWheatBlock extends CropBlock {

	public CropWinterWheatBlock() {
		super(Properties.copy(Blocks.WHEAT));
	}

	protected ItemLike getBaseSeedId() {
		return HarderFarming.ModItems.winter_wheat_seeds;
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		super.randomTick(state, world, pos, rand);
		/*Biome bio = world.getBiome(pos);
		if (world.getLightSubtracted(pos, 0) >= 9) {
			int i = this.getAge(state);

			if (i < this.getMaxAge()) {
				if(i == 0) {
					if(bio.getTemperature(pos) <= 0.5) {
						if (ForgeHooks.onCropsGrowPre(world, pos, state, true)) {
							world.setBlockState(pos, this.withAge(i + 1), 2);
							ForgeHooks.onCropsGrowPost(world, pos, state);
						}
					}
				}
				else {
					float f = getGrowthChance(this, world, pos);

					if (ForgeHooks.onCropsGrowPre(world, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
						world.setBlockState(pos, this.withAge(i + 1), 2);
						ForgeHooks.onCropsGrowPost(world, pos, state);
					}
				}
			}
		}*/
	}
}
