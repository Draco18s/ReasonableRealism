package com.draco18s.industry.network;

import com.draco18s.industry.entities.TileEntityFilter;
import com.draco18s.industry.entities.TileEntityFilter.EnumAcceptType;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHandlerServer implements IMessageHandler<CtoSMessage, IMessage> {

	@Override
	public IMessage onMessage(final CtoSMessage packet, MessageContext context) {
		final EntityPlayerMP player = context.getServerHandler().player;
		final World world = player.world;
		//System.out.println("Packet recieved");
		final IThreadListener mainThread = (WorldServer) world;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				
				try {
					//World world = DimensionManager.getWorld(packet.dim());
					
					TileEntity te = world.getTileEntity(new BlockPos(packet.x(), packet.y(), packet.z()));
					if(te instanceof TileEntityFilter) {
						//System.out.println("TE updated");
						((TileEntityFilter)te).setEnumType(EnumAcceptType.values()[packet.ordinal()]);
					}
					else {
						//System.out.println("No TE!?");
					}
					
				}
				catch(Exception e) {
					//System.out.println("Something bad :(");
				}
			}
		});
		return null;
	}

}
