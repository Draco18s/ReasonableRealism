package com.draco18s.industry.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotValidInsert extends Slot {
	public SlotValidInsert(IInventory inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return inventory.isItemValidForSlot(slotNumber, stack);
    }
}
