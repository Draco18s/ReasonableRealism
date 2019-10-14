package com.draco18s.harderfarming.inventory;

import com.draco18s.harderfarming.HarderFarming;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class SaltSlotHandler extends ItemStackHandler {
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(!HarderFarming.ModItemTags.SALT.contains(stack.getItem())) return stack;
		return super.insertItem(slot, stack, simulate);
	}
}
