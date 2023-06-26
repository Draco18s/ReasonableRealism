package com.draco18s.harderores.enchantment;

import com.draco18s.harderores.HarderOres;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ToolActions;

public class PulverizeEnchantment extends Enchantment {

	public PulverizeEnchantment(EquipmentSlot[] slots) {
		super(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.DIGGER, slots);
	}
	
	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinCost(int par1) {
		return 10 + 5 * par1 * 3;
	}

	@Override
	public int getMaxCost(int par1) {
		return getMinCost(par1) + 15;
	}
	
	@Override
	protected boolean checkCompatibility(Enchantment other) {
		boolean ret = super.checkCompatibility(other);
		ret &= other != Enchantments.BLOCK_EFFICIENCY;
		ret &= other != Enchantments.SILK_TOUCH;
		ret &= other != HarderOres.ModEnchantments.cracker;
		return ret;
	}
	
	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) || stack.canPerformAction(ToolActions.PICKAXE_DIG);
	}
}
