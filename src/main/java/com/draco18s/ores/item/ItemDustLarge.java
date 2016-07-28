package com.draco18s.ores.item;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.hardlib.blockproperties.EnumOreType;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDustLarge extends Item {
	public ItemDustLarge() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		EnumOreType v = EnumOreType.values()[stack.getItemDamage()];
		switch(v) {
			case IRON:
			case GOLD:
			case FLOUR:
				return "item.harderores:large_"+v.name+"_dust";
			default:
				return "item.harderores:unknown_large_dust";
		}
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.IRON.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.GOLD.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.FLOUR.meta));
	}
}
