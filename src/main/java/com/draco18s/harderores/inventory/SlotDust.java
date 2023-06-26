package com.draco18s.harderores.inventory;

import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotDust extends SlotItemHandler {

	public SlotDust(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack) {
		return super.mayPlace(stack) && !HardLibAPI.oreMachines.getSiftResult(stack, false).isEmpty();
	}
}