package com.draco18s.ores.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerOreCart extends Container {
	private final IInventory lowerChestInventory;
	private final int numRows;

	public ContainerOreCart(IInventory playerInventory, IInventory chestInventory, EntityPlayer player) {
		this.lowerChestInventory = chestInventory;
		int width = 5;
		this.numRows = chestInventory.getSizeInventory() / width;
		chestInventory.openInventory(player);
		int i = (this.numRows - 4) * 18;

		for (int j = 0; j < this.numRows; ++j) {
			for (int k = 0; k < width; ++k) {
				this.addSlotToContainer(new Slot(chestInventory, k + j * width, 26 + k * 18, 18 + j * 18));
			}
		}
		
		bindPlayerInventory(player.inventory);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.lowerChestInventory.isUseableByPlayer(playerIn);
	}

	@Override
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.numRows * 5) {
				if (!this.mergeItemStack(itemstack1, this.numRows * 5, this.inventorySlots.size(), true)) {
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 5, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack)null);
			}
			else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.lowerChestInventory.closeInventory(playerIn);
	}

	public IInventory getLowerChestInventory() {
		return this.lowerChestInventory;
	}

	protected void bindPlayerInventory(InventoryPlayer playerInventory) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 160));
		}
	}
}
