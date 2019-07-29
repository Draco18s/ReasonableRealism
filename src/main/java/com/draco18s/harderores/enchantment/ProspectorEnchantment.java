package com.draco18s.harderores.enchantment;

import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraftforge.common.ToolType;

public class ProspectorEnchantment extends Enchantment {

	public ProspectorEnchantment(EquipmentSlotType[] slots) {
		super(Enchantment.Rarity.RARE, EnchantmentType.create("DIGGER_COMPASS", new Predicate<Item>() {
			@Override
			public boolean test(Item itemIn) {
				return itemIn instanceof ToolItem || itemIn == Items.COMPASS;
			}
		}), slots);
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
		if(i instanceof ToolItem) {
			ToolItem tool = (ToolItem)i;
			Set<ToolType> classes = tool.getToolTypes(stack);
			for(ToolType cl : classes) {
				if(cl.equals(ToolType.PICKAXE)) {
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