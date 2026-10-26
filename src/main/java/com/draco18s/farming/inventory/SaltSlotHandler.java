package com.draco18s.farming.inventory;

import com.draco18s.farming.HarderFarming;

import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/** The salt on top of a tanning rack (anything tagged forge:dusts/salt). */
public class SaltSlotHandler extends ItemStacksResourceHandler {
	public SaltSlotHandler() {
		super(1);
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		return resource.typeHolder().is(HarderFarming.Tags.Items.SALT);
	}
}
