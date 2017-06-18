package com.draco18s.industry.inventory;

import com.draco18s.hardlib.api.internal.CommonContainer;
import com.draco18s.industry.entities.TileEntityFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerFilter extends CommonContainer {
	protected TileEntityFilter tileEntity;

	public ContainerFilter(InventoryPlayer inventoryPlayer, TileEntityFilter te) {
		super(11);
		tileEntity = te;
		for (int i = 0; i < tileEntity.getSizeInventory(); ++i) {
			this.addSlotToContainer(new SlotValidInsert(tileEntity, i, 8 + i * 18, 17));
		}
		this.addSlotToContainer(new SlotIInventory(tileEntity, 5, 8, 48));
		this.addSlotToContainer(new SlotIInventory(tileEntity, 6, 26, 48));
		this.addSlotToContainer(new SlotIInventory(tileEntity, 7, 53, 48));
		this.addSlotToContainer(new SlotIInventory(tileEntity, 8, 71, 48));
		this.addSlotToContainer(new SlotIInventory(tileEntity, 9, 98, 48));
		this.addSlotToContainer(new SlotIInventory(tileEntity, 10, 116, 48));
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return tileEntity.isUsableByPlayer(playerIn);
	}
	
	@Override
	protected void bindPlayerInventory(InventoryPlayer playerInventory) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 78 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 136));
		}
	}
}
