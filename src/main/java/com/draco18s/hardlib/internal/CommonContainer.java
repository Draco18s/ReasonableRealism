package com.draco18s.hardlib.internal;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CommonContainer extends Container {
	protected final int invenSize;
	
	public CommonContainer(int size) {
		invenSize = size;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	protected void bindPlayerInventory(InventoryPlayer playerInventory) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
		}
	}

	@Override
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index <  invenSize) {
				if (!this.mergeItemStack(itemstack1, invenSize, this.inventorySlots.size(), true)) {
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, invenSize, false)) {
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
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection){
	    boolean flag = false;
	    int i = startIndex;
	    if (reverseDirection) i = endIndex - 1;
	    
	    if (stack.isStackable()){
	        while (stack.stackSize > 0 && (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex)){
	            Slot slot = (Slot)this.inventorySlots.get(i);
	            ItemStack itemstack = slot.getStack();
	            int maxLimit = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
	            
	            if (itemstack != null && areItemStacksEqual(stack, itemstack)){
	                int j = itemstack.stackSize + stack.stackSize;
	                if (j <= maxLimit){
	                    stack.stackSize = 0;
	                    itemstack.stackSize = j;
	                    slot.onSlotChanged();
	                    flag = true;
	                    
	                }else if (itemstack.stackSize < maxLimit){
	                    stack.stackSize -= maxLimit - itemstack.stackSize;
	                    itemstack.stackSize = maxLimit;
	                    slot.onSlotChanged();
	                    flag = true;
	                }
	            }
	            if (reverseDirection){ 
	            	--i;
	            }else ++i;
	        }
	    }
	    if (stack.stackSize > 0){
	        if (reverseDirection){
	            i = endIndex - 1;
	        }else i = startIndex;

	        while (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex){
	            Slot slot1 = (Slot)this.inventorySlots.get(i);
	            ItemStack itemstack1 = slot1.getStack();

	            if (itemstack1 == null && slot1.isItemValid(stack)){ // Forge: Make sure to respect isItemValid in the slot.
	            	if(stack.stackSize <= slot1.getSlotStackLimit()){
	            	    slot1.putStack(stack.copy());
	                    slot1.onSlotChanged();
	                    stack.stackSize = 0;
	                    flag = true;
	                    break;
	            	}else{
	            	    itemstack1 = stack.copy();
	            	    stack.stackSize -= slot1.getSlotStackLimit();
	                    itemstack1.stackSize = slot1.getSlotStackLimit();
	                    slot1.putStack(itemstack1);
	                    slot1.onSlotChanged();
	                    flag = true;
	            	}                    
	            }
	            if (reverseDirection){
	                --i;
	            }else ++i;
	        }
	    }
	    return flag;
	}
	
    private static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB)
    {
        return stackB.getItem() == stackA.getItem() && (!stackA.getHasSubtypes() || stackA.getMetadata() == stackB.getMetadata()) && ItemStack.areItemStackTagsEqual(stackA, stackB);
    }
}
