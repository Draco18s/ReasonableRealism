package com.draco18s.harderores.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class WindvaneBlock extends Block {
	public static final VoxelShape NORTH = makeCuboidShape(0.0D*16, 0.4D*16, 0.0D*16, 0.75D*16, 0.6D*16, 1.0D*16);
	public static final VoxelShape WEST = makeCuboidShape(0.0D*16, 0.4D*16, 0.0D*16, 1.0D*16, 0.6D*16, 0.75D*16);
	public static final VoxelShape UP = makeCuboidShape(0.3D*16, 0.0D*16, 0.4D*16, 1.0D*16, 1.0D*16, 0.6D*16);
	public static final VoxelShape DOWN = makeCuboidShape(0.0D*16, 0.0D*16, 0.4D*16, 0.7D*16, 1.0D*16, 0.6D*16);

	public WindvaneBlock() {
		super(Properties.create(Material.WOOL, MaterialColor.SNOW).hardnessAndResistance(1, 0.1f).sound(SoundType.CLOTH));
		this.setDefaultState(stateContainer.getBaseState().with(BlockStateProperties.FACING, Direction.UP));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		world.getPendingBlockTicks().scheduleTick(context.getPos(), this, 10);

		for(Direction face : Direction.values()) {
			world.getBlockState(pos.offset(face, 1)).neighborChanged(world, pos.offset(face,1), this, pos, false);
			world.getBlockState(pos.offset(face, 2)).neighborChanged(world, pos.offset(face,2), this, pos, false);
		}

		return getDefaultState().with(BlockStateProperties.FACING, Direction.UP);
	}

	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
		breakBlock(worldIn, pos, worldIn.getBlockState(pos));
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
		breakBlock(world, pos, state);
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}
	
	/*@Override
	@Deprecated
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state,world,pos,block,fromPos,isMoving);
		if(block == this && world.getBlockState(fromPos).getBlock() != this) {
			//breakBlock(world, pos, state);
		}
	}*/

	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		for(Direction face : Direction.values()) {
			worldIn.getBlockState(pos.offset(face, 1)).neighborChanged(worldIn, pos.offset(face,1), this, pos, false);
			worldIn.getBlockState(pos.offset(face, 2)).neighborChanged(worldIn, pos.offset(face,2), this, pos, false);
		}
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch(state.get(BlockStateProperties.FACING)) {
		case DOWN:
			return DOWN;
		case EAST:
			return WEST;
		case NORTH:
			return NORTH;
		case SOUTH:
			return NORTH;
		case UP:
			return UP;
		case WEST:
			return WEST;
		default:
			return VoxelShapes.fullCube();
		}
	}

	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return this.getShape(state, worldIn, pos, context);
	}
}
