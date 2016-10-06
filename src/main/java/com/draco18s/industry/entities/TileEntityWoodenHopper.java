package com.draco18s.industry.entities;

import net.minecraft.block.BlockHopper;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;

public class TileEntityWoodenHopper extends TileEntityHopper {

	public TileEntityWoodenHopper() {
		super();
		setCustomName("Wooden Hopper");
	}
	
	@Override
	public int getInventoryStackLimit() {
        return 16;
    }

	@Override
	public void update() {
		if(worldObj.getBlockState(pos).getValue(BlockHopper.FACING) != EnumFacing.DOWN) {
			worldObj.setBlockState(this.getPos(), this.getBlockType().getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.DOWN), 3);
		}
		super.update();
	}
}
