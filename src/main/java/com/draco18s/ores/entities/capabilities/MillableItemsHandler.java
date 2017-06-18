package com.draco18s.ores.entities.capabilities;

import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class MillableItemsHandler extends ItemStackHandler {
	public MillableItemsHandler(int size) {
		super(size);
	}
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(HardLibAPI.oreMachines.getMillResult(stack).isEmpty()) return stack;
		return super.insertItem(slot, stack, simulate);
	}
}
