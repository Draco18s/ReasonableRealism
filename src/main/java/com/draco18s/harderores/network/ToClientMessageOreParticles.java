package com.draco18s.harderores.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ToClientMessageOreParticles {
	public int effectID;
	public BlockPos eventAt;
	public BlockPos oreAt;

	public ToClientMessageOreParticles() {
		
	}
	
	public ToClientMessageOreParticles(int id, BlockPos ore, BlockPos event) {
		effectID = id;
		eventAt = event;
		oreAt = ore;
	}
	
	public static void encode(ToClientMessageOreParticles msg, PacketBuffer buf)
	{
		buf.writeInt(msg.effectID);
		buf.writeInt(msg.oreAt.getX());
		buf.writeInt(msg.oreAt.getY());
		buf.writeInt(msg.oreAt.getZ());
		buf.writeInt(msg.eventAt.getX());
		buf.writeInt(msg.eventAt.getY());
		buf.writeInt(msg.eventAt.getZ());
	}

	public static ToClientMessageOreParticles decode(PacketBuffer buf)
	{
		int effectID = buf.readInt();
		BlockPos oreAt = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		BlockPos eventAt = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		return new ToClientMessageOreParticles(effectID, oreAt, eventAt);
	}
	
	public static class Handler
	{
		public static void handle(final ToClientMessageOreParticles message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> {
				
			});
		}
	}
}