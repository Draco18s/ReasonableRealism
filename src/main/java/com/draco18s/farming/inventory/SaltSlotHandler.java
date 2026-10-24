package com.draco18s.farming.inventory;

import com.draco18s.farming.HarderFarming;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/** The salt on top of a tanning rack (anything tagged forge:dusts/salt). */
public class SaltSlotHandler extends ItemStackHandler {
	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return stack.is(HarderFarming.Tags.Items.SALT);
	}
}
