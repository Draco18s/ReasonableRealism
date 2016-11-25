package com.draco18s.industry.entities.capabilities;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.industry.ExpandedIndustryBase;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class MoldTemplateItemStackHandler extends ItemStackHandler {
	public MoldTemplateItemStackHandler() {
		super();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(stack.getItem() == ExpandedIndustryBase.itemMold) {
			if(stack.hasTagCompound())
				return super.insertItem(slot, stack, simulate);			
		}
		return stack;
	}
}
