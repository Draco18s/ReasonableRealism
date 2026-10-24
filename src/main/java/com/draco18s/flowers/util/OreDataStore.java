package com.draco18s.flowers.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.interfaces.IOreData;
import com.draco18s.hardlib.api.internal.BlockWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Ore amounts per chunk and 8-block slice, kept while the chunk is loaded and saved in the chunk's NBT.
 * Written from worldgen threads, so everything here is thread safe.
 */
public class OreDataStore implements IOreData {
	public static final int SLICE_HEIGHT = 8;
	private static final String NBT_KEY = "oreflowers:ore_data";
	private static final int VERSION = 4;

	private final Map<ResourceKey<Level>, Map<Long, ChunkOres>> data = new ConcurrentHashMap<ResourceKey<Level>, Map<Long, ChunkOres>>();
	/** Chunks that have unloaded but not saved yet (Forge posts the unload event first). */
	private final Map<ResourceKey<Level>, Set<Long>> unloading = new ConcurrentHashMap<ResourceKey<Level>, Set<Long>>();

	/** One chunk: slice (its lowest y) → ore → amount. */
	public static class ChunkOres {
		private final Map<Integer, Map<Block, Integer>> slices = new HashMap<Integer, Map<Block, Integer>>();

		public synchronized int get(int sliceY, Block ore) {
			Map<Block, Integer> slice = slices.get(sliceY);
			return slice == null ? 0 : slice.getOrDefault(ore, 0);
		}

		public synchronized void put(int sliceY, Block ore, int amount) {
			slices.computeIfAbsent(sliceY, y -> new HashMap<Block, Integer>()).put(ore, Math.max(amount, 0));
		}

		synchronized CompoundTag save() {
			ListTag list = new ListTag();
			for(Map.Entry<Integer, Map<Block, Integer>> slice : slices.entrySet()) {
				CompoundTag ores = new CompoundTag();
				slice.getValue().forEach((ore, amount) -> ores.putInt(BuiltInRegistries.BLOCK.getKey(ore).toString(), amount));
				CompoundTag snbt = new CompoundTag();
				snbt.putInt("y", slice.getKey());
				snbt.put("ores", ores);
				list.add(snbt);
			}
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("version", VERSION);
			nbt.put("slices", list);
			return nbt;
		}

		static ChunkOres load(CompoundTag nbt) {
			ChunkOres chunk = new ChunkOres();
			for(Tag t : nbt.getList("slices", Tag.TAG_COMPOUND)) {
				CompoundTag snbt = (CompoundTag)t;
				CompoundTag ores = snbt.getCompound("ores");
				for(String id : ores.getAllKeys()) {
					Block ore = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(id));
					if(ore != Blocks.AIR) chunk.put(snbt.getInt("y"), ore, ores.getInt(id));
				}
			}
			return chunk;
		}
	}

	public static int sliceOf(int y) {
		return y - Math.floorMod(y, SLICE_HEIGHT);
	}

	@Nullable
	private static ResourceKey<Level> dimension(LevelAccessor world) {
		if(world instanceof Level level) return level.dimension();
		if(world instanceof ServerLevelAccessor level) return level.getLevel().dimension();
		return null;
	}

	@Nullable
	private ChunkOres chunk(LevelAccessor world, ChunkPos pos, boolean create) {
		ResourceKey<Level> dim = dimension(world);
		if(dim == null) return null;
		Map<Long, ChunkOres> chunks = data.computeIfAbsent(dim, d -> new ConcurrentHashMap<Long, ChunkOres>());
		return create ? chunks.computeIfAbsent(pos.toLong(), p -> new ChunkOres()) : chunks.get(pos.toLong());
	}

	@Override
	public int getOreData(LevelAccessor world, BlockPos pos, BlockWrapper ore) {
		ChunkOres chunk = chunk(world, new ChunkPos(pos), false);
		return chunk == null ? 0 : chunk.get(sliceOf(pos.getY()), ore.block);
	}

	@Override
	public void adjustOreData(LevelAccessor world, BlockPos pos, BlockWrapper ore, int amount) {
		ChunkOres chunk = chunk(world, new ChunkPos(pos), true);
		if(chunk == null) return;
		synchronized(chunk) {
			int y = sliceOf(pos.getY());
			do {
				int v = chunk.get(y, ore.block);
				chunk.put(y, ore.block, v + amount);
				amount = Math.min(v + amount, 0);
				y -= SLICE_HEIGHT;
			} while(amount < 0 && y >= world.getMinBuildHeight());
		}
	}

	/** Replaces a chunk's counts (when its ores are generated or recounted). */
	public void putChunk(LevelAccessor world, ChunkPos pos, ChunkOres ores) {
		ResourceKey<Level> dim = dimension(world);
		if(dim == null) return;
		data.computeIfAbsent(dim, d -> new ConcurrentHashMap<Long, ChunkOres>()).put(pos.toLong(), ores);
	}

	public boolean hasChunk(LevelAccessor world, ChunkPos pos) {
		return chunk(world, pos, false) != null;
	}

	public void saveChunk(LevelAccessor world, ChunkPos pos, CompoundTag chunkNbt) {
		ChunkOres chunk = chunk(world, pos, false);
		if(chunk != null) chunkNbt.put(NBT_KEY, chunk.save());
		ResourceKey<Level> dim = dimension(world);
		Set<Long> pending = dim == null ? null : unloading.get(dim);
		if(pending != null && pending.remove(pos.toLong())) {
			Map<Long, ChunkOres> chunks = data.get(dim);
			if(chunks != null) chunks.remove(pos.toLong());
		}
	}

	/** @return false if the chunk was saved without ore data (or with an old version of it). */
	public boolean loadChunk(LevelAccessor world, ChunkPos pos, CompoundTag chunkNbt) {
		if(!chunkNbt.contains(NBT_KEY, Tag.TAG_COMPOUND)) return false;
		CompoundTag nbt = chunkNbt.getCompound(NBT_KEY);
		if(nbt.getInt("version") != VERSION) return false;
		putChunk(world, pos, ChunkOres.load(nbt));
		return true;
	}

	/** The chunk unloaded; if it is about to be saved, its data is dropped once that's done. */
	public void unloadChunk(LevelAccessor world, ChunkPos pos, boolean willSave) {
		ResourceKey<Level> dim = dimension(world);
		if(dim == null) return;
		if(willSave) {
			unloading.computeIfAbsent(dim, d -> ConcurrentHashMap.newKeySet()).add(pos.toLong());
		}
		else {
			Map<Long, ChunkOres> chunks = data.get(dim);
			if(chunks != null) chunks.remove(pos.toLong());
		}
	}

	/** The chunk is loaded (again): keep its data. */
	public void chunkLoaded(LevelAccessor world, ChunkPos pos) {
		ResourceKey<Level> dim = dimension(world);
		Set<Long> pending = dim == null ? null : unloading.get(dim);
		if(pending != null) pending.remove(pos.toLong());
	}

	public void unloadLevel(LevelAccessor world) {
		ResourceKey<Level> dim = dimension(world);
		if(dim != null) {
			data.remove(dim);
			unloading.remove(dim);
		}
	}
}
