package com.draco18s.harderores.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Breaking stone with it shows nearby hard ore (particles); at level III it sometimes turns up a little dust.
 * Works on a pickaxe, or on a compass held in the off hand.
 */
public class ProspectorEnchantment extends PickaxeEnchantment {
	public ProspectorEnchantment(EquipmentSlot[] slots) {
		super(Rarity.RARE, slots);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinCost(int level) {
		return 15 * level - 5;
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + 15;
	}

	@Override
	protected boolean checkCompatibility(Enchantment other) {
		return super.checkCompatibility(other) && other != Enchantments.BLOCK_FORTUNE;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) || stack.is(Items.COMPASS);
	}
}
