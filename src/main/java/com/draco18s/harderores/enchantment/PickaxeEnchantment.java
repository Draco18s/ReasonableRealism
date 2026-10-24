package com.draco18s.harderores.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ToolActions;

/**
 * An enchantment for pickaxes only (as 1.12 had them), at the enchanting table and on an anvil. The DIGGER category
 * alone would also allow shovels, axes and hoes.
 */
public abstract class PickaxeEnchantment extends Enchantment {
	protected PickaxeEnchantment(Rarity rarity, EquipmentSlot[] slots) {
		super(rarity, EnchantmentCategory.DIGGER, slots);
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.canPerformAction(ToolActions.PICKAXE_DIG);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return canEnchant(stack);
	}
}
