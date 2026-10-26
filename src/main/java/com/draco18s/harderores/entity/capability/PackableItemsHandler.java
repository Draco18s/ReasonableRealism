package com.draco18s.harderores.entity.capability;

import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/** The packager's two input slots: only packable items, and matching stacks merge into the first slot. */
public class PackableItemsHandler extends ItemStacksResourceHandler {
	public PackableItemsHandler() {
		super(2);
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		return !HardLibAPI.oreMachines.getPressurePackResult(resource.toStack(), false).isEmpty();
	}

	@Override
	protected void onContentsChanged(int index, ItemStack previousContents) {
		super.onContentsChanged(index, previousContents);
		if(index+1 >= stacks.size()) return;
		ItemStack first = stacks.get(index);
		ItemStack second = stacks.get(index+1);
		if(!first.isEmpty() && !second.isEmpty() && ItemStack.isSameItemSameComponents(first, second)) {
			int change = Math.min(second.getCount(), first.getMaxStackSize() - first.getCount());
			if(change <= 0) return;
			ItemStack previousSecond = second.copy();
			stacks.set(index, first.copyWithCount(first.getCount() + change));
			stacks.set(index+1, second.getCount() == change ? ItemStack.EMPTY : second.copyWithCount(second.getCount() - change));
			onContentsChanged(index+1, previousSecond);
		}
	}
}
