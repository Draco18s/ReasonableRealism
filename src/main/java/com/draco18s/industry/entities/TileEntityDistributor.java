package com.draco18s.industry.entities;

import javax.annotation.Nullable;

import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityDistributor extends TileEntityHopper {
	private int delay = 4;

	public TileEntityDistributor() {
		super();
		setCustomName("container.expindustry:distributor");
	}

	@Override
	public void update() {
		IBlockState state = world.getBlockState(pos);
		EnumFacing face = state.getValue(BlockHopper.FACING);
		if(face == EnumFacing.DOWN || face == EnumFacing.UP) {
			world.setBlockState(this.getPos(), state.withProperty(BlockHopper.FACING, EnumFacing.NORTH), 3);
		}
		else {
			delay--;
			if(delay <= 0) {
				face = face.rotateY();
				world.setBlockState(this.getPos(), state.withProperty(BlockHopper.FACING, face), 2);
				delay = 4;
				setTransferCooldown(0);
				updateHopper();
				world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
				this.markDirty();
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("expindustry:delay", delay);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		delay = compound.getInteger("expindustry:delay");
	}
}
