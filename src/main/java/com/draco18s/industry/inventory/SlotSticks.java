package com.draco18s.industry.inventory;

import java.util.List;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.industry.entities.capabilities.CastingItemStackHandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class SlotSticks extends SlotItemHandler {
	
	public SlotSticks(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return super.isItemValid(stack) && CastingItemStackHandler.isStick(stack);
	}
}
