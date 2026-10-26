package com.draco18s.flowers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SimpleFlower extends BushBlock {
	protected final FlowerSoil type;
	protected static final VoxelShape FLOWER_AABB = Block.box(4.8, 0.0, 4.8, 11.2, 16.0, 11.2);

	public SimpleFlower(FlowerSoil plantType, Properties properties) {
		super(flowerProperties(properties));
		type = plantType;
	}

	/** Sets a flower's properties. */
	public static Properties flowerProperties(Properties properties) {
		return properties.mapColor(MapColor.PLANT).pushReaction(PushReaction.POPPED).noCollision().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Vec3 offset = state.getOffset(pos);
		return FLOWER_AABB.move(offset.x, offset.y, offset.z);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return mayPlaceOn(type, state) || super.mayPlaceOn(state, level, pos);
	}

	/** Desert flowers grow on sand and terracotta (what Forge let a desert plant survive on). */
	static boolean mayPlaceOn(FlowerSoil type, BlockState state) {
		return type == FlowerSoil.DESERT && (state.is(BlockTags.SAND) || state.is(BlockTags.TERRACOTTA));
	}
}
