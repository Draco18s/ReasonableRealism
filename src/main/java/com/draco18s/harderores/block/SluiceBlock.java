package com.draco18s.harderores.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.harderores.entity.SluiceTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class SluiceBlock extends Block {
	protected static final VoxelShape PARTIAL_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
	public static BooleanProperty FLOWING = BooleanProperty.create("flowing");
	
	public SluiceBlock() {
		super(Properties.create(Material.WOOD, MaterialColor.WATER).hardnessAndResistance(2).harvestTool(ToolType.AXE).harvestLevel(1).sound(SoundType.WOOD));
		this.setDefaultState(stateContainer.getBaseState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).with(FLOWING, false));
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
		builder.add(FLOWING);
	}

	@Override
	@Deprecated
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SluiceTileEntity();
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		context.getWorld().getPendingBlockTicks().scheduleTick(context.getPos(), this, 1);
		return super.getStateForPlacement(context);
		//return getUpdatedState(context.getWorld(), context.getPos(), context.getWorld().getBlockState(context.getPos()));
	}

	@Override
	@Deprecated
	public void tick(BlockState state, World world, BlockPos pos, Random random) {
		world.setBlockState(pos, getUpdatedState(world, pos, world.getBlockState(pos)), 3);
		((SluiceTileEntity)world.getTileEntity(pos)).updateNeighborChanged();
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state,world,pos,block,fromPos,isMoving);
		if(!world.getBlockState(pos.down()).func_224755_d(world, pos, Direction.UP)) {
			if (!world.isRemote)  {
				world.destroyBlock(pos, true);
			}
		}
		if(world.getBlockState(pos).getBlock() == this) {
			world.getPendingBlockTicks().scheduleTick(pos, this, 1);
		}
	}

	private BlockState getUpdatedState(IWorld world, BlockPos pos, BlockState thisState) {
		if(thisState.getBlock() != this) return null;
		for(Direction face : Direction.values()) {
			if(face == Direction.UP || face == Direction.DOWN) continue;
			BlockState bl = world.getBlockState(pos.offset(face,1));
			IFluidState fl = bl.getFluidState();
			if(fl.getFluid() == Fluids.WATER) {
				return thisState.with(BlockStateProperties.HORIZONTAL_FACING, face.getOpposite());
			}
		}
		for(Direction face : Direction.values()) {
			if(face == Direction.UP || face == Direction.DOWN) continue;
			BlockState bl = world.getBlockState(pos.offset(face,1));
			IFluidState fl = bl.getFluidState();
			if(fl.getFluid() == Fluids.FLOWING_WATER) {
				return thisState.with(BlockStateProperties.HORIZONTAL_FACING, face.getOpposite());
			}
		}
		for(Direction face : Direction.values()) {
			if(face == Direction.UP || face == Direction.DOWN) continue;
			BlockState bl = world.getBlockState(pos.offset(face,1));
			if(bl.getBlock() == this && bl.get(BlockStateProperties.HORIZONTAL_FACING) == face.getOpposite()) {
				return thisState.with(BlockStateProperties.HORIZONTAL_FACING, face.getOpposite());
			}
		}

		return thisState.with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return PARTIAL_AABB;
	}
}
