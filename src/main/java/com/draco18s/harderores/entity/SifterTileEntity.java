package com.draco18s.harderores.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.capability.SiftableItemsHandler;
import com.draco18s.harderores.inventory.SifterContainer;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.interfaces.ICustomContainer;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class SifterTileEntity extends TileEntity implements ITickableTileEntity, ICustomContainer {
	protected ItemStackHandler inputSlot;
	protected ItemStackHandler outputSlot;
	private ItemStackHandler outputSlotWrapper;
	private final LazyOptional<ItemStackHandler> inputSlotholder = LazyOptional.of(() -> inputSlot);
	private final LazyOptional<ItemStackHandler> outputSlotWrapperholder = LazyOptional.of(() -> outputSlotWrapper);
	private float siftTime;
	private int activeSlot = -1;

	public SifterTileEntity() {
		super(HarderOres.ModTileEntities.sifter);
		inputSlot = new SiftableItemsHandler();
		outputSlot = new ItemStackHandler();
		outputSlotWrapper = new OutputItemStackHandler(outputSlot);
	}

	@Override
	public void tick() {
		if(world.getBlockState(pos).getBlock() != this.getBlockState().getBlock()) return;
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
			if(resetTime != activeSlot && !this.getWorld().isRemote) {
				activeSlot = resetTime;
				siftTime = 100;
				world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
				markDirty();
			}
			if(!canAnySift) {
				siftTime = 0;
				world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
				markDirty();
			}
			else if (siftTime <= 0) {
				siftItem();
				world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
				markDirty();
			}
		}
		else {
			activeSlot = -1;
			for(int s = 0; s < inputSlot.getSlots(); s++) {
				if(canSift(s)) {
					siftTime = 40;
					activeSlot = s;
					world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
					markDirty();
				}
			}
		}
	}

	private boolean canSift(int slot) {
		if(inputSlot.getStackInSlot(slot).isEmpty()) return false;
		ItemStack result = HardLibAPI.oreMachines.getSiftResult(inputSlot.getStackInSlot(slot), true);
		if(result.isEmpty()) return false;
		if(!outputSlot.insertItem(0, result, true).isEmpty()) return false;
		return true;
	}

	private void siftItem() {
		for(int s = 0; s < inputSlot.getSlots(); s++) {
			ItemStack stack = inputSlot.getStackInSlot(s);
			if(stack.isEmpty()) continue;
			ItemStack result = HardLibAPI.oreMachines.getSiftResult(stack, true);
			if(result.isEmpty()) continue;
			if(!outputSlot.insertItem(0, result, true).isEmpty()) continue;
			inputSlot.extractItem(s, HardLibAPI.oreMachines.getSiftAmount(stack), false);
			outputSlot.insertItem(0, result.copy(), false);
		}
	}

	private void suckItems() {
		List<ItemEntity> ents = world.getEntitiesWithinAABB(ItemEntity.class, getAABB(pos));
		if(ents.size() > 0) {
			ItemStack stack;
			ItemEntity ent;
			for(int e = ents.size()-1; e >= 0; e--) {
				ent = (ItemEntity) ents.get(e);
				stack = ent.getItem().copy();
				if(HardLibAPI.oreMachines.getSiftResult(stack, false) != null) {
					stack = inputSlot.insertItem(0, stack, false);
					if(!stack.isEmpty())
						stack = inputSlot.insertItem(1, stack, false);
					if(!stack.isEmpty())
						ent.setItem(stack);
					else
						ent.remove();
					this.markDirty();
				}
			}
		}
	}

	private AxisAlignedBB getAABB(BlockPos p) {
		return new AxisAlignedBB(p.getX(), p.getY(), p.getZ(), p.getX()+1, p.getY()+1.25, p.getZ()+1);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			this.markDirty();
			if(world != null && world.getBlockState(pos).getBlock() != this.getBlockState().getBlock()) {//if the block at myself isn't myself, allow full access (Block Broken)
				return LazyOptional.of(() -> new CombinedInvWrapper(inputSlot, outputSlotWrapper)).cast();
			}
			if(facing == null) {
				return LazyOptional.of(() -> new CombinedInvWrapper(inputSlot, outputSlot)).cast();
			}
			if(world == null) {
				if(facing == Direction.UP) {
					return inputSlotholder.cast();
				}
				if(facing == Direction.DOWN) {
					return outputSlotWrapperholder.cast();
				}
				return super.getCapability(capability, facing);
			}
			if(facing == Direction.UP) {
				return inputSlotholder.cast();
			}
			if(facing == Direction.DOWN) {
				return outputSlotWrapperholder.cast();
			}
		}
		return super.getCapability(capability, facing);
	}

	public float getTime() {
		return siftTime;
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
		tag.put("harderores:inputslot", inputSlot.serializeNBT());
		tag.put("harderores:outputSlot", outputSlot.serializeNBT());
		tag.putFloat("harderores:siftTime", siftTime);
		return tag;
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		inputSlot.deserializeNBT(tag.getCompound("harderores:inputslot"));
		outputSlot.deserializeNBT(tag.getCompound("harderores:outputSlot"));
		siftTime = tag.getFloat("harderores:siftTime");
	}

	@Override
	public void openGUI(ServerPlayerEntity player) {
		if (!world.isRemote) {
			NetworkHooks.openGui(player, this, getPos());
		}
	}

	@Override
	public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player) {
		return new SifterContainer(windowID, playerInventory, new CombinedInvWrapper(inputSlot, outputSlotWrapper), this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("harderores:sifter.name");
	}
}
