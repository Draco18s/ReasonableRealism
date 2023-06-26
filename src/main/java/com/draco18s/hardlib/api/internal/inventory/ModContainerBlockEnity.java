package com.draco18s.hardlib.api.internal.inventory;

import javax.annotation.Nullable;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.draco18s.hardlib.api.interfaces.ICustomContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

public abstract class ModContainerBlockEnity extends BaseContainerBlockEntity implements ICustomContainer {
	public static ItemStackHandler EMPTY_HANDLER = new ItemStackHandler(0);
	
	protected ModContainerBlockEnity(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
		super(p_155076_, p_155077_, p_155078_);
	}

	protected abstract @NonNull IItemHandlerModifiable getInventory(@Nullable Direction direction);
	
	@Override
	public void openGUI(ServerPlayer player) {
		if (!level.isClientSide) {
			NetworkHooks.openScreen(player, this, getBlockPos());
		}
	}
	
	public final <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
		if(cap == ForgeCapabilities.ITEM_HANDLER) {
			return getInventoryCapability(cap, side);
		}
		return super.getCapability(cap, side);
	}
	
	@NonNull
	protected abstract <T> LazyOptional<T> getInventoryCapability(Capability<T> cap, @Nullable net.minecraft.core.Direction side);

	public double getLevelX() {
		return (double)worldPosition.getX() + 0.5D;
	}

	public double getLevelY() {
		return (double)worldPosition.getY() + 0.5D;
	}

	public double getLevelZ() {
		return (double)worldPosition.getZ() + 0.5D;
	}

	@Override
	public int getContainerSize() {
		return getInventory(null).getSlots();
	}

	@Override
	public boolean isEmpty() {
		return isInventoryEmpty(getInventory(null));
	}

	@Override
	public ItemStack getItem(int slot) {
		return getInventory(null).getStackInSlot(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amt) {
		return getInventory(null).extractItem(slot, amt, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return getInventory(null).extractItem(slot, LARGE_MAX_STACK_SIZE, false);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		getInventory(null).setStackInSlot(slot, stack);
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public void clearContent() {
		IItemHandler inven = getInventory(null);
		for(int i = 0; i < inven.getSlots() ; i++) {
			inven.extractItem(i, LARGE_MAX_STACK_SIZE, false);
		}
	}

	public static boolean isInventoryEmpty(IItemHandler inven) {
		for(int i = 0; i < inven.getSlots() ; i++) {
			if(!inven.getStackInSlot(i).isEmpty())
				return false;
		}
		return true;
	}

	public static boolean isInventoryFull(IItemHandler inven) {
		for(int i = 0; i < inven.getSlots() ; i++) {
			ItemStack stack = inven.getStackInSlot(i);
			if(stack.getCount() < Math.min(stack.getMaxStackSize(), inven.getSlotLimit(i)))
				return false;
		}
		return true;
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		modSave(nbt);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		modLoad(nbt);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
	    CompoundTag nbtTag = new CompoundTag();
	    modSave(nbtTag);
	    ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this, (e) -> nbtTag);
	    return packet;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
	    modLoad(pkt.getTag());
	}
	
	protected abstract void modSave(CompoundTag nbt);

	protected abstract void modLoad(CompoundTag nbt);
}
