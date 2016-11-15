package com.draco18s.ores.entities.capabilities;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.ores.OresBase;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerDirt extends ItemStackHandler {

	public ItemStackHandlerDirt() {
		super();
	}

	public ItemStackHandlerDirt(int size) {
		super(size);
	}

	public ItemStackHandlerDirt(ItemStack[] stacks) {
		super(stacks);
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(stack != null && stack.getItem() instanceof ItemBlock) {
			ItemBlock ib = (ItemBlock)stack.getItem();
			if((OresBase.sluiceAllowDirt && ib.block == Blocks.DIRT) || ib.block == Blocks.SAND || ib.block == Blocks.GRAVEL) {
				return super.insertItem(slot, stack, simulate);
			}
		}
		return stack;
	}
}
