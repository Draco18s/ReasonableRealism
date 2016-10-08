package com.draco18s.ores.entities;

import com.draco18s.ores.OresBase;
import com.draco18s.ores.block.BlockSluice;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntitySluice extends TileEntity implements ITickable {
	private int waterAmount;

	@Override
	public void update() {
		int prevWater = waterAmount;
		IBlockState state = this.worldObj.getBlockState(pos);
		EnumFacing dir = state.getValue(BlockSluice.FACING).getOpposite();
		
		IBlockState source = this.worldObj.getBlockState(pos.add(dir.getFrontOffsetX(),0,dir.getFrontOffsetZ()));
		
		if(source.getBlock() == Blocks.WATER || source.getBlock() == Blocks.FLOWING_WATER) {
			waterAmount = 8 - source.getValue(BlockLiquid.LEVEL);
		}
		else if(source.getBlock() == OresBase.sluice) {
			waterAmount = ((TileEntitySluice)worldObj.getTileEntity(pos.add(dir.getFrontOffsetX(),0,dir.getFrontOffsetZ()))).getWaterAmount() - 2;
		}
		if(prevWater != waterAmount) {
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	public int getWaterAmount() {
		return waterAmount>0?waterAmount:0;
	}
	
	public int getTime() {
		return 0;
	}
}
