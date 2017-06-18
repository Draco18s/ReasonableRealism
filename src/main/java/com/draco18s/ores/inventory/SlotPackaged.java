package com.draco18s.ores.inventory;

import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotPackaged extends SlotItemHandler {

	public SlotPackaged(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return super.isItemValid(stack) && !HardLibAPI.oreMachines.getPressurePackResult(stack, false).isEmpty();
	}
}
