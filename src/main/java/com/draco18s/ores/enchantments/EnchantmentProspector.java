package com.draco18s.ores.enchantments;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

public class EnchantmentProspector extends Enchantment {

	public EnchantmentProspector(EntityEquipmentSlot[] slots) {
		super(Enchantment.Rarity.RARE, EnumEnchantmentType.DIGGER, slots);
	}
	
	@Override
	public int getMinEnchantability(int par1) {
		return 15 * par1 - 5;
	}
	
	@Override
	public int getMaxEnchantability(int par1) {
		return getMinEnchantability(par1) + 15;
	}
	
	@Override
	public int getMaxLevel() {
		return 3;
	}
	
	public boolean canApplyTogether(Enchantment other) {
		boolean ret = super.canApplyTogether(other);
		ret |= other != Enchantments.FORTUNE;
		return ret;
	}
	
	@Override
	public boolean canApply(ItemStack stack) {
		Item i = stack.getItem();
		if(i instanceof ItemTool) {
			ItemTool tool = (ItemTool)i;
			Set<String> classes = tool.getToolClasses(stack);
			for(String cl : classes) {
				if(cl.equals("pickaxe")) {
					return true;
				}
			}
		}
		else if(i == Items.COMPASS) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return canApply(stack);
	}
}
