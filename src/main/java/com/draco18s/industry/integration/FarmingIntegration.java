package com.draco18s.industry.integration;

import java.util.List;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.FarmingBase;
import com.draco18s.industry.ExpandedIndustryBase;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class FarmingIntegration {

	public static void addButcherKnifeMold(ItemStack base, List<ItemStack> subItems) {
		subItems.add(addImprint(base.copy(), new ItemStack(FarmingBase.butcherKnife)));
	}
	
	private static ItemStack addImprint(ItemStack out, ItemStack imprint) {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound itemTag = new NBTTagCompound();
		imprint.writeToNBT(itemTag);
		nbt.setTag("expindustry:item_mold", itemTag);
		out.setTagCompound(nbt);
		return out;
	}
}
