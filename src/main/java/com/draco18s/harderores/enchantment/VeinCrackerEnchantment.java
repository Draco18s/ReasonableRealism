package com.draco18s.harderores.enchantment;

import com.draco18s.harderores.HarderOres;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ToolActions;

public class VeinCrackerEnchantment extends Enchantment {

	public VeinCrackerEnchantment(EquipmentSlot[] slots) {
		super(Enchantment.Rarity.RARE, EnchantmentCategory.DIGGER, slots);
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public int getMinCost(int par1) {
		return 1 + 10 * par1;
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
		ret &= other != HarderOres.ModEnchantments.shatter;
		return ret;
	}
	
	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) || stack.canPerformAction(ToolActions.PICKAXE_DIG);
	}
}
