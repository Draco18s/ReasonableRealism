package com.draco18s.industry.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotValidInsert extends SlotItemHandler {
	public SlotValidInsert(IItemHandler inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return this.getItemHandler().isItemValid(slotNumber, stack);
		//return true;
	}
}