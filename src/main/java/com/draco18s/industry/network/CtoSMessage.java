package com.draco18s.industry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CtoSMessage implements IMessage {
	private int dim;
	private int posx;
	private int posy;
	private int posz;
	
	private int ordinal;
	
	public CtoSMessage() {
		this(0, BlockPos.ORIGIN, 0);
	}
	
	public CtoSMessage(int d, BlockPos pos, int o) {
		dim = d;
		posx = pos.getX();
		posy = pos.getY();
		posz = pos.getZ();
		
		ordinal = o;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		dim = buffer.readInt();
		posx = buffer.readInt();
		posy = buffer.readInt();
		posz = buffer.readInt();
		ordinal = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(dim);
		buffer.writeInt(posx);
		buffer.writeInt(posy);
		buffer.writeInt(posz);
		buffer.writeInt(ordinal);
	}
	
	public int dim() {
		return dim;
	}
	
	public int x() {
		return posx;
	}
	
	public int y() {
		return posy;
	}
	
	public int z() {
		return posz;
	}
	
	public int ordinal() {
		return ordinal;
	}
}
