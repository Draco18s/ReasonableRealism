package com.draco18s.harderores.network;

import java.util.ArrayList;
import java.util.List;


import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.draco18s.harderores.HarderOres;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ToClientMessageOreParticles implements CustomPacketPayload {
	public static final Type<ToClientMessageOreParticles> TYPE = new Type<ToClientMessageOreParticles>(HarderOres.getRL("ore_particles"));
	public static final StreamCodec<FriendlyByteBuf, ToClientMessageOreParticles> STREAM_CODEC = StreamCodec.of(
			(buf, msg) -> encode(msg, buf), ToClientMessageOreParticles::decode);

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
	
	@Override
	public Type<ToClientMessageOreParticles> type() {
		return TYPE;
	}

	public static class Handler {
		/** Runs on the client's main thread (NeoForge's default for payload handlers). */
		public static void handle(final ToClientMessageOreParticles message, IPayloadContext ctx) {
			HarderOres.PROXY.spawnParticles(message, ctx);
		}
	}
}