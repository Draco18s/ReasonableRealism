package com.draco18s.harderores.entity.capability;

import com.draco18s.hardlib.api.HardLibAPI;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class SiftableItemsHandler extends ItemStacksResourceHandler {
	public SiftableItemsHandler(int size) {
		super(size);
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		return !HardLibAPI.oreMachines.getSiftResult(resource.toStack(), false).isEmpty() || resource.typeHolder().is(Tags.Items.DUSTS);
	}
}
