package com.draco18s.industry.inventory;

import com.draco18s.hardlib.internal.CommonContainer;
import com.draco18s.industry.entities.TileEntityWoodenHopper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerExtHopper extends CommonContainer {
	TileEntityHopper tileEntity;

	public ContainerExtHopper(InventoryPlayer inventory, TileEntityHopper te) {
		super(5);
		tileEntity = te;
		IItemHandler inven = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		for (int i = 0; i < tileEntity.getSizeInventory(); ++i) {
			this.addSlotToContainer(new Slot(tileEntity, i, 44 + i * 18, 20));
		}

		bindPlayerInventory(inventory);
	}
	
	@Override
	protected void bindPlayerInventory(InventoryPlayer playerInventory) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 51 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 109));
		}
	}
}
