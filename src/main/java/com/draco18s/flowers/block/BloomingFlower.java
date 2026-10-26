package com.draco18s.flowers.block;

import com.draco18s.hardlib.api.block.state.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;

/** A flower that now and then switches between blooming and not. */
public class BloomingFlower extends SimpleFlower {

	public BloomingFlower(FlowerSoil plantType, Properties properties) {
		super(plantType, properties.randomTicks());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockProperties.BLOOM_PHASE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(BlockProperties.BLOOM_PHASE, context.getLevel().getRandom().nextBoolean());
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		if(rand.nextInt(100) == 0) {
			level.setBlock(pos, state.cycle(BlockProperties.BLOOM_PHASE), 3);
		}
	}
}
