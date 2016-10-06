package com.draco18s.industry;

import com.draco18s.industry.client.gui.GuiContainerWoodenHopper;
import com.draco18s.industry.entities.TileEntityWoodenHopper;
import com.draco18s.industry.inventory.ContainerWoodenHopper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static int WOODEN_HOPPER = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if(ID == WOODEN_HOPPER) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TileEntityWoodenHopper){
				return new ContainerWoodenHopper(player.inventory, (TileEntityWoodenHopper) tileEntity);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if(ID == WOODEN_HOPPER) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TileEntityWoodenHopper){
				return new GuiContainerWoodenHopper(new ContainerWoodenHopper(player.inventory, (TileEntityWoodenHopper) tileEntity), (TileEntityWoodenHopper)tileEntity);
			}
    	}
		return null;
	}
}
