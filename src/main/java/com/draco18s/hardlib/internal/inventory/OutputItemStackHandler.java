package com.draco18s.hardlib.internal.inventory;

import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class OutputItemStackHandler extends ItemStackHandler {
	public OutputItemStackHandler() {
		super();
	}
	public OutputItemStackHandler(int size) {
		super(size);
	}
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}
}
