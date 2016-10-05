package com.draco18s.flowers;

import java.util.List;
import java.util.Map;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.internal.BlockWrapper;
import com.draco18s.hardlib.internal.OreFlowerData;
import com.draco18s.hardlib.internal.OreFlowerDictator;

import CustomOreGen.Util.CogOreGenEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.Tuple;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FlowerEventHandler {
	private boolean poopBonemealFlowers = false;

	@SubscribeEvent
	public void onBonemeal(BonemealEvent event) {
		Block eventBlock = event.getBlock().getBlock();
		if(!event.getWorld().isRemote && (eventBlock == Blocks.GRASS || eventBlock == Blocks.SAND) && (event.getEntityPlayer() != null || poopBonemealFlowers)) {
			Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> list = HardLibAPI.oreFlowers.getOreList();
			OreFlowerData entry;
			for(BlockWrapper ore : list.keySet()) {
				int count = HardLibAPI.oreData.getOreData(event.getWorld(), event.getPos(), ore) +
				HardLibAPI.oreData.getOreData(event.getWorld(), event.getPos().down(8), ore) +
				HardLibAPI.oreData.getOreData(event.getWorld(), event.getPos().down(16), ore) +
				HardLibAPI.oreData.getOreData(event.getWorld(), event.getPos().down(24), ore);
				
				System.out.println("Found: " + ore.block.getRegistryName() + " = " + count);
			}
		}
	}

	@SubscribeEvent
	public void onChunkGen(CogOreGenEvent event) {
		if(event.getWorld().isRemote || event.getWorld().provider.getDimension() == Integer.MIN_VALUE) return;
		Chunk c = event.getWorld().getChunkFromBlockCoords(event.getPos());
		int cx = c.xPosition;
		int cz = c.zPosition;
		OreFlowersBase.oreCounter.generate(cx, cz, event.getWorld());
	}
	
	@SubscribeEvent
	public void chunkLoad(ChunkDataEvent.Load event) {
		if(!event.getWorld().isRemote)
			OreFlowersBase.dataHooks.readData(event.getWorld(), event.getChunk().xPosition, event.getChunk().zPosition, event.getData());
	}
	
	@SubscribeEvent
	public void chunkSave(ChunkDataEvent.Save event) {
		if(!event.getWorld().isRemote)
			OreFlowersBase.dataHooks.saveData(event.getWorld(), event.getChunk().xPosition, event.getChunk().zPosition, event.getData());
	}
	
	@SubscribeEvent
	public void chunkUnload(ChunkEvent.Unload event) {
		if(!event.getWorld().isRemote) {
			OreFlowersBase.dataHooks.clearData(event.getWorld(), event.getChunk().xPosition, event.getChunk().zPosition);
		}
	}
}
