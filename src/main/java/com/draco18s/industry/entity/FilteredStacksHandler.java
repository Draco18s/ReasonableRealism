package com.draco18s.industry.entity;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class FilteredStacksHandler extends ItemStackHandler {
	private final FilterTileEntity filter;
	private final ItemStackHandler internalSlot;

	public FilteredStacksHandler(ItemStackHandler inventory, FilterTileEntity filterTileEntity) {
		filter = filterTileEntity;
		internalSlot = inventory;
	}
	
	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return internalSlot.insertItem(slot, stack, simulate);
	}
	
	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return stack.isEmpty() || filter.canInsert(stack);
	}
	
	@Override
	public void setSize(int size) {
		stacks = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		internalSlot.setStackInSlot(slot, stack);
	}

	@Override
	public int getSlots() {
		return internalSlot.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return internalSlot.getStackInSlot(slot);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return internalSlot.extractItem(slot, amount, simulate);
	}
}
