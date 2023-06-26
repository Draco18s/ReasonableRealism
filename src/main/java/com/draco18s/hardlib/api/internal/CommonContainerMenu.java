package com.draco18s.hardlib.api.internal;

import javax.annotation.Nonnull;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * No-nonsense container class that does all the thigns a container needs to do.<br>
 * Every "container" TE can create a subclass of this to use for a GUI.
 * @author Draco18s
 *
 */
public abstract class CommonContainerMenu extends AbstractContainerMenu {
	protected final int invenSize;
	protected final int windowHeight;
	protected final Container container;

	public CommonContainerMenu(MenuType<?> p_38851_, int p_38852_, int size, int height) {
		super(p_38851_, p_38852_);
		invenSize = size;
		windowHeight = height;
		container = new SimpleContainer(size);
	}

	public CommonContainerMenu(MenuType<?> p_38851_, int p_39643_, int height, Inventory playerInventory, Container p_39645_) {
		super(p_38851_, p_39643_);
		this.container = p_39645_;
		invenSize = container.getContainerSize();
		windowHeight = height;
		container.startOpen(playerInventory.player);
		bindPlayerInventory(playerInventory);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return container.stillValid(playerIn);
	}

	protected void bindPlayerInventory(Inventory playerInventory) {
		for(int l = 0; l < 3; ++l) {
			for(int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + windowHeight - 82));
			}
		}

		for(int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, windowHeight - 24));
		}
	}

	@Override
	@Nonnull
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if (index >= this.slots.size() - this.container.getContainerSize()) {
				if (!this.moveItemStackTo(itemstack1, 0, this.slots.size()-this.container.getContainerSize(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, this.slots.size()-this.container.getContainerSize(), this.slots.size(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.getCount() == 0) {
				slot.setByPlayer(ItemStack.EMPTY);
			}
			else {
				slot.setChanged();
			}
		}

		return itemstack;
	}
}