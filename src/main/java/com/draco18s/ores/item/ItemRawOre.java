package com.draco18s.ores.item;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.hardlib.blockproperties.ores.EnumOreType;

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

public class ItemRawOre extends Item {
	public ItemRawOre() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		EnumOreType v = EnumOreType.values()[stack.getItemDamage()];
		switch(v) {
			case IRON:
			case GOLD:
			case DIAMOND:
			case LIMONITE:
			case TIN:
			case COPPER:
			case LEAD:
			case URANIUM:
			case SILVER:
			case NICKEL:
			case ALUMINUM:
			case OSMIUM:
				return "item.harderores:"+v.name+"_ore";
			default:
				return "item.harderores:unknown_raw_ore";
		}
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.IRON.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.GOLD.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.DIAMOND.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.LIMONITE.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.TIN.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.COPPER.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.LEAD.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.URANIUM.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.SILVER.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.NICKEL.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.ALUMINUM.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumOreType.OSMIUM.meta));
	}
}
