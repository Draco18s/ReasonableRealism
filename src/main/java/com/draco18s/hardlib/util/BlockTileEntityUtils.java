package com.draco18s.hardlib.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockTileEntityUtils {
	public static void dropItems(World worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		IItemHandler inventory = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			EntityItem entityIn;
			if (stack != null && !worldIn.isRemote) {
				entityIn = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				entityIn.setDefaultPickupDelay();
				worldIn.spawnEntityInWorld(entityIn);
			}
		}
	}
}
