package com.draco18s.flowers;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.Level;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;

import CustomOreGen.Util.CogOreGenEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.Tuple;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class FlowerEventHandler {
	private boolean poopBonemealFlowers = false;
	private Random rand = new Random();
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if(HardLibAPI.oreFlowers.getOreList().size() == 0) {
			OreFlowersBase.instance.addAllOres();
		}
	}

	@SubscribeEvent
	public void onBonemeal(BonemealEvent event) {
		if(event.isCanceled()) return;
		if(event.getWorld().isRemote) return;
		Block eventBlock = event.getBlock().getBlock();
		if((eventBlock == Blocks.GRASS || eventBlock == Blocks.SAND) && (event.getEntityPlayer() != null || poopBonemealFlowers)) {
			Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> list = HardLibAPI.oreFlowers.getOreList();
			List<OreFlowerData> entry;
			for(BlockWrapper ore : list.keySet()) {
				int count = HardLibAPI.oreData.getOreData(event.getWorld(), event.getPos(), ore) +
				HardLibAPI.oreData.getOreData(event.getWorld(), event.getPos().down(8), ore) +
				HardLibAPI.oreData.getOreData(event.getWorld(), event.getPos().down(16), ore) +
				HardLibAPI.oreData.getOreData(event.getWorld(), event.getPos().down(24), ore);
				//OreFlowersBase.logger.log(Level.WARN, ore.block.getRegistryName() + ": " + count);
				int orCt = count;
				if(count > 0) {
					count = (int)Math.min(Math.round(Math.log(count)), 10);
					entry = list.get(ore).getSecond();
					
					for(;--count >= 0;) {
						for(OreFlowerData data : entry) {
							if(count >= data.highConcentrationThreshold && event.getEntityPlayer() != null) {
								HardLibAPI.oreFlowers.doSpawnFlowerCluster(event.getWorld(), event.getPos(), data.flower.withProperty(Props.FLOWER_STALK, false), rand, 1, 7, data.flower.getValue(Props.FLOWER_STALK), data.twoBlockChance);
							}
							if(rand.nextBoolean() && (event.getEntityPlayer() != null || rand.nextInt(128) == 0)) {
								HardLibAPI.oreFlowers.doSpawnFlowerCluster(event.getWorld(), event.getPos(), data.flower.withProperty(Props.FLOWER_STALK, false), rand, 1, 7, data.flower.getValue(Props.FLOWER_STALK), data.twoBlockChance);
							}
						}
					}
					if(event.getEntityPlayer() != null && event.getEntityPlayer() instanceof EntityPlayerMP) {
						HardLibAPI.Advancements.FOUND_ORE.trigger((EntityPlayerMP) event.getEntityPlayer(), orCt);
					}
				}
			}
			if(eventBlock == Blocks.SAND) {
				event.setResult(Result.ALLOW);
				int count = rand.nextInt(4) + 3;
				for(;--count >= 0;) {
					if(rand.nextBoolean() && (event.getEntityPlayer() != null || rand.nextInt(128) == 0)) {
						HardLibAPI.oreFlowers.doSpawnFlowerCluster(event.getWorld(), event.getPos(), Blocks.DEADBUSH.getDefaultState(), rand, 1, 7, false, 0);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onChunkGen(CogOreGenEvent event) {
		if(event.getWorld().isRemote || event.getWorld().provider.getDimension() <= Integer.MIN_VALUE+5) return;
		Chunk c = event.getWorld().getChunkFromBlockCoords(event.getPos());
		int cx = c.x;
		int cz = c.z;
		OreFlowersBase.oreCounter.generate(cx, cz, event.getWorld());
	}
	
	@SubscribeEvent
	public void chunkLoad(ChunkDataEvent.Load event) {
		if(!event.getWorld().isRemote)
			OreFlowersBase.dataHooks.readData(event.getWorld(), event.getChunk().x, event.getChunk().z, event.getData());
	}
	
	@SubscribeEvent
	public void chunkSave(ChunkDataEvent.Save event) {
		if(!event.getWorld().isRemote)
			OreFlowersBase.dataHooks.saveData(event.getWorld(), event.getChunk().x, event.getChunk().z, event.getData());
	}
	
	@SubscribeEvent
	public void chunkUnload(ChunkEvent.Unload event) {
		if(!event.getWorld().isRemote) {
			OreFlowersBase.dataHooks.clearData(event.getWorld(), event.getChunk().x, event.getChunk().z);
		}
	}
}
