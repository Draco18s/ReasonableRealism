package com.draco18s.ores.networking;

import com.draco18s.ores.entities.EntityOreMinecart.DumpDir;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ToServerMessageOreCart implements IMessage {
	public int entityID;
	public DumpDir dumpDir;

	public ToServerMessageOreCart() {
		
	}
	
	public ToServerMessageOreCart(int id, DumpDir dir) {
		entityID = id;
		dumpDir = dir;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = buf.readInt();
		dumpDir = DumpDir.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeInt(dumpDir.ordinal());
	}
}
