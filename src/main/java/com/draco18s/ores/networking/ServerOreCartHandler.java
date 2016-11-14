package com.draco18s.ores.networking;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.draco18s.hardlib.interfaces.IBlockMultiBreak;
import com.draco18s.ores.client.ProspectorParticleDust;
import com.draco18s.ores.entities.EntityOreMinecart;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerOreCartHandler implements IMessageHandler<ToServerMessageOreCart, IMessage> {

	public IMessage onMessage(final ToServerMessageOreCart message, final MessageContext ctx) {
		//System.out.println("===###Packet Recieved###===");
		final WorldServer mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				//System.out.println("===###Running###===");
				Entity ent = mainThread.getEntityByID(message.entityID);
				if(ent instanceof EntityOreMinecart) {
					EntityOreMinecart cart = (EntityOreMinecart)ent;
					//System.out.println("===###"+message.dumpDir+"###===");
					cart.setDumpDir(message.dumpDir);
					//System.out.println("===###"+cart.getDumpDir()+"###===");
				}
			}
		});
		return null;
	}
}
