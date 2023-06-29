package com.draco18s.harderores.block;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.AxelBlockEntity;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.interfaces.IMechanicalPower;
import com.draco18s.hardlib.api.internal.block.ModEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AxelBlock extends ModEntityBlock {
	public static final VoxelShape WEST = 	axel(0, 2, 2, 16, 14, 14);
	public static final VoxelShape UP = 	axel(2, 0, 2, 14, 16, 14);
	public static final VoxelShape NORTH = 	axel(2, 2, 0, 14, 14, 16);
	public static final VoxelShape GEARS = gearBox();
	
	public AxelBlock() {
		super(Properties.of(Material.WOOD).strength(2).sound(SoundType.WOOD));
		registerDefaultState(this.stateDefinition.any().setValue(BlockProperties.AXEL_ORIENTATION, AxelOrientation.NONE).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	private static VoxelShape axel(int nx, int ny, int nz, int mx, int my, int mz) {
		VoxelShape p1 = box(nx, ny, nz, mx, my, mz);
		VoxelShape p2;
		if(ny == 0) {
			p2 = Shapes.joinUnoptimized(box(0, 0, 6, 16, 16, 10), box(6, 0, 0, 10, 16, 16), BooleanOp.OR);
		}
		else if(nx == 0) {
			p2 = Shapes.joinUnoptimized(box(0, 6, 0, 16, 10, 16), box(0, 0, 6, 16, 16, 10), BooleanOp.OR);
		}
		else {
			p2 = Shapes.joinUnoptimized(box(6, 0, 0, 10, 16, 16), box(0, 6, 0, 16, 10, 16), BooleanOp.OR);
		}
		return Shapes.join(p1, p2, BooleanOp.OR);
	}

	private static VoxelShape gearBox() {
		VoxelShape p1 = Shapes.block();
		p1 = Shapes.joinUnoptimized(p1, Block.box(0.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D), BooleanOp.ONLY_FIRST);
		p1 = Shapes.joinUnoptimized(p1, Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), BooleanOp.ONLY_FIRST);
		p1 = Shapes.join(p1, Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 16.0D), BooleanOp.ONLY_FIRST);
		return p1;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockProperties.AXEL_ORIENTATION);
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction dir = BlockProperties.getFacingFromEntity(context.getClickedPos(), context.getPlayer());
		BlockState state = this.defaultBlockState();
		context.getLevel().scheduleTick(context.getClickedPos(), this, 2);
		state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, dir);
		return state;
	}
	
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block p_60512_, BlockPos p_60513_, boolean p_60514_) {
		this.checkPlacement(world, pos, state);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		this.checkPlacement(world, pos, state);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.HUB ? new AxelBlockEntity(pos, state) : null;
	}
	
	public void checkPlacement(Level worldIn, BlockPos pos, BlockState stateIn) {
		BlockState state = stateIn;
		Direction facing = stateIn.getValue(BlockStateProperties.HORIZONTAL_FACING);
		AxelOrientation axelState = stateIn.getValue(BlockProperties.AXEL_ORIENTATION);
		if(worldIn.getBlockState(pos.above()).getBlock() == this) {
			if(axelState != AxelOrientation.UP)
				state = state.setValue(BlockProperties.AXEL_ORIENTATION, AxelOrientation.UP);
		}
		else {
			if(axelState != AxelOrientation.GEARS && worldIn.getBlockState(pos.below()).getBlock() == this) {
				state = state.setValue(BlockProperties.AXEL_ORIENTATION, AxelOrientation.GEARS);
				worldIn.scheduleTick(pos.below(), this, 2);
			}
			BlockState oState = worldIn.getBlockState(pos.relative(facing));
			boolean shouldRotate = oState.getBlock() != this;
			
			if(shouldRotate && worldIn.getBlockState(pos.relative(facing.getOpposite())).getBlock() == this) {
				state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite());
				worldIn.scheduleTick(pos.relative(facing.getOpposite()), this, 2);
			}
			else if(shouldRotate && worldIn.getBlockState(pos.relative(facing.getClockWise())).getBlock() == this) {
				state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getClockWise());
				worldIn.scheduleTick(pos.relative(facing.getClockWise()), this, 2);
			}
			else if(shouldRotate && worldIn.getBlockState(pos.relative(facing.getCounterClockWise())).getBlock() == this) {
				state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getCounterClockWise());
				worldIn.scheduleTick(pos.relative(facing.getOpposite()), this, 2);
			}
			
			if(worldIn.getBlockEntity(pos.below()) != null && worldIn.getBlockEntity(pos.below()).getCapability(IMechanicalPower.MECHANICAL_POWER_CAPABILITY, Direction.DOWN).isPresent()) {
				if(worldIn.getBlockState(pos.relative(facing)).getBlock() != this && worldIn.getBlockState(pos.relative(facing.getOpposite())).getBlock() == this) {
					state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite());
					worldIn.scheduleTick(pos.relative(facing.getOpposite()), this, 2);
				}
				state = state.setValue(BlockProperties.AXEL_ORIENTATION, AxelOrientation.GEARS);
				worldIn.scheduleTick(pos.relative(facing), this, 2);
			}
			else {
				Direction[] checks = { Direction.UP, Direction.DOWN, facing.getClockWise(), facing.getCounterClockWise() };
				if(checkForVanes(worldIn, pos, checks)) {
					state = state.setValue(BlockProperties.AXEL_ORIENTATION, AxelOrientation.HUB);
				}
				else if(state.getValue(BlockProperties.AXEL_ORIENTATION) != AxelOrientation.GEARS){
					state = state.setValue(BlockProperties.AXEL_ORIENTATION, AxelOrientation.NONE);
				}
			}
		}
		worldIn.setBlock(pos, state, 3);
	}

	private boolean checkForVanes(Level worldIn, BlockPos pos, Direction[] checks) {
		for(Direction f2 : checks) {
			if(worldIn.getBlockState(pos.relative(f2,1)).getBlock() != HarderOres.ModBlocks.machine_windvane) return false;
			if(worldIn.getBlockState(pos.relative(f2,1)).getBlock() != HarderOres.ModBlocks.machine_windvane) return false;
		}
		return true;
	}
	
	public static final VoxelShape FULL_BLOCK = box(1,1,1,15,15,15);

	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext context) {
		AxelOrientation axelState = state.getValue(BlockProperties.AXEL_ORIENTATION);
		if(axelState == AxelOrientation.UP) return UP;
		if(axelState == AxelOrientation.GEARS) return GEARS;
		Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		switch(dir) {
			case EAST:
			case WEST:
				return WEST;
			case NORTH:
			case SOUTH:
				return NORTH;
			default:
				break;
		
		}
		return FULL_BLOCK;
	}
}
