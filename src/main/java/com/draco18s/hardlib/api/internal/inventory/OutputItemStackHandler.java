package com.draco18s.hardlib.api.internal.inventory;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Item handler wrapper that allows for all the usual inventory item manipulation except
 * that when exposed externally (GUI, hopper, etc.), items may not be inserted, only extracted.
 * As an IndexModifier it still sets the hidden handler's contents directly (for menu slots).
 * @author Draco18s
 *
 */
public class OutputItemStackHandler extends DelegatingResourceHandler<ItemResource> implements IndexModifier<ItemResource> {
	private final ItemStacksResourceHandler internalSlot;

	public OutputItemStackHandler(ItemStacksResourceHandler hidden) {
		super(hidden);
		internalSlot = hidden;
	}

	@Override
	public void set(int index, ItemResource resource, int amount) {
		internalSlot.set(index, resource, amount);
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		return false;
	}

	@Override
	public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public int insert(ItemResource resource, int amount, TransactionContext transaction) {
		return 0;
	}
}
