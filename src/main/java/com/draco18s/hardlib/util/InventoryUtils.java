package com.draco18s.hardlib.util;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class InventoryUtils {
	public static void dropItemHandlerContents(final IItemHandler inventory, World world, BlockPos pos) {
		if(inventory == null) return;
		for(int i = 0; i < inventory.getSlots(); ++i) {
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
		}
	}
}
