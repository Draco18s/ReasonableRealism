package com.draco18s.harderores.enchantment;

import java.util.function.Predicate;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ToolActions;

public class ProspectorEnchantment extends Enchantment {

	public ProspectorEnchantment(EquipmentSlot[] slots) {
		super(Enchantment.Rarity.RARE, EnchantmentCategory.create("DIGGER_COMPASS", new Predicate<Item>() {
			@Override
			public boolean test(Item itemIn) {
				return itemIn == Items.COMPASS;
			}
		}), slots);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinCost(int p_44679_) {
		return 15 * p_44679_ - 5;
	}

	@Override
	public int getMaxCost(int p_44691_) {
		return getMinCost(p_44691_) + 15;
	}

	@Override
	protected boolean checkCompatibility(Enchantment other) {
		boolean ret = super.checkCompatibility(other);
		ret |= other != Enchantments.BLOCK_FORTUNE;
		return ret;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack) || stack.canPerformAction(ToolActions.PICKAXE_DIG);
	}
}