package com.draco18s.harderores.enchantment;

import java.util.Set;

import com.draco18s.harderores.HarderOres;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraftforge.common.ToolType;

public class ShatterEnchantment extends Enchantment {

	public ShatterEnchantment(EquipmentSlotType[] slots) {
		super(Enchantment.Rarity.UNCOMMON, EnchantmentType.DIGGER, slots);
	}
	
	@Override
	public int getMinEnchantability(int par1) {
		return 1 + 10 * par1;
	}
	
	@Override
	public int getMaxEnchantability(int par1) {
		return getMinEnchantability(par1) + 15;
	}
	
	@Override
	public int getMaxLevel() {
		return 4;
	}
	
	public boolean canApplyTogether(Enchantment other) {
		boolean ret = super.canApplyTogether(other);
		ret &= other != Enchantments.EFFICIENCY;
		ret &= other != Enchantments.SILK_TOUCH;
		ret &= other != HarderOres.ModEnchantments.cracker;
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
		return false;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return canApply(stack);
	}
}