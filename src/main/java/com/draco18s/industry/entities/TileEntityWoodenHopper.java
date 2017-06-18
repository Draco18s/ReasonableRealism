package com.draco18s.industry.entities;

import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityWoodenHopper extends TileEntityHopper {

	public TileEntityWoodenHopper() {
		super();
		setCustomName("container.expindustry:wood_hopper");
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 16;
	}

	@Override
	public void update() {
		if(world.getBlockState(pos).getValue(BlockHopper.FACING) != EnumFacing.DOWN) {
			world.setBlockState(this.getPos(), this.getBlockType().getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.DOWN), 3);
		}
		super.update();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
}
