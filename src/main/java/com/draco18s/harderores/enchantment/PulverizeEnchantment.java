package com.draco18s.harderores.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/** Hard ore that is nearly mined out drops as milled dust (see HarderSetCount). One level, as of 1.14. */
public class PulverizeEnchantment extends PickaxeEnchantment {
	public PulverizeEnchantment(EquipmentSlot[] slots) {
		super(Rarity.UNCOMMON, slots);
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinCost(int level) {
		return 10 + 5 * level * 3;
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
		if(ret) {
			// Nor with other mods' auto-smelting enchantments.
			String name = other.getDescriptionId().toLowerCase();
			ret &= !name.contains("smelt");
			ret &= !name.contains("heat");
			ret &= !name.contains("inferno");
			ret &= !name.contains("fiery");
			ret &= !name.contains("forg");
		}
		return ret;
	}
}
