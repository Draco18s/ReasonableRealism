package com.draco18s.farming.inventory;

import com.draco18s.farming.HarderFarming;

import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/** One side of a tanning rack: a single rawhide. */
public class LeatherSlotHandler extends ItemStacksResourceHandler {
	public LeatherSlotHandler() {
		super(1);
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		return resource.is(HarderFarming.ModItems.raw_leather);
	}

	@Override
	protected int getCapacity(int index, ItemResource resource) {
		return 1;
	}
}
