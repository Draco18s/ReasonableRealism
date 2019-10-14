package com.draco18s.harderfarming.block;

import java.util.Random;

import com.draco18s.harderfarming.HarderFarming;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeHooks;

public class CropWinterWheatBlock extends CropsBlock {

	public CropWinterWheatBlock() {
		super(Properties.create(Material.PLANTS).tickRandomly().hardnessAndResistance(0.0F).doesNotBlockMovement().sound(SoundType.CROP));
	}

	protected IItemProvider getSeedsItem() {
		return HarderFarming.ModItems.winter_wheat_seeds;
	}

	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random rand) {
		Biome bio = world.getBiome(pos);
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
		}
	}
}
