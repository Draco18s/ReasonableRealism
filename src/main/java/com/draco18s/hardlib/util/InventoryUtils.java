package com.draco18s.hardlib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

public class InventoryUtils {

	public static void dropItemHandlerContents(final ResourceHandler<ItemResource> inventory, Level world, BlockPos pos) {
		if(inventory == null) return;
		for(int i = 0; i < inventory.size(); ++i) {
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ItemUtil.getStack(inventory, i));
		}
	}
}
