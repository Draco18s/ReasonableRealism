package com.draco18s.farming.entities.capabilities;

import com.draco18s.farming.FarmingBase;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemLeatherHandler extends ItemStackHandler {
	public ItemLeatherHandler(int i) {
		super(i);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(stack.getItem() != FarmingBase.rawLeather) return stack;
		return super.insertItem(slot, stack, simulate);
	}

	@Override
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

	@Override
    protected int getStackLimit(int slot, ItemStack stack) {
        return 1;
    }
}
