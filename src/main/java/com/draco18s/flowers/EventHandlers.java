package com.draco18s.flowers;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.draco18s.flowers.config.ConfigHelper;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OreFlowers.MODID)
public class EventHandlers {
	private static Random rand = new Random();

	public static void onBonemeal(BonemealEvent event) {
		if(event.isCanceled()) return;
		if(event.getWorld().isRemote) return;
		Block eventBlock = event.getBlock().getBlock();
		if((eventBlock == Blocks.GRASS || eventBlock == Blocks.SAND) && (event.getPlayer() != null || ConfigHelper.poopBonemealFlowers )) {
			Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> list = HardLibAPI.oreFlowers.getOreList();
			List<OreFlowerData> entry;
			for(BlockWrapper ore : list.keySet()) {
				int count = 0;
				for (int scanLevel = 0; scanLevel < ConfigHelper.configScanDepth; scanLevel++) {
					BlockPos pos = event.getPos();
					pos.down(scanLevel * 8);
					//TODO: count += HardLibAPI.oreData.getOreData(event.getWorld(), pos, ore);
				}
				int orCt = count;
				if(count > 0) {
					count = (int)Math.min(Math.round(Math.log(count)), 10);
					entry = list.get(ore).getB();
					
					for(;--count >= 0;) {
						for(OreFlowerData data : entry) {
							if(count >= data.highConcentrationThreshold && event.getPlayer() != null) {
								HardLibAPI.oreFlowers.doSpawnFlowerCluster(event.getWorld(), event.getPos(), data.flower, rand, 1, 7, data.flower.has(BlockStateProperties.DOUBLE_BLOCK_HALF));
							}
							if(rand.nextBoolean() && (event.getPlayer() != null || rand.nextInt(128) == 0)) {
								HardLibAPI.oreFlowers.doSpawnFlowerCluster(event.getWorld(), event.getPos(), data.flower, rand, 1, 7, data.flower.has(BlockStateProperties.DOUBLE_BLOCK_HALF));
							}
						}
					}
					if(event.getPlayer() != null && event.getPlayer() instanceof ServerPlayerEntity) {
						HardLibAPI.Advancements.FOUND_ORE.trigger((ServerPlayerEntity) event.getPlayer(), orCt);
					}
				}
			}
			if(eventBlock == Blocks.SAND) {
				event.setResult(Result.ALLOW);
				int count = rand.nextInt(4) + 3;
				for(;--count >= 0;) {
					if(rand.nextBoolean() && (event.getPlayer() != null || rand.nextInt(128) == 0)) {
						HardLibAPI.oreFlowers.doSpawnFlowerCluster(event.getWorld(), event.getPos(), Blocks.DEAD_BUSH.getDefaultState(), rand, 1, 7, false);
					}
				}
			}
		}
	}
}
