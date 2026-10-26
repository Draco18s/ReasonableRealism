package com.draco18s.harderores.inventory;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.inventory.HandlerSlot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class SlotDust extends HandlerSlot {

	public SlotDust(ItemStacksResourceHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack) {
		return super.mayPlace(stack) && !HardLibAPI.oreMachines.getSiftResult(stack, false).isEmpty();
	}
}
