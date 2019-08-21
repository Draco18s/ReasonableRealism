package com.draco18s.harderores.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class SluiceBlock extends Block {
	protected static final VoxelShape PARTIAL_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

	public SluiceBlock() {
		super(Properties.create(Material.WOOD, MaterialColor.WATER).hardnessAndResistance(2).harvestTool(ToolType.AXE).harvestLevel(1).sound(SoundType.WOOD));
		this.setDefaultState(stateContainer.getBaseState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	@Deprecated
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
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
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return PARTIAL_AABB;
	}
}
