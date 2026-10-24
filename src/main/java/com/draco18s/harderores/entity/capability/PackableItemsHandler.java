package com.draco18s.harderores.entity.capability;

import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/** The packager's two input slots: only packable items, and matching stacks merge into the first slot. */
public class PackableItemsHandler extends ItemStackHandler {
	public PackableItemsHandler() {
		super(2);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return !HardLibAPI.oreMachines.getPressurePackResult(stack, false).isEmpty();
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		if(slot+1 >= stacks.size()) return;
		ItemStack first = stacks.get(slot);
		ItemStack second = stacks.get(slot+1);
		if(!first.isEmpty() && !second.isEmpty() && ItemStack.isSameItemSameComponents(first, second)) {
			int change = Math.min(second.getCount(), first.getMaxStackSize() - first.getCount());
			if(change <= 0) return;
			first.grow(change);
			second.shrink(change);
			if(second.isEmpty()) {
				stacks.set(slot+1, ItemStack.EMPTY);
			}
			onContentsChanged(slot+1);
		}
	}
}
