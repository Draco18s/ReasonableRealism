package com.draco18s.harderores.block;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.SluiceBlockEntity;
import com.draco18s.hardlib.api.internal.block.ModEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;

public class SluiceBlock extends ModEntityBlock {
	public static final VoxelShape THIN_SLAB = box(0,0,0,16,1,16);
	public static final VoxelShape FLAT_SLAB = box(0,0,0,16,2,16);
	public static final VoxelShape THICK_SLAB = box(0,0,0,16,6,16);
	public static final VoxelShape MOST_BLOCK = box(0,0,0,16,10,16);
	public static final VoxelShape NEARLY_BLOCK = box(0,0,0,16,15,16);
	public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 9);
	/** Each level's floor, solid from the bottom of the block (see floorHeight). */
	private static final VoxelShape[] FLOORS = new VoxelShape[10];
	static {
		for(int level = 0; level <= 9; level++) {
			FLOORS[level] = box(0, 0, 0, 16, floorHeight(level), 16);
		}
	}

	public SluiceBlock(Properties props) {
		super(props.mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).ignitedByLava());
		registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));		
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
		builder.add(LEVEL);
	}

	@Override
	@Deprecated
	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighbor, @Nullable Orientation orientation, boolean isMoving) {
		super.neighborChanged(state, world, pos, neighbor, orientation, isMoving);
		BlockState newState = getUpdatedState(world, pos, state);
		world.setBlock(pos, newState, 3);
		if(newState.getValue(LEVEL) > 1) {
			handleOutflow(world, newState, pos);
		}
	}
	
	private void handleOutflow(Level world, BlockState state, BlockPos pos) {
		Direction outDir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		int outamt = state.getValue(LEVEL) - 1;
		BlockPos outside = pos.relative(outDir);
		BlockState outsideState = world.getBlockState(outside);
		// Water washes away anything that isn't solid, which a sluice isn't: don't wash away the next one.
		if(!outsideState.is(this) && outsideState.canBeReplaced(Fluids.WATER)) {
			BlockState waterstate = Fluids.WATER.getFlowing(outamt, false).createLegacyBlock();
			FluidType flty = world.getBlockState(outside).getFluidState().getFluidType();
			if(flty == Fluids.WATER.getFluidType() || flty == Fluids.FLOWING_WATER.getFluidType()) {
				return;
			}
			world.setBlock(outside, waterstate, 3);
		}
	}

	private BlockState getUpdatedState(Level world, BlockPos pos, BlockState thisState) {
		if(thisState.getBlock() != this) return null;
		int height = 0;
		Direction facing = Direction.NORTH;
		for(Direction face : Direction.values()) {
			if(face == Direction.UP || face == Direction.DOWN) continue;
			BlockState bl = world.getBlockState(pos.relative(face,1));
			FluidState fl = bl.getFluidState();
			FluidType flty = fl.getFluidType();
			if(bl.getBlock() != this && (flty == Fluids.WATER.getFluidType() || flty == Fluids.FLOWING_WATER.getFluidType())) {
				if(fl.getAmount() > height) {
					facing = face.getOpposite();
					height = fl.getAmount();
				}
			}
		}
		height++;
		for(Direction face : Direction.values()) {
			if(face == Direction.UP || face == Direction.DOWN) continue;
			BlockState bl = world.getBlockState(pos.relative(face,1));
			if(bl.getBlock() == this /*&& bl.getValue(BlockStateProperties.HORIZONTAL_FACING) == face.getOpposite()*/) {
				if(bl.getValue(LEVEL) > height) {
					facing = face.getOpposite();
					height = bl.getValue(LEVEL);
				}
			}
		}
		BlockState abv = world.getBlockState(pos.above());
		FluidState abvF = abv.getFluidState();
		FluidType abvL = abvF.getFluidType();
		if(abvL == Fluids.WATER.getFluidType() || abvL == Fluids.FLOWING_WATER.getFluidType()) {
			if(height <= 1)
				height = 11;
			else
				world.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), UPDATE_ALL);
		}

		return thisState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing).setValue(LEVEL, Math.max(height-2,0));
	}

	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		super.destroy(world, pos, state);
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_ALL);
	}

	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		int l = state.getValue(LEVEL);
		if(l > 8) l = 8;
		if(l > 0)
			return Fluids.FLOWING_WATER.defaultFluidState().setValue(FlowingFluid.LEVEL, l);
		else return super.getFluidState(state);
	}

	/**
	 * How high (in pixels) the floor stands at a level: a pixel under the water's surface (the water is
	 * level/9 of a block deep, at most 8/9), so the ridges on it sit just under water. At least a pixel.
	 * The block models (sluice_0 to sluice_8) are built from the same numbers.
	 */
	public static double floorHeight(int level) {
		int amount = Math.min(level, 8);
		return Math.max(1, Math.floor((amount * 16 / 9.0 - 1) * 2) / 2);
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return FLOORS[state.getValue(LEVEL)];
	}

	@Override
	@Deprecated
	public VoxelShape getBlockSupportShape(BlockState p_60581_, BlockGetter p_60582_, BlockPos p_60583_) {
		return FLAT_SLAB;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos blockpos = pos.relative(Direction.DOWN);
		return world.getBlockState(blockpos).isFaceSturdy(world, blockpos, Direction.UP, SupportType.RIGID);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SluiceBlockEntity(pos,state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> betType) {
		return level.isClientSide() ? null : createTickerHelper(betType, HarderOres.ModBlockEntities.sluice, SluiceBlockEntity::tick);
	}
}
