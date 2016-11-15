package com.draco18s.industry.block;

import javax.annotation.Nullable;

import com.draco18s.industry.ExpandedIndustryBase;
import com.draco18s.industry.IndustryGuiHandler;
import com.draco18s.industry.entities.TileEntityDistributor;
import com.draco18s.industry.entities.TileEntityFilter;

import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFilter extends BlockHopper {

	public BlockFilter() {
		super();
		setHardness(2.0F);
		setResistance(4.0F);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityFilter();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		playerIn.openGui(ExpandedIndustryBase.instance, IndustryGuiHandler.FILTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}
