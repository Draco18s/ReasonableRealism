package com.draco18s.harderores.enchantment;

import com.draco18s.harderores.HarderOres;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/** Breaking a hard ore block also takes a layer off neighboring blocks of the same ore. */
public class VeinCrackerEnchantment extends PickaxeEnchantment {
	public VeinCrackerEnchantment(EquipmentSlot[] slots) {
		super(Rarity.UNCOMMON, slots);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinCost(int level) {
		return 5 + 8 * level;
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
		ret &= other != HarderOres.ModEnchantments.shatter;
		return ret;
	}
}
