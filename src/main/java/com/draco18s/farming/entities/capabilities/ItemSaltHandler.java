package com.draco18s.farming.entities.capabilities;

import java.lang.reflect.Array;

import org.apache.commons.lang3.ArrayUtils;

import com.draco18s.farming.FarmingBase;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

public class ItemSaltHandler extends ItemStackHandler {
	private int[] saltIDs;
	
	public ItemSaltHandler(int i) {
		super(i);
		saltIDs = new int[]{OreDictionary.getOreID("itemSalt"),
				OreDictionary.getOreID("dustSalt"),
				OreDictionary.getOreID("foodSalt")};
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		int[] ids = OreDictionary.getOreIDs(stack);
		boolean isvalid = false;
		for(int j : saltIDs) {
			isvalid |= ArrayUtils.contains(ids, j);
		}
		if(!isvalid) return stack;
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
}
