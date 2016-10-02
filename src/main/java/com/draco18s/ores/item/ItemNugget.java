package com.draco18s.ores.item;

import java.util.List;

import com.draco18s.hardlib.blockproperties.EnumOreType;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemNugget extends Item {

	public ItemNugget() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		EnumOreType v = EnumOreType.values()[stack.getItemDamage()];
		switch(v) {
			case IRON:
				return "item.harderores:s"+v.name+"_nugget";
			default:
				return "item.harderores:unknown_nugget";
		}
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.IRON.meta));
	}
}
