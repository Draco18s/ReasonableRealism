package com.draco18s.hardlib.api.internal.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class SlotOutput extends ResourceHandlerSlot {
	
	public SlotOutput(OutputItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}
}
