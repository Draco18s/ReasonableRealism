package com.draco18s.farming.entities;

import java.util.ArrayList;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.FarmingBase;
import com.draco18s.farming.entities.capabilities.ItemLeatherHandler;
import com.draco18s.farming.entities.capabilities.ItemSaltHandler;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class TileEntityTanner extends TileEntity implements ITickable {

	protected ItemLeatherHandler leftSlot;
	protected ItemLeatherHandler rightSlot;
	protected ItemSaltHandler saltSlot;
	protected int[] tanningTime;
	protected int saltTime;
	private boolean shouldUpdate;

	public TileEntityTanner() {
		leftSlot = new ItemLeatherHandler(1);
		rightSlot = new ItemLeatherHandler(1);
		saltSlot = new ItemSaltHandler(1);
		tanningTime = new int[]{0,0};
	}

	@Override
	public void update() {
		if(!leftSlot.getStackInSlot(0).isEmpty()) {
			if(leftSlot.getStackInSlot(0).getItem() == FarmingBase.rawLeather) {
				if(cureLeather(0)) {
					leftSlot.setStackInSlot(0, new ItemStack(Items.LEATHER));
					setBlockToUpdate();
					tanningTime[0] = 0;
				}
			}
		}
		else {
			tanningTime[0] = 0;
		}
		if(!rightSlot.getStackInSlot(0).isEmpty()) {
			if(rightSlot.getStackInSlot(0).getItem() == FarmingBase.rawLeather) {
				if(cureLeather(1)) {
					rightSlot.setStackInSlot(0, new ItemStack(Items.LEATHER));
					setBlockToUpdate();
					tanningTime[1] = 0;
				}
			}
		}
		else {
			tanningTime[1] = 0;
		}
		if(saltTime > 0) {
			saltTime -= (canRainHere()?2:1);
			if(saltTime <= 0)
				setBlockToUpdate();
		}
		//if(!world.isRemote)
			//FarmingBase.logger.log(Level.INFO, tanningTime[0] + "," + tanningTime[1] + "|" + saltTime);
		int v = Math.max(Math.max(tanningTime[0], tanningTime[1]),saltTime);
		if(v > 0 && v % 100 <= 1) {
			setBlockToUpdate();
		}
		if(shouldUpdate) {
			sendUpdates();
			shouldUpdate = false;
		}
	}

	private void setBlockToUpdate() {
		sendUpdates();
		//shouldUpdate = true;
	}
	
	private void sendUpdates() {
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyBlockUpdate(pos, getState(), getState(), 3);
		world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
		markDirty();
	}

	private boolean canRainHere() {
		return world.isRaining() && world.getPrecipitationHeight(pos).getY() <= pos.getY();
	}

	private IBlockState getState() {
		return world.getBlockState(pos);
	}

	private boolean cureLeather(int i) {
		if(tanningTime[i] < 3) {
			setBlockToUpdate();
		}
		tanningTime[i] += (getSaltTimer()?2:1);
		return tanningTime[i] >= 2400;
	}

	private boolean getSaltTimer() {
		if(saltTime>0) {
			return true;
		}
		else if(!saltSlot.getStackInSlot(0).isEmpty()) {
			saltSlot.extractItem(0, 1, false);
			saltTime = 4800 - 1;//time enough to cure 8 leather (optimal)
			setBlockToUpdate();
			return true;
		}
		return false;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			setBlockToUpdate();
			if(world != null && world.getBlockState(pos).getBlock() != getBlockType()) {//if the block at myself isn't myself, allow full access (Block Broken)
				return (T) new CombinedInvWrapper(leftSlot, rightSlot, saltSlot);
			}
			if(facing == null) {
				return (T) new CombinedInvWrapper(leftSlot, rightSlot, saltSlot);
			}
			if(facing == EnumFacing.UP) {
				return (T) saltSlot;
			}
			if(facing == EnumFacing.DOWN) {
				return (T) getOuputSlots();
			}
			IBlockState bs = world.getBlockState(pos);
			EnumFacing bface = bs.getValue(BlockHorizontal.FACING);
			if(bface == facing) {
				return (T) leftSlot;
			}
			if(bface.getOpposite() == facing) {
				return (T) rightSlot;
			}
		}
		return super.getCapability(capability, facing);
	}

	private CombinedInvWrapper getOuputSlots() {
		ArrayList<ItemStackHandler> allSlots = new ArrayList();
		if(!leftSlot.getStackInSlot(0).isEmpty() && leftSlot.getStackInSlot(0).getItem() == Items.LEATHER) {
			allSlots.add(leftSlot);
		}
		if(!rightSlot.getStackInSlot(0).isEmpty() && rightSlot.getStackInSlot(0).getItem() == Items.LEATHER) {
			allSlots.add(rightSlot);
		}
		return new CombinedInvWrapper(allSlots.toArray(new ItemStackHandler[0]));
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
		compound.setTag("harderfarming:leftSlot", leftSlot.serializeNBT());
		compound.setTag("harderfarming:rightSlot", rightSlot.serializeNBT());
		compound.setTag("harderfarming:saltSlot", saltSlot.serializeNBT());
		compound.setIntArray("harderfarming:tanningTime", tanningTime);
		compound.setInteger("harderfarming:saltTime", saltTime);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(leftSlot == null) {
			leftSlot = new ItemLeatherHandler(1);
			rightSlot = new ItemLeatherHandler(1);
			saltSlot = new ItemSaltHandler(1);
		}
		if(compound.hasKey("harderfarming:leftSlot")) {
			leftSlot.deserializeNBT((NBTTagCompound) compound.getTag("harderfarming:leftSlot"));
		}
		if(compound.hasKey("harderfarming:rightSlot")) {
			rightSlot.deserializeNBT((NBTTagCompound) compound.getTag("harderfarming:rightSlot"));
		}
		if(compound.hasKey("harderfarming:saltSlot")) {
			saltSlot.deserializeNBT((NBTTagCompound) compound.getTag("harderfarming:saltSlot"));
		}
		tanningTime = compound.getIntArray("harderfarming:tanningTime");
		if(tanningTime.length < 2) {
			tanningTime = new int[]{0,0};
		}
		saltTime = compound.getInteger("harderfarming:saltTime");
	}

	public float getTime() {
		return 0;
		//return tanningTime;
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public int getSalt() {
		if(saltSlot.getStackInSlot(0).isEmpty()) {
			return (saltTime > 0)?1:0;
		}
		int size = saltSlot.getStackInSlot(0).getCount();
		if(size <= 2) return 2;
		if(size <= 16) return 3;
		if(size <= 32) return 4;
		if(size <= 48) return 5;
		return 6;
	}

	public int getLeather(int slot) {
		//FarmingBase.logger.log(Level.INFO, "Slot " + slot);
		if(slot == 0) {
			//FarmingBase.logger.log(Level.INFO, "	  " + leftSlot.getStackInSlot(0));
			//return leftSlot.getStackInSlot(0) != null;
			if(leftSlot.getStackInSlot(0).isEmpty()) return 0;
			if(leftSlot.getStackInSlot(0).getItem() == FarmingBase.rawLeather) return 1;
			if(leftSlot.getStackInSlot(0).getItem() == Items.LEATHER) return 2;
		}
		if(slot == 1) {
			//FarmingBase.logger.log(Level.INFO, "	  " + rightSlot.getStackInSlot(0));
			if(rightSlot.getStackInSlot(0).isEmpty()) return 0;
			if(rightSlot.getStackInSlot(0).getItem() == FarmingBase.rawLeather) return 1;
			if(rightSlot.getStackInSlot(0).getItem() == Items.LEATHER) return 2;
		}
		return 0;
	}
}
