package com.draco18s.ores.item;

import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class SiftableItemsHandler extends ItemStackHandler {
	public SiftableItemsHandler() {
		super(2);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(HardLibAPI.oreMachines.getSiftResult(stack,false) == null) return stack;
		return super.insertItem(slot, stack, simulate);
	}

	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		if(slot+1 >= stacks.length) return;
		if(this.stacks[slot] != null && this.stacks[slot+1] != null) {
			if(ItemStack.areItemsEqual(stacks[slot], stacks[slot+1])) {
				int totalSize = stacks[slot].stackSize + stacks[slot+1].stackSize;
				int change = stacks[slot+1].stackSize;
				if(totalSize > stacks[slot].getMaxStackSize()) {
					change = stacks[slot].getMaxStackSize() - stacks[slot].stackSize;
				}
				stacks[slot].stackSize += change;
				stacks[slot+1].stackSize -= change;
				if(stacks[slot+1].stackSize == 0) {
					stacks[slot+1] = null;
				}
				onContentsChanged(slot+1);
			}
		}
	}
}
