package com.draco18s.harderfarming.entity;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.draco18s.harderfarming.HarderFarming;
import com.draco18s.harderfarming.inventory.LeatherSlotHandler;
import com.draco18s.harderfarming.inventory.SaltSlotHandler;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.farming.LeatherStatus;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class TannerTileEntity extends TileEntity implements ITickableTileEntity {
	protected ItemStackHandler leftSlot;
	protected ItemStackHandler rightSlot;
	protected ItemStackHandler saltSlot;
	private final LazyOptional<IItemHandler> leftSlotholder = LazyOptional.of(() -> leftSlot);
	private final LazyOptional<IItemHandler> rightSlotholder = LazyOptional.of(() -> rightSlot);
	private final LazyOptional<IItemHandler> saltSlotholder = LazyOptional.of(() -> saltSlot);
	private final LazyOptional<IItemHandler> outputSlots = LazyOptional.of(() -> new CombinedInvWrapper(leftSlot, rightSlot));
	private final LazyOptional<IItemHandler> everything = LazyOptional.of(() -> {
		ArrayList<ItemStackHandler> allSlots = new ArrayList<ItemStackHandler>();
		if(!leftSlot.getStackInSlot(0).isEmpty() && leftSlot.getStackInSlot(0).getItem() == Items.LEATHER) {
			allSlots.add(leftSlot);
		}
		if(!rightSlot.getStackInSlot(0).isEmpty() && rightSlot.getStackInSlot(0).getItem() == Items.LEATHER) {
			allSlots.add(rightSlot);
		}
		return new CombinedInvWrapper(allSlots.toArray(new ItemStackHandler[0]));
	});
	protected int[] tanningTime;
	protected int saltTime;

	public TannerTileEntity() {
		super(HarderFarming.ModTileEntities.tanner);
		tanningTime = new int[]{0,0};
		leftSlot = new LeatherSlotHandler();
		rightSlot = new LeatherSlotHandler();
		saltSlot = new SaltSlotHandler();
	}

	@Override
	public void tick() {
		if(!leftSlot.getStackInSlot(0).isEmpty()) {
			if(leftSlot.getStackInSlot(0).getItem() == HarderFarming.ModItems.raw_leather) {
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
			if(rightSlot.getStackInSlot(0).getItem() == HarderFarming.ModItems.raw_leather) {
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
	}

	public void setBlockToUpdate() {
		BlockState state = getBlockState();
		state = state.with(BlockProperties.SALT_LEVEL, this.getSalt());
		state = state.with(BlockProperties.LEFT_LEATHER_STATE, getLeather(0));
		state = state.with(BlockProperties.RIGHT_LEATHER_STATE, getLeather(1));
		getWorld().setBlockState(pos, state);
	}

	private LeatherStatus getLeather(int slot) {
		if(slot == 0) {
			if(leftSlot.getStackInSlot(0).isEmpty()) return LeatherStatus.NONE;
			if(leftSlot.getStackInSlot(0).getItem() == HarderFarming.ModItems.raw_leather) return LeatherStatus.RAW;
			if(leftSlot.getStackInSlot(0).getItem() == Items.LEATHER) return LeatherStatus.CURED;
		}
		if(slot == 1) {
			if(rightSlot.getStackInSlot(0).isEmpty()) return LeatherStatus.NONE;
			if(rightSlot.getStackInSlot(0).getItem() == HarderFarming.ModItems.raw_leather) return LeatherStatus.RAW;
			if(rightSlot.getStackInSlot(0).getItem() == Items.LEATHER) return LeatherStatus.CURED;
		}
		return LeatherStatus.NONE;
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

	private boolean canRainHere() {
		return world.isRainingAt(getPos());
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(facing == null || world != null && world.getBlockState(pos).getBlock() != getBlockState().getBlock()) {//if the block at myself isn't myself, allow full access (Block Broken)
				return everything.cast();
			}
			if(facing == Direction.UP) {
				return saltSlotholder.cast();
			}
			if(facing == Direction.DOWN) {
				return outputSlots.cast();
			}
			BlockState bs = world.getBlockState(pos);
			Direction bface = bs.get(BlockStateProperties.HORIZONTAL_FACING);
			if(bface == facing) {
				return leftSlotholder.cast();
			}
			if(bface.getOpposite() == facing) {
				return rightSlotholder.cast();
			}
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public void remove() {
		super.remove();
		leftSlotholder.invalidate();
		rightSlotholder.invalidate();
		outputSlots.invalidate();
		saltSlotholder.invalidate();
		everything.invalidate();
	}
	
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
		read(pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		tag = super.write(tag);
		tag.put("harderfarming:leftSlot", leftSlot.serializeNBT());
		tag.put("harderfarming:rightSlot", rightSlot.serializeNBT());
		tag.put("harderfarming:saltSlot", saltSlot.serializeNBT());
		tag.putIntArray("harderfarming:tanningTime", tanningTime);
		tag.putInt("harderfarming:saltTime", saltTime);
		return tag;
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		leftSlot.deserializeNBT((CompoundNBT)tag.get("harderfarming:leftSlot"));
		rightSlot.deserializeNBT((CompoundNBT)tag.get("harderfarming:rightSlot"));
		saltSlot.deserializeNBT((CompoundNBT)tag.get("harderfarming:saltSlot"));
		tanningTime = tag.getIntArray("harderfarming:tanningTime");
		if(tanningTime.length < 2) {
			tanningTime = new int[]{0,0};
		}
		saltTime = tag.getInt("harderfarming:saltTime");
	}
	
	public float getTime(int slot) {
		if(slot >= tanningTime.length) return 0;
		return tanningTime[slot];
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
}
