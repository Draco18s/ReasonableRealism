package com.draco18s.farming.inventory;

import com.draco18s.farming.HarderFarming;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

/** One side of a tanning rack: a single rawhide. */
public class LeatherSlotHandler extends ItemStackHandler {
	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return stack.getItem() == HarderFarming.ModItems.raw_leather;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}
}
