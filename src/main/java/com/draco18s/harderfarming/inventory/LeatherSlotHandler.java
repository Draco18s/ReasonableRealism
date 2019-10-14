package com.draco18s.harderfarming.inventory;

import com.draco18s.harderfarming.HarderFarming;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class LeatherSlotHandler extends ItemStackHandler {
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(stack.getItem() != HarderFarming.ModItems.raw_leather) return stack;
		return super.insertItem(slot, stack, simulate);
	}
	
	@Override
	protected int getStackLimit(int slot, ItemStack stack) {
		return 1;
	}
}
