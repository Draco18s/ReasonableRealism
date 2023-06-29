package com.draco18s.harderores.entity.capability;

import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class SiftableItemsHandler extends ItemStackHandler {
	public SiftableItemsHandler(int size) {
		super(size);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(HardLibAPI.oreMachines.getSiftResult(stack, false).isEmpty()) return stack;
		return super.insertItem(slot, stack, simulate);
	}
}
