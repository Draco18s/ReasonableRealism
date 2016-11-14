package com.draco18s.industry.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPoweredRailBridge extends BlockRailPowered {
	protected static final AxisAlignedBB THICK_FLAT_AABB = new AxisAlignedBB(0.0D, -0.125D, 0.0D, 1.0D, 0.125D, 1.0D);
	
	public BlockPoweredRailBridge() {
		super();
	}
	
	@Override
	public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
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
		this.updateState(state, world, pos, block);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return THICK_FLAT_AABB;
	}
	
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
	
	public void onMinecartPass(World world, net.minecraft.entity.item.EntityMinecart cart, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		BlockRailBase.EnumRailDirection railDir = this.getRailDirection(world, pos, state, cart);
		boolean isPowered = state.getValue(POWERED);
		double d15 = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);

		if(isPowered) {
			if (d15 > 0.01D)
			{
				double d16 = 0.06D;
				cart.motionX += cart.motionX / d15 * 0.06D;
				cart.motionZ += cart.motionZ / d15 * 0.06D;
			}
			else if (railDir == BlockRailBase.EnumRailDirection.EAST_WEST)
			{
				if (cart.worldObj.getBlockState(pos.west()).isNormalCube())
				{
					cart.motionX = 0.02D;
				}
				else if (world.getBlockState(pos.east()).isNormalCube())
				{
					cart.motionX = -0.02D;
				}
			}
			else if (railDir == BlockRailBase.EnumRailDirection.NORTH_SOUTH)
			{
				if (world.getBlockState(pos.north()).isNormalCube())
				{
					cart.motionZ = 0.02D;
				}
				else if (world.getBlockState(pos.south()).isNormalCube())
				{
					cart.motionZ = -0.02D;
				}
			}
		}
		else {
			cart.motionX = 0;
			cart.motionY = 0;
			cart.motionZ = 0;
		}
	}
}
