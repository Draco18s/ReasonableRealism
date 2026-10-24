package com.draco18s.harderores.enchantment;

import com.draco18s.harderores.HarderOres;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/** "Ore Breaker": each break takes extra layers off a hard ore block, but digs ore at half speed. */
public class ShatterEnchantment extends PickaxeEnchantment {
	public ShatterEnchantment(EquipmentSlot[] slots) {
		super(Rarity.UNCOMMON, slots);
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public int getMinCost(int level) {
		return 1 + 10 * level;
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + 15;
	}

	@Override
	protected boolean checkCompatibility(Enchantment other) {
		boolean ret = super.checkCompatibility(other);
		ret &= other != Enchantments.BLOCK_EFFICIENCY;
		ret &= other != Enchantments.SILK_TOUCH;
		ret &= other != HarderOres.ModEnchantments.cracker;
		return ret;
	}
}
