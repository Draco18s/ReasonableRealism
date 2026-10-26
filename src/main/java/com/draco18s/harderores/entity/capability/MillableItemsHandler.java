package com.draco18s.harderores.entity.capability;

import com.draco18s.hardlib.api.HardLibAPI;

import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class MillableItemsHandler extends ItemStacksResourceHandler {
	public MillableItemsHandler(int size) {
		super(size);
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		return !HardLibAPI.oreMachines.getMillResult(resource.toStack()).isEmpty();
	}
}
