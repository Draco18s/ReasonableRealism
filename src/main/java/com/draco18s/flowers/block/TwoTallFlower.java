package com.draco18s.flowers.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;

public class TwoTallFlower extends TallFlowerBlock {
	protected final PlantType type;
	protected static final VoxelShape FLOWER_AABB = Block.makeCuboidShape(4.8, 0.0, 4.8, 11.2, 16.0, 11.2);

	public TwoTallFlower(PlantType plantType) {
		super(Block.Properties.create(Material.PLANTS).sound(SoundType.PLANT).hardnessAndResistance(0).doesNotBlockMovement());
		type = plantType;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Vec3d vec3d = state.getOffset(worldIn, pos);
		return FLOWER_AABB.withOffset(vec3d.x, vec3d.y, vec3d.z);
	}
}
