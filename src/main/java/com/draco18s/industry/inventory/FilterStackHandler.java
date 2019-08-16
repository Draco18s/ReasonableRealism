package com.draco18s.industry.inventory;

import javax.annotation.Nonnull;

import com.draco18s.industry.entity.FilterTileEntity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class FilterStackHandler extends ItemStackHandler {
	private final FilterTileEntity filter;

	public FilterStackHandler(int slots, FilterTileEntity te) {
		super(slots);
		filter = te;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		ItemStack ret = super.insertItem(slot, stack, simulate);
		filter.dirtyFilters(slot);
		return ret;
	}
	
	@Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
		super.extractItem(slot, amount, simulate);
		filter.dirtyFilters(slot);
		return ItemStack.EMPTY;
    }
	
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return filter.allowableFilter(stack, slot);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		super.setStackInSlot(slot, stack);
		filter.dirtyFilters(slot);
	}
}