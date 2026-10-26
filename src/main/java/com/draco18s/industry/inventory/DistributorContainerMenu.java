package com.draco18s.industry.inventory;

import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class DistributorContainerMenu extends ExtHopperContainerMenu {
	public DistributorContainerMenu(int windowID, Inventory p_39641_) {
		this(windowID, p_39641_, new ItemStackHandler(5), new SimpleContainer(5));
	}
	
	public DistributorContainerMenu(int windowID, int size) {
		super(ExpandedIndustry.ModContainerTypes.machine_distributor, windowID, size);
	}

	public DistributorContainerMenu(int windowID, Inventory playerInventory, IItemHandler inven, Container container) {
		super(ExpandedIndustry.ModContainerTypes.machine_distributor, windowID, playerInventory, container);
		for(int j = 0; j < 5; ++j) {
			this.addSlot(new SlotItemHandler(inven, j, 44 + j * 18, 20));
		}
	}
}
