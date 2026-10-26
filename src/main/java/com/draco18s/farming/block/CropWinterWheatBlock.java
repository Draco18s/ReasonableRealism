package com.draco18s.farming.block;

import com.draco18s.farming.HarderFarming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
/*import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.util.CubicSpline;*/
import net.minecraft.world.level.levelgen.DensityFunctions.Spline.Coordinate;
import net.minecraft.world.level.levelgen.DensityFunctions.Spline.Point;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class CropWinterWheatBlock extends CropBlock {

	public CropWinterWheatBlock() {
		super(Properties.ofFullCopy(Blocks.WHEAT));
	}

	protected ItemLike getBaseSeedId() {
		return HarderFarming.ModItems.winter_wheat_seeds;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		Biome bio = world.getBiome(pos).value();
		if (world.getRawBrightness(pos, 0) < 9) return;
		int i = this.getAge(state);
		if (i >= this.getMaxAge()) return;
		if(i == 0) {
			if(bio.getBaseTemperature() <= 0.5) {
				if (CommonHooks.canCropGrow(world, pos, state, true)) {
					world.setBlock(pos, this.getStateForAge(i + 1), 2);
					CommonHooks.fireCropGrowPost(world, pos, state);
				}
			}
		}
		else {
			float f = getGrowthSpeed(state, world, pos);

			if (CommonHooks.canCropGrow(world, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
				world.setBlock(pos, this.getStateForAge(i + 1), 2);
				CommonHooks.fireCropGrowPost(world, pos, state);
			}
		}
	}
}
