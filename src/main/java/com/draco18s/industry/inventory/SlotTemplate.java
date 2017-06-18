package com.draco18s.industry.inventory;

import com.draco18s.industry.ExpandedIndustryBase;
import com.draco18s.industry.entities.capabilities.CastingItemStackHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotTemplate extends SlotItemHandler {
	public SlotTemplate(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return super.isItemValid(stack) && isValidMold(stack);
	}

	private boolean isValidMold(ItemStack stack) {
		if(stack.getItem() == ExpandedIndustryBase.itemMold) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt == null) return false;
			NBTTagCompound itemTags = nbt.getCompoundTag("expindustry:item_mold");
			ItemStack result = new ItemStack(itemTags);
			
			return result != null;
		}
		return false;
	}
}
