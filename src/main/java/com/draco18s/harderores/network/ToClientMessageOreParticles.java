package com.draco18s.harderores.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.draco18s.hardlib.HardLib;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ToClientMessageOreParticles {
	public int effectID;
	public BlockPos eventAt;
	public List<BlockPos> oresAt;

	public ToClientMessageOreParticles() {
		
	}
	
	public ToClientMessageOreParticles(int id, List<BlockPos> ore, BlockPos event) {
		effectID = id;
		eventAt = event;
		oresAt = ore;
	}
	
	public static void encode(ToClientMessageOreParticles msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.effectID);
		buf.writeInt(msg.eventAt.getX());
		buf.writeInt(msg.eventAt.getY());
		buf.writeInt(msg.eventAt.getZ());
		buf.writeInt(msg.oresAt.size());
		for(BlockPos p : msg.oresAt) {
			buf.writeBlockPos(p);
		}
	}

	public static ToClientMessageOreParticles decode(FriendlyByteBuf buf) {
		int effectID = buf.readInt();
		BlockPos eventAt = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		int num = buf.readInt();
		List<BlockPos> oresAt = new ArrayList<BlockPos>();
		for(int i = 0; i < num; i++) {
			BlockPos p = buf.readBlockPos();
			oresAt.add(p);
		}
		return new ToClientMessageOreParticles(effectID, oresAt, eventAt);
	}
	
	public static class Handler {
		public static void handle(final ToClientMessageOreParticles message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				HardLib.PROXY.spawnParticles(message, ctx);
			});
		}
	}
}