package com.draco18s.flowers.block;

import com.mojang.serialization.MapCodec;
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
import net.minecraftforge.common.PlantType;

public class SimpleFlower extends BushBlock {

	/** Blocks are only written by codec for vanilla's block type listing; this one has no parameters to write. */
	private final MapCodec<SimpleFlower> codec = MapCodec.unit(this);

	@Override
	protected MapCodec<? extends BushBlock> codec() {
		return codec;
	}
	protected final PlantType type;
	protected static final VoxelShape FLOWER_AABB = Block.box(4.8, 0.0, 4.8, 11.2, 16.0, 11.2);

	public SimpleFlower(PlantType plantType) {
		this(plantType, flowerProperties());
	}

	public SimpleFlower(PlantType plantType, Properties properties) {
		super(properties);
		type = plantType;
	}

	public static Properties flowerProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Vec3 offset = state.getOffset(level, pos);
		return FLOWER_AABB.move(offset.x, offset.y, offset.z);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return mayPlaceOn(type, state) || super.mayPlaceOn(state, level, pos);
	}

	/** Desert flowers grow on sand and terracotta (what Forge lets a desert plant survive on). */
	static boolean mayPlaceOn(PlantType type, BlockState state) {
		return type == PlantType.DESERT && (state.is(BlockTags.SAND) || state.is(BlockTags.TERRACOTTA));
	}

	@Override
	public PlantType getPlantType(BlockGetter level, BlockPos pos) {
		return type;
	}
}
