package com.draco18s.hardlib.api.internal.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

/**
 * ItemStackHandler wrapper that allows for all the usual inventory item manipulation except
 * that when exposed externally (GUI, hopper, etc.), items may not be inserted, only extracted.
 * @author Draco18s
 *
 */
public class OutputItemStackHandler extends ItemStackHandler {
	private final ItemStackHandler internalSlot;

	public OutputItemStackHandler(ItemStackHandler hidden) {
		super();
		internalSlot = hidden;
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
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return internalSlot.extractItem(slot, amount, simulate);
	}
}