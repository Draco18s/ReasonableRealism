package com.draco18s.ores.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ToClientMessageOreParticles implements IMessage {
	public int effectID;
	public BlockPos pos;

	public ToClientMessageOreParticles() {
		
	}
	
	public ToClientMessageOreParticles(int id, BlockPos p) {
		effectID = id;
		pos = p;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		effectID = buf.readInt();
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(effectID);
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}
}
