package com.draco18s.industry;

import com.draco18s.industry.client.gui.GuiContainerExtHopper;
import com.draco18s.industry.inventory.ContainerExtHopper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class IndustryGuiHandler implements IGuiHandler {
	public static int EXT_HOPPER = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if(ID == EXT_HOPPER) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TileEntityHopper){
				return new ContainerExtHopper(player.inventory, (TileEntityHopper) tileEntity);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if(ID == EXT_HOPPER) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TileEntityHopper){
				return new GuiContainerExtHopper(new ContainerExtHopper(player.inventory, (TileEntityHopper) tileEntity), (TileEntityHopper)tileEntity);
			}
		}
		return null;
	}
}
