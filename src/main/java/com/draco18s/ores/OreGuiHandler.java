package com.draco18s.ores;

import com.draco18s.ores.client.gui.GuiContainerOreCart;
import com.draco18s.ores.client.gui.GuiContainerPackager;
import com.draco18s.ores.client.gui.GuiContainerSifter;
import com.draco18s.ores.entities.EntityOreMinecart;
import com.draco18s.ores.entities.TileEntityPackager;
import com.draco18s.ores.entities.TileEntitySifter;
import com.draco18s.ores.inventory.ContainerOreCart;
import com.draco18s.ores.inventory.ContainerPackager;
import com.draco18s.ores.inventory.ContainerSifter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class OreGuiHandler implements IGuiHandler {
	public static int SIFTER = 0;
	public static int ORE_CART = 1;
	public static int PACKAGER = 2;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if(ID == SIFTER) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TileEntitySifter){
				return new ContainerSifter(player.inventory, (TileEntitySifter) tileEntity);
			}
		}
		if(ID == ORE_CART) {
			Entity ent = world.getEntityByID(x);
			if(ent instanceof EntityOreMinecart){
				EntityOreMinecart cart = (EntityOreMinecart)ent;
				return cart.createContainer(player.inventory, player);
			}
		}
		if(ID == PACKAGER) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TileEntityPackager){
				return new ContainerPackager(player.inventory, (TileEntityPackager) tileEntity);
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
		if(ID == ORE_CART) {
			Entity ent = world.getEntityByID(x);
			if(ent instanceof EntityOreMinecart){
				EntityOreMinecart cart = (EntityOreMinecart)ent;
				return new GuiContainerOreCart(new ContainerOreCart(player.inventory, cart, player), cart);
			}
		}
		if(ID == PACKAGER) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TileEntityPackager){
				return new GuiContainerPackager(new ContainerPackager(player.inventory, (TileEntityPackager) tileEntity), (TileEntityPackager)tileEntity);
			}
		}
		return null;
	}
}
