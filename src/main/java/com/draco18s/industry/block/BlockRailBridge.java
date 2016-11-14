package com.draco18s.industry.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailBridge extends BlockRail {
	protected static final AxisAlignedBB HALF_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	protected static final AxisAlignedBB THICK_FLAT_AABB = new AxisAlignedBB(0.0D, -0.125D, 0.0D, 1.0D, 0.125D, 1.0D);

	public BlockRailBridge() {
		super();
	}
	
	@Override
	public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
        return false;
    }
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
        if(world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) ||
        		this.isRailBlock(world, pos.down()) || this.isRailBlock(world, pos.up())) {
        	return false;
        }
        return true;
    }

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
		if (!world.isRemote) {
			if(!canPlaceBlockAt(world, pos)) {
    			this.dropBlockAsItem(world, pos, state, 0);
                world.setBlockToAir(pos);
    		}
		}
	}
	
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
        return false;
    }
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = state.getBlock() == this ? (BlockRailBase.EnumRailDirection)state.getValue(this.getShapeProperty()) : null;
        return blockrailbase$enumraildirection != null && blockrailbase$enumraildirection.isAscending() ? HALF_BLOCK_AABB : THICK_FLAT_AABB;
    }

	/*@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity) {
		
		if(entity == null || !entity.isSneaking()) {
			//collidingBoxes.add(null);
		}
		else {
			System.out.println(entity);
			collidingBoxes.add(HALF_BLOCK_AABB);
		}
    }*/
	
	@Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
		EntityPlayer p = world.getClosestPlayer(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, 1.5, false);
		if(p == null) {
			p = world.getClosestPlayer(pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 1.5, false);
		}
		if(p == null) {
			p = world.getClosestPlayer(pos.getX()+0.5, pos.getY()+2, pos.getZ()+0.5, 1.5, false);
		}
		if(p != null && p.isSneaking()) {
			return THICK_FLAT_AABB;
		}
        return NULL_AABB;
    }
}
