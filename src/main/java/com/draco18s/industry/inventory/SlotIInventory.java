package com.draco18s.industry.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotIInventory extends Slot {
	public SlotIInventory(IInventory inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if(inventory.isItemValidForSlot(slotNumber, stack)) {
			ItemStack ss = stack.copy();
			ss.stackSize = 1;
			inventory.setInventorySlotContents(slotNumber, ss);
		}
		//return inventory.isItemValidForSlot(slotNumber, stack);
		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		inventory.setInventorySlotContents(slotNumber, null);
		return false;
	}
}
