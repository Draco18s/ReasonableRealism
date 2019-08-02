package com.draco18s.harderores.enchantment;

import java.util.Set;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraftforge.common.ToolType;

public class PulverizeEnchantment extends Enchantment {

	public PulverizeEnchantment(EquipmentSlotType[] slots) {
		super(Enchantment.Rarity.UNCOMMON,EnchantmentType.DIGGER, slots);
	}
	
	@Override
	public int getMinEnchantability(int par1) {
		return 10 + 5 * par1 * 3;
	}
	
	@Override
	public int getMaxEnchantability(int par1) {
		return getMinEnchantability(par1) + 15;
	}
	
	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean canApplyTogether(Enchantment other) {
		boolean ret = super.canApplyTogether(other);
		ret &= other != Enchantments.SILK_TOUCH;
		ret &= other != Enchantments.EFFICIENCY;
		if(ret) {
			String name = other.getName().toLowerCase();
			ret &= !name.contains("smelt");
			ret &= !name.contains("heat");
			ret &= !name.contains("inferno");
			ret &= !name.contains("fiery");
		}
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
					boolean ret = true;
					String name = i.getTranslationKey().toLowerCase();
					ret &= !name.contains("smelt");
					ret &= !name.contains("heat");
					ret &= !name.contains("inferno");
					ret &= !name.contains("fiery");
					return ret;
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