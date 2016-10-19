package com.draco18s.ores.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ToClientMessageOreParticles implements IMessage {
	public int effectID;
	public BlockPos eventAt;
	public BlockPos oreAt;

	public ToClientMessageOreParticles() {
		
	}
	
	public ToClientMessageOreParticles(int id, BlockPos event, BlockPos ore) {
		effectID = id;
		eventAt = event;
		oreAt = ore;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		effectID = buf.readInt();
		oreAt = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		eventAt = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(effectID);
		buf.writeInt(oreAt.getX());
		buf.writeInt(oreAt.getY());
		buf.writeInt(oreAt.getZ());
		buf.writeInt(eventAt.getX());
		buf.writeInt(eventAt.getY());
		buf.writeInt(eventAt.getZ());
	}
}
