package com.draco18s.flowers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwoTallFlower extends TallFlowerBlock {
	protected final FlowerSoil type;

	public TwoTallFlower(FlowerSoil plantType) {
		super(SimpleFlower.flowerProperties());
		type = plantType;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Vec3 offset = state.getOffset(level, pos);
		return SimpleFlower.FLOWER_AABB.move(offset.x, offset.y, offset.z);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return SimpleFlower.mayPlaceOn(type, state) || super.mayPlaceOn(state, level, pos);
	}
}
