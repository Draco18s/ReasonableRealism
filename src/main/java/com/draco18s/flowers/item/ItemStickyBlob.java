package com.draco18s.flowers.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemStickyBlob extends Item {

	public ItemStickyBlob() {
		super(new Properties().group(ItemGroup.MATERIALS));
	}
	
	@Override
	public int getBurnTime(ItemStack itemStack) {
		return 150;
	}
}