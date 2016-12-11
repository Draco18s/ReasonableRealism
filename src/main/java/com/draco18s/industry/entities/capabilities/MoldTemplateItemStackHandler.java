package com.draco18s.industry.entities.capabilities;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.util.RecipesUtils;
import com.draco18s.industry.ExpandedIndustryBase;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

public class MoldTemplateItemStackHandler extends ItemStackHandler {
	public MoldTemplateItemStackHandler() {
		super();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("expindustry:item_mold") && getRecipeForTemplate(nbt) != null) {
				return super.insertItem(slot, stack, simulate);
			}
		}
		return stack;
	}
	
	private IRecipe getRecipeForTemplate(NBTTagCompound nbt) {
		NBTTagCompound itemTags = nbt.getCompoundTag("expindustry:item_mold");
		ItemStack result = ItemStack.loadItemStackFromNBT(itemTags);
		
		return RecipesUtils.getRecipeWithOutput(result);
	}
}
