package com.draco18s.harderores.block;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WindvaneBlock extends Block {
	public static final VoxelShape FULL_BLOCK = box(0,0,0,16,16,16);
	public static final VoxelShape TINY_BLOCK = box(7,7,7,9,9,9);
	public static final VoxelShape NORTH = box(0.0D*16, 0.4D*16, 0.0D*16, 0.75D*16, 0.6D*16, 1.0D*16);
	public static final VoxelShape WEST = box(0.0D*16, 0.4D*16, 0.0D*16, 1.0D*16, 0.6D*16, 0.75D*16);
	public static final VoxelShape UP = box(0.3D*16, 0.0D*16, 0.4D*16, 1.0D*16, 1.0D*16, 0.6D*16);
	public static final VoxelShape DOWN = box(0.0D*16, 0.0D*16, 0.4D*16, 0.7D*16, 1.0D*16, 0.6D*16);

	public WindvaneBlock() {
		super(Properties.of(Material.CLOTH_DECORATION));
		registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING);
	}
	
	@Override
	@Deprecated
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction dir = Direction.UP;
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		world.scheduleTick(context.getClickedPos(), this, 10);

		for(Direction face : Direction.values()) {
			BlockPos pos2 = pos.relative(face, 1);
			BlockState state2 = world.getBlockState(pos2);
			if(state2.getBlock() == HarderOres.ModBlocks.machine_axel) {
				if(shouldConnect(face, state2))
					dir = face.getOpposite();
			}
			state2.neighborChanged(world, pos2, this, pos, false);

			pos2 = pos.relative(face, 2);
			state2 = world.getBlockState(pos2);
			if(state2.getBlock() == HarderOres.ModBlocks.machine_axel) {
				if(shouldConnect(face, state2))
					dir = face.getOpposite();
			}
			state2.neighborChanged(world, pos2, this, pos, false);
		}

		return defaultBlockState().setValue(BlockStateProperties.FACING, dir);
	}
	
	private boolean shouldConnect(Direction dir, BlockState axelState) {
		AxelOrientation axelProp = axelState.getValue(BlockProperties.AXEL_ORIENTATION);
		Direction axelFacing = axelState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		if(axelFacing == dir || axelFacing == dir.getOpposite()) return false;
		if(axelProp == AxelOrientation.NONE || axelProp == AxelOrientation.HUB) return true;
		return false;
	}

	@Override
	@Deprecated
	public void wasExploded(Level worldIn, BlockPos pos, Explosion explosionIn) {
		breakBlock(worldIn, pos, worldIn.getBlockState(pos));
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
	{
		breakBlock(world, pos, state);
		return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
	}
	
	@SuppressWarnings("deprecation")
	public void breakBlock(Level worldIn, BlockPos pos, BlockState state) {
		for(Direction face : Direction.values()) {
			worldIn.getBlockState(pos.relative(face, 1)).neighborChanged(worldIn, pos.relative(face,1), this, pos, false);
			worldIn.getBlockState(pos.relative(face, 2)).neighborChanged(worldIn, pos.relative(face,2), this, pos, false);
		}
	}
	
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
		switch(state.getValue(BlockStateProperties.FACING)) {
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
				return TINY_BLOCK;
		}
	}


	@Deprecated
	public VoxelShape getBlockSupportShape(BlockState p_60581_, BlockGetter p_60582_, BlockPos p_60583_) {
		return Shapes.empty();
	}
}
