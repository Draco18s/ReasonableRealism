package com.draco18s.industry.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class SlotIInventory extends SlotItemHandler {
	public SlotIInventory(IItemHandler inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		//return this.getItemHandler().isItemValid(slotNumber, stack);
		if(this.getItemHandler().isItemValid(slotNumber, stack)) {
			ItemStack ss = stack.copy();
			ss.setCount(1);
			((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(slotNumber, ss);
			//inventory.setInventorySlotContents(slotNumber, ss);
			
			//return true;
		}
		return false;
	}
	
	@Override
	public boolean canTakeStack(PlayerEntity par1EntityPlayer) {
		((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(slotNumber, ItemStack.EMPTY);
		return false;
		//return true;
	}
	
	/*@Override
    @Nonnull
    public ItemStack decrStackSize(int amount) {
        this.getItemHandler().extractItem(slotNumber, 64, false);
        return ItemStack.EMPTY;
    }*/
}