package com.draco18s.hardlib.api.internal.inventory;

import net.minecraftforge.items.ItemStackHandler;

public class MaxSizeItemStackHandler extends ItemStackHandler {
	protected final int maxStackSize;

	public MaxSizeItemStackHandler(int slots, int maxCount) {
		super(slots);
		maxStackSize = maxCount;
	}

    @Override
    public int getSlotLimit(int slot)
    {
        return maxStackSize;
    }
}