package com.draco18s.ores.entities;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.capability.CapabilityMechanicalPower;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.entities.capabilities.MillableItemsHandler;
import com.draco18s.ores.entities.capabilities.SiftableItemsHandler;
import com.draco18s.ores.inventory.ContainerSifter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class TileEntitySifter extends TileEntity implements ITickable {
	protected ItemStackHandler inputSlot;
	protected ItemStackHandler outputSlot;
	private ItemStackHandler outputSlotWrapper;
	private float siftTime;
	private int activeSlot = -1;
	
	public TileEntitySifter() {
		inputSlot = new SiftableItemsHandler();
		outputSlot = new ItemStackHandler();
		outputSlotWrapper = new OutputItemStackHandler(outputSlot);
	}
	
	@Override
	public void update() {
		if(worldObj.getBlockState(pos).getBlock() != this.getBlockType()) return;
		suckItems();
		if(siftTime > 0) {
			--siftTime;
			boolean canAnySift = false;
			int resetTime = -1;
			for(int s = 0; s < inputSlot.getSlots(); s++) {
				if(canSift(s)) {
					canAnySift = true;
					resetTime = s;
				}
			}
			if(resetTime != activeSlot) {
				activeSlot = resetTime;
				siftTime = 100;
			}
			if(!canAnySift) {
				siftTime = 0;
			}
			else if (siftTime <= 0) {
				siftItem();
			}
		}
		else {
			activeSlot = -1;
			for(int s = 0; s < inputSlot.getSlots(); s++) {
				if(canSift(s)) {
					siftTime = 40;
					activeSlot = s;
				}
			}
		}
	}

	private boolean canSift(int slot) {
		if(inputSlot.getStackInSlot(slot) == null) return false;
		ItemStack result = HardLibAPI.oreMachines.getSiftResult(inputSlot.getStackInSlot(slot), true);
		if(result == null) return false;
		if(outputSlot.insertItem(0, result, true) != null) return false;
		return true;
	}

	private void siftItem() {
		for(int s = 0; s < inputSlot.getSlots(); s++) {
			ItemStack stack = inputSlot.getStackInSlot(s);
			if(stack == null) continue;
			ItemStack result = HardLibAPI.oreMachines.getSiftResult(stack, true);
			if(result == null) continue;
			if(outputSlot.insertItem(0, result, true) != null) continue;
			inputSlot.extractItem(s, HardLibAPI.oreMachines.getSiftAmount(stack), false);
			outputSlot.insertItem(0, result.copy(), false);
		}
	}
	
	private void suckItems() {
		List<EntityItem> ents = worldObj.getEntitiesWithinAABB(EntityItem.class, getAABB(pos));
		if(ents.size() > 0) {
			ItemStack stack;
			EntityItem ent;
			for(int e = ents.size()-1; e >= 0; e--) {
				ent = (EntityItem) ents.get(e);
				stack = ent.getEntityItem().copy();
				if(HardLibAPI.oreMachines.getSiftResult(stack, false) != null) {
					stack = inputSlot.insertItem(0, stack, false);
					if(stack != null)
						stack = inputSlot.insertItem(1, stack, false);
					if(stack != null)
						ent.setEntityItemStack(stack);
					else
						ent.setDead();
					this.markDirty();
				}
			}
		}
	}
	
	private AxisAlignedBB getAABB(BlockPos p) {
		return new AxisAlignedBB(p.getX(), p.getY(), p.getZ(), p.getX()+1, p.getY()+1.25, p.getZ()+1);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			this.markDirty();
			if(worldObj != null && worldObj.getBlockState(pos).getBlock() != getBlockType()) {//if the block at myself isn't myself, allow full access (Block Broken)
				return (T) new CombinedInvWrapper(inputSlot, outputSlotWrapper);
			}
			if(facing == null) {
				return (T) new CombinedInvWrapper(inputSlot, outputSlotWrapper);
			}
			if(facing == EnumFacing.UP) {
				return (T) inputSlot;
			}
			if(facing == EnumFacing.DOWN) {
				return (T) outputSlotWrapper;
			}
		}
		return super.getCapability(capability, facing);
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
		compound.setTag("harderores:inputSlot", inputSlot.serializeNBT());
		compound.setTag("harderores:outputSlot", outputSlot.serializeNBT());
		compound.setFloat("harderores:siftTime", siftTime);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(inputSlot == null) {
			inputSlot = new SiftableItemsHandler();
			outputSlot = new ItemStackHandler();
			outputSlotWrapper = new OutputItemStackHandler(outputSlot);
		}
		if(compound.hasKey("harderores:inputSlot")) {
			inputSlot.deserializeNBT((NBTTagCompound) compound.getTag("harderores:inputSlot"));
			outputSlot.deserializeNBT((NBTTagCompound) compound.getTag("harderores:outputSlot"));
		}
		siftTime = compound.getFloat("harderores:siftTime");
	}

	public float getTime() {
		return siftTime;
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
}
