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
		if(slot+1 >= stacks.size()) return;
		if(this.stacks.get(slot) != ItemStack.EMPTY && this.stacks.get(slot+1) != ItemStack.EMPTY) {
			if(ItemStack.areItemsEqual(this.stacks.get(slot), this.stacks.get(slot+1))) {
				int totalSize = this.stacks.get(slot).getCount() + this.stacks.get(slot+1).getCount();
				int change = this.stacks.get(slot+1).getCount();
				if(totalSize > this.stacks.get(slot).getMaxStackSize()) {
					change = this.stacks.get(slot).getMaxStackSize() - this.stacks.get(slot).getCount();
				}
				this.stacks.get(slot).grow(change);
				this.stacks.get(slot+1).shrink(change);
				if(this.stacks.get(slot+1).isEmpty()) {
					this.stacks.set(slot+1, ItemStack.EMPTY);
				}
				onContentsChanged(slot+1);
			}
		}
	}
}
