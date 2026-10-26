package com.draco18s.hardlib.api.internal.inventory;

import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

/** A menu slot showing one index of an item handler, which it changes directly (SlotItemHandler's replacement). */
public class HandlerSlot extends ResourceHandlerSlot {

	public HandlerSlot(ItemStacksResourceHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, itemHandler::set, index, xPosition, yPosition);
	}
}
