package com.draco18s.flowers.block;

import java.util.Random;

import com.draco18s.hardlib.api.block.state.BlockProperties;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

public class BloomingFlower extends SimpleFlower {

	public BloomingFlower(PlantType plantType) {
		super(plantType, Block.Properties.create(Material.PLANTS).sound(SoundType.PLANT).hardnessAndResistance(0).doesNotBlockMovement().tickRandomly());
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(BlockProperties.BLOOM_PHASE);
	}

	@Override
	@Deprecated
	public void tick(BlockState state, World world, BlockPos pos, Random rand) {
		super.tick(state, world, pos, rand);
		if(rand.nextInt(100) == 0) {
			world.setBlockState(pos, state.with(BlockProperties.BLOOM_PHASE, !state.get(BlockProperties.BLOOM_PHASE)), 3);
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		state = state.with(BlockProperties.BLOOM_PHASE, world.getRandom().nextBoolean());
		world.setBlockState(pos, state, 3);
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}
}
