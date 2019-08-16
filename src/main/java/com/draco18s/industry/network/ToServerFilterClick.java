package com.draco18s.industry.network;

import java.util.function.Supplier;

import com.draco18s.industry.entity.FilterTileEntity;
import com.draco18s.industry.entity.FilterTileEntity.EnumAcceptType;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ToServerFilterClick {
	private int dim;
	private BlockPos pos;	
	
	private int ordinal;
	
	//public ToServerFilterClick() {
	//	this(0, BlockPos.ZERO, 0);
	//}
	
	public ToServerFilterClick(int d, BlockPos pos, int o) {
		dim = d;
		this.pos = pos;
		
		ordinal = o;
	}
	
	public static void encode(ToServerFilterClick msg, PacketBuffer buf) {
		buf.writeInt(msg.dim);
		buf.writeInt(msg.pos.getX());
		buf.writeInt(msg.pos.getY());
		buf.writeInt(msg.pos.getZ());
		buf.writeInt(msg.ordinal);
	}

	public static ToServerFilterClick decode(PacketBuffer buf) {
		int dim = buf.readInt();
		int posx = buf.readInt();
		int posy = buf.readInt();
		int posz = buf.readInt();
		int ordinal = buf.readInt();
		BlockPos p = new BlockPos(posx,posy,posz);
		
		return new ToServerFilterClick(dim, p, ordinal);
	}
	
	public static class Handler {
		public static void handle(final ToServerFilterClick message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				TileEntity te = ctx.get().getSender().getServerWorld().getTileEntity(message.pos);
				if(te instanceof FilterTileEntity) {
					//System.out.println("TE updated");
					((FilterTileEntity)te).setEnumType(EnumAcceptType.values()[message.ordinal]);
				}
				else {
					//System.out.println("No TE!?");
				}
			});
		}
	}
}
