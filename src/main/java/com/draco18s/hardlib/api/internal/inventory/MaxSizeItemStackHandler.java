package com.draco18s.hardlib.api.internal.inventory;

import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class MaxSizeItemStackHandler extends ItemStacksResourceHandler {
	protected final int maxStackSize;

	public MaxSizeItemStackHandler(int slots, int maxCount) {
		super(slots);
		maxStackSize = maxCount;
	}

    @Override
    protected int getCapacity(int index, ItemResource resource)
    {
        return Math.min(maxStackSize, super.getCapacity(index, resource));
    }
}
