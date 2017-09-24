package com.draco18s.ores.item;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDustSmall extends Item {
	public ItemDustSmall() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		EnumOreType v = EnumOreType.values()[stack.getItemDamage()];
		/*switch(v) {
			case IRON:
			case GOLD:
			case FLOUR:
			case SUGAR:
			case TIN:
			case COPPER:
			case LEAD:
			case SILVER:
			case NICKEL:
			case ALUMINUM:
			case PLATINUM:
			case ZINC:
			case OSMIUM:
				return "item.harderores:small_"+v.name+"_dust";
			default:
				return "item.harderores:unknown_small_dust";
		}*/
		return "item.harderores:small_"+v.name+"_dust";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			subItems.add(new ItemStack(this, 1, EnumOreType.IRON.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.GOLD.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.FLOUR.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.SUGAR.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.TIN.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.COPPER.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.LEAD.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.SILVER.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.NICKEL.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.ALUMINUM.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.PLATINUM.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.ZINC.meta));
			subItems.add(new ItemStack(this, 1, EnumOreType.OSMIUM.meta));
		}
	}
}
