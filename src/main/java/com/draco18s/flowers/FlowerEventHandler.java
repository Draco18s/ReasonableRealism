package com.draco18s.flowers;

import java.util.List;
import java.util.Map;

import com.draco18s.flowers.util.ChunkOreCounter;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.advancement.FoundOreTrigger;
import com.draco18s.hardlib.api.interfaces.IFlowerData;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;
import com.draco18s.hardlib.util.Tuple;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.loot.LootParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

import CustomOreGen.Util.CogOreGenEvent;

@EventBusSubscriber(modid = OreFlowers.MODID)
public class FlowerEventHandler {
	private static final RandomSource rand = RandomSource.create();

	@SubscribeEvent
	public static void onTagsUpdated(TagsUpdatedEvent event) {
		// Ores are registered by tag, so the list is (re)built whenever the server's tags load.
		if(event instanceof TagsUpdatedEvent.ServerDataLoad) {
			OreFlowers.registerOres();
		}
	}

	/** A chunk's ores are final: count them and maybe spawn flowers over them. */
	@SubscribeEvent
	public static void onOreGen(CogOreGenEvent event) {
		IFlowerData flowers = HardLibAPI.oreFlowers;
		if(flowers.getOreList().isEmpty()) return;
		ChunkAccess chunk = event.getChunk();
		OreFlowers.oreData.putChunk(chunk,
				ChunkOreCounter.count(chunk, flowers, (pos, ore) -> flowers.trySpawnFlowerCluster(event.getRegion(), pos, ore)));
	}

	@SubscribeEvent
	public static void onBonemeal(BonemealEvent event) {
		Level world = event.getLevel();
		if(world.isClientSide()) return;
		// Dispensers have no player: they only grow ore flowers if the config allows, and much more rarely.
		boolean machine = event.getPlayer() == null;
		if(machine && !FlowersConfig.poopBonemealFlowers.get()) return;
		BlockState block = event.getState();
		boolean sand = block.is(Blocks.SAND) || block.is(Blocks.RED_SAND);
		if(!sand && !block.is(Blocks.GRASS_BLOCK)) return;
		BlockPos pos = event.getPos();
		Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> list = HardLibAPI.oreFlowers.getOreList();
		for(BlockWrapper ore : list.keySet()) {
			int count = 0;
			for(int scanLevel = 0; scanLevel < FlowersConfig.scanDepthSlices.get(); scanLevel++) {
				count += HardLibAPI.oreData.getOreData(world, pos.below(scanLevel * 8), ore);
			}
			if(count <= 0) continue;
			if(!machine && event.getPlayer() instanceof ServerPlayer player && HardLibAPI.Advancements.FOUND_ORE != null) {
				LootParams ctx = new LootParams.Builder(player.level())
						.withParameter(FoundOreTrigger.ORE_COUNT, count)
						.create(FoundOreTrigger.requiredParams);
				HardLibAPI.Advancements.FOUND_ORE.trigger(player, ctx);
			}
			// More ore, more flowers, but only logarithmically; rich deposits also get guaranteed ones.
			count = (int)Math.min(Math.round(Math.log(count)), 10);
			while(--count >= 0) {
				for(OreFlowerData data : list.get(ore).getB()) {
					if(count >= data.highConcentrationThreshold && !machine) {
						HardLibAPI.oreFlowers.doSpawnFlowerCluster(world, pos, data.flower, rand, 1, 7);
					}
					if(rand.nextBoolean() && (!machine || rand.nextInt(128) == 0)) {
						HardLibAPI.oreFlowers.doSpawnFlowerCluster(world, pos, data.flower, rand, 1, 7);
					}
				}
			}
		}
		if(sand) {
			// Sand isn't bonemealable in vanilla; here it grows dead bushes too (and uses up the bonemeal).
			event.setSuccessful(true);
			for(int count = rand.nextInt(4) + 3; --count >= 0;) {
				if(rand.nextBoolean() && (!machine || rand.nextInt(128) == 0)) {
					HardLibAPI.oreFlowers.doSpawnFlowerCluster(world, pos, Blocks.DEAD_BUSH.defaultBlockState(), rand, 1, 7);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		LevelAccessor world = event.getLevel();
		if(world == null || world.isClientSide() || !(event.getChunk() instanceof LevelChunk chunk)) return;
		if(!OreFlowers.oreData.hasChunk(chunk) && !HardLibAPI.oreFlowers.getOreList().isEmpty()) {
			// Generated before ore data existed (or its data was lost): count it now, without spawning flowers.
			OreFlowers.oreData.putChunk(chunk, ChunkOreCounter.count(chunk, HardLibAPI.oreFlowers, null));
		}
	}
}
