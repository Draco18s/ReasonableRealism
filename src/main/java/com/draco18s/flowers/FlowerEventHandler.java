package com.draco18s.flowers;

import CustomOreGen.Util.CogOreGenEvent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FlowerEventHandler {

	@SubscribeEvent
	public void onChunkGen(CogOreGenEvent event) {
		if(event.getWorld().isRemote || event.getWorld().provider.getDimension() == Integer.MIN_VALUE) return;
		Chunk c = event.getWorld().getChunkFromBlockCoords(event.getPos());
		int cx = c.xPosition;
		int cz = c.zPosition;
		OreFlowersBase.oreCounter.generate(cx, cz, event.getWorld());
	}
}
