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

import CustomOreGen.Util.CogOreGenEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = OreFlowers.MODID)
public class FlowerEventHandler {
	private static final RandomSource rand = RandomSource.create();

	@SubscribeEvent
	public static void onTagsUpdated(TagsUpdatedEvent event) {
		// Ores are registered by tag, so the list is (re)built whenever the server's tags load.
		if(event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
			OreFlowers.registerOres();
		}
	}

	/** A chunk's ores are final: count them and maybe spawn flowers over them. */
	@SubscribeEvent
	public static void onOreGen(CogOreGenEvent event) {
		IFlowerData flowers = HardLibAPI.oreFlowers;
		if(flowers.getOreList().isEmpty()) return;
		ChunkAccess chunk = event.getChunk();
		OreFlowers.oreData.putChunk(event.getRegion(), chunk.getPos(),
				ChunkOreCounter.count(chunk, flowers, (pos, ore) -> flowers.trySpawnFlowerCluster(event.getRegion(), pos, ore)));
	}

	@SubscribeEvent
	public static void onBonemeal(BonemealEvent event) {
		Level world = event.getLevel();
		if(world.isClientSide) return;
		// Dispensers have no player: they only grow ore flowers if the config allows, and much more rarely.
		boolean machine = event.getEntity() == null;
		if(machine && !FlowersConfig.poopBonemealFlowers.get()) return;
		BlockState block = event.getBlock();
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
			if(!machine && event.getEntity() instanceof ServerPlayer player && HardLibAPI.Advancements.FOUND_ORE != null) {
				LootParams ctx = new LootParams.Builder(player.serverLevel())
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
			event.setResult(Result.ALLOW);
			for(int count = rand.nextInt(4) + 3; --count >= 0;) {
				if(rand.nextBoolean() && (!machine || rand.nextInt(128) == 0)) {
					HardLibAPI.oreFlowers.doSpawnFlowerCluster(world, pos, Blocks.DEAD_BUSH.defaultBlockState(), rand, 1, 7);
				}
			}
		}
	}

	/** Only chunks being loaded as full chunks know their level here; the rest are recounted in onChunkLoad. */
	@SubscribeEvent
	public static void onChunkDataLoad(ChunkDataEvent.Load event) {
		LevelAccessor world = event.getLevel();
		if(world == null || world.isClientSide()) return;
		OreFlowers.oreData.loadChunk(world, event.getChunk().getPos(), event.getData());
	}

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		LevelAccessor world = event.getLevel();
		if(world == null || world.isClientSide() || !(event.getChunk() instanceof LevelChunk chunk)) return;
		OreFlowers.oreData.chunkLoaded(world, chunk.getPos());
		if(!OreFlowers.oreData.hasChunk(world, chunk.getPos()) && !HardLibAPI.oreFlowers.getOreList().isEmpty()) {
			// Generated before ore data existed (or its data was lost): count it now, without spawning flowers.
			OreFlowers.oreData.putChunk(world, chunk.getPos(), ChunkOreCounter.count(chunk, HardLibAPI.oreFlowers, null));
		}
	}

	@SubscribeEvent
	public static void onChunkSave(ChunkDataEvent.Save event) {
		LevelAccessor world = event.getLevel();
		if(world == null || world.isClientSide()) return;
		OreFlowers.oreData.saveChunk(world, event.getChunk().getPos(), event.getData());
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		LevelAccessor world = event.getLevel();
		if(world == null || world.isClientSide() || !(event.getChunk() instanceof LevelChunk)) return;
		// Forge posts this just before saving the chunk (if it has changes), and the save still needs the data.
		OreFlowers.oreData.unloadChunk(world, event.getChunk().getPos(), event.getChunk().isUnsaved());
	}

	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		if(!event.getLevel().isClientSide()) {
			OreFlowers.oreData.unloadLevel(event.getLevel());
		}
	}
}
