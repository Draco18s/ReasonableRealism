package com.draco18s.flowers.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemStickyBlob extends Item {
	public ItemStickyBlob() {
		this.setHasSubtypes(false);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	@Override
	public int getItemBurnTime(ItemStack fuel) {
		//if(fuel.getItem() == this) {
			return 150;
		//}
		//return 0;
	}
}
