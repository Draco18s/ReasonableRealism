package com.draco18s.ores;

import com.draco18s.ores.client.gui.GuiContainerSifter;
import com.draco18s.ores.entities.TileEntitySifter;
import com.draco18s.ores.inventory.ContainerSifter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static int SIFTER = 0;

	public GuiHandler() {
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if(ID == SIFTER) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TileEntitySifter){
				return new ContainerSifter(player.inventory, (TileEntitySifter) tileEntity);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if(ID == SIFTER) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TileEntitySifter){
				return new GuiContainerSifter(new ContainerSifter(player.inventory, (TileEntitySifter) tileEntity), (TileEntitySifter)tileEntity);
			}
    	}
		return null;
	}
}
