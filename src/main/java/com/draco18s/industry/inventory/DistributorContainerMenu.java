package com.draco18s.industry.inventory;

import com.draco18s.hardlib.api.internal.inventory.HandlerSlot;
import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class DistributorContainerMenu extends ExtHopperContainerMenu {
	public DistributorContainerMenu(int windowID, Inventory p_39641_) {
		this(windowID, p_39641_, new ItemStacksResourceHandler(5), new SimpleContainer(5));
	}
	
	public DistributorContainerMenu(int windowID, int size) {
		super(ExpandedIndustry.ModContainerTypes.machine_distributor, windowID, size);
	}

	public DistributorContainerMenu(int windowID, Inventory playerInventory, ItemStacksResourceHandler inven, Container container) {
		super(ExpandedIndustry.ModContainerTypes.machine_distributor, windowID, playerInventory, container);
		for(int j = 0; j < 5; ++j) {
			this.addSlot(new HandlerSlot(inven, j, 44 + j * 18, 20));
		}
	}
}
