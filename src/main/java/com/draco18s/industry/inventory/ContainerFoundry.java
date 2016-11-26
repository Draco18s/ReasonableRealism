package com.draco18s.industry.inventory;

import javax.annotation.Nullable;

import com.draco18s.hardlib.internal.CommonContainer;
import com.draco18s.hardlib.internal.inventory.SlotOutput;
import com.draco18s.industry.entities.TileEntityFoundry;
import com.draco18s.ores.entities.TileEntitySifter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerFoundry extends CommonContainer {
	TileEntityFoundry tileEntity;

	public ContainerFoundry(InventoryPlayer inventory, TileEntityFoundry tileEntity2) {
		super(3);
		tileEntity = tileEntity2;
		IItemHandler inven = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		
		addSlotToContainer(new SlotSticks(inven, 0, 71, 13));
		addSlotToContainer(new SlotIngots(inven, 1, 89, 13));
		
		addSlotToContainer(new SlotTemplate(inven, 2, 53, 35));
		
		addSlotToContainer(new SlotOutput(inven, 3, 80, 58));
		bindPlayerInventory(inventory);
	}
}
