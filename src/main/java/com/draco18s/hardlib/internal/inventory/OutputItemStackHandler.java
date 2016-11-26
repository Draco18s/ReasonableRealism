package com.draco18s.hardlib.internal.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class OutputItemStackHandler extends ItemStackHandler {
	private final ItemStackHandler internalSlot;

	public OutputItemStackHandler(ItemStackHandler hidden) {
		super();
		internalSlot = hidden;
	}

	@Override
	public void setSize(int size) {
		stacks = new ItemStack[size];
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
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return internalSlot.extractItem(slot, amount, simulate);
	}
}
