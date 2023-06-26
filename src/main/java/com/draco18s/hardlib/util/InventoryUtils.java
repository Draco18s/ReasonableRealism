package com.draco18s.hardlib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class InventoryUtils {

	public static void dropItemHandlerContents(final IItemHandler inventory, Level world, BlockPos pos) {
		if(inventory == null) return;
		for(int i = 0; i < inventory.getSlots(); ++i) {
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
		}
	}
}