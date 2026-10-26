package com.draco18s.hardlib.api.internal.inventory;

import javax.annotation.Nullable;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.draco18s.hardlib.api.interfaces.ICustomContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class ModContainerBlockEntity extends BaseContainerBlockEntity implements ICustomContainer {
	public static ItemStackHandler EMPTY_HANDLER = new ItemStackHandler(0);
	
	protected ModContainerBlockEntity(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
		super(p_155076_, p_155077_, p_155078_);
	}

	protected abstract @NonNull IItemHandlerModifiable getInventory(@Nullable Direction direction);
	
	@Override
	public void openGUI(ServerPlayer player) {
		if (!level.isClientSide) {
			player.openMenu(this, getBlockPos());
		}
	}
	
	/** The item handler capability on a side (null for all of it), or null for none; see HardLib's capability registration. */
	@Nullable
	public abstract IItemHandler getItemHandler(@Nullable Direction side);

	public double getLevelX() {
		return (double)worldPosition.getX() + 0.5D;
	}

	public double getLevelY() {
		return (double)worldPosition.getY() + 0.5D;
	}

	public double getLevelZ() {
		return (double)worldPosition.getZ() + 0.5D;
	}

	/** The inventory lives in the item handlers; these satisfy BaseContainerBlockEntity with a copy of it. */
	@Override
	protected NonNullList<ItemStack> getItems() {
		IItemHandler inven = getInventory(null);
		NonNullList<ItemStack> items = NonNullList.withSize(inven.getSlots(), ItemStack.EMPTY);
		for(int i = 0; i < inven.getSlots(); i++) {
			items.set(i, inven.getStackInSlot(i));
		}
		return items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		IItemHandlerModifiable inven = getInventory(null);
		for(int i = 0; i < inven.getSlots() && i < items.size(); i++) {
			inven.setStackInSlot(i, items.get(i));
		}
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
		return getInventory(null).extractItem(slot, Integer.MAX_VALUE, false);
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
			inven.extractItem(i, Integer.MAX_VALUE, false);
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
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveAdditional(nbt, registries);
		modSave(nbt, registries);
	}

	@Override
	protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadAdditional(nbt, registries);
		modLoad(nbt, registries);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag nbtTag = new CompoundTag();
		modSave(nbtTag, registries);
		return nbtTag;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
	    return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries){
	    modLoad(pkt.getTag(), registries);
	}
	
	protected abstract void modSave(CompoundTag nbt, HolderLookup.Provider registries);

	protected abstract void modLoad(CompoundTag nbt, HolderLookup.Provider registries);
}
