package com.draco18s.flowers.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.interfaces.IOreData;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

/**
 * Ore amounts per chunk and 8-block slice, kept on the chunk as a data attachment (oreflowers:ore_data), which
 * NeoForge saves with the chunk and carries over from the generating chunk to the loaded one. (Chunk save events
 * don't give the chunk's NBT since 26.x.) Written from worldgen threads, so the counts are thread safe.
 */
public class OreDataStore implements IOreData {
	public static final int SLICE_HEIGHT = 8;
	private static final int VERSION = 5;

	/** One chunk: slice (its lowest y) → ore → amount. */
	public static class ChunkOres {
		/** Slice y (as a string, for NBT's sake) → ore → amount. */
		private static final Codec<Map<String, Map<Block, Integer>>> SLICES_CODEC = Codec.unboundedMap(Codec.STRING,
				Codec.unboundedMap(BuiltInRegistries.BLOCK.byNameCodec(), Codec.INT));

		private final Map<Integer, Map<Block, Integer>> slices = new HashMap<Integer, Map<Block, Integer>>();
		/** False for a chunk whose ores haven't been counted (saved without data, or with an old version of it). */
		private final boolean counted;

		public ChunkOres() {
			this(true);
		}

		private ChunkOres(boolean counted) {
			this.counted = counted;
		}

		public synchronized int get(int sliceY, Block ore) {
			Map<Block, Integer> slice = slices.get(sliceY);
			return slice == null ? 0 : slice.getOrDefault(ore, 0);
		}

		public synchronized void put(int sliceY, Block ore, int amount) {
			slices.computeIfAbsent(sliceY, y -> new HashMap<Block, Integer>()).put(ore, Math.max(amount, 0));
		}

		synchronized boolean save(ValueOutput output) {
			if(!counted) return false;
			Map<String, Map<Block, Integer>> out = new HashMap<String, Map<Block, Integer>>();
			slices.forEach((y, ores) -> out.put(String.valueOf(y), new HashMap<Block, Integer>(ores)));
			output.putInt("version", VERSION);
			output.store("slices", SLICES_CODEC, out);
			return true;
		}

		static ChunkOres load(ValueInput input) {
			if(input.getIntOr("version", 0) != VERSION) return new ChunkOres(false);
			return input.read("slices", SLICES_CODEC).map(in -> {
				ChunkOres chunk = new ChunkOres();
				in.forEach((y, ores) -> ores.forEach((ore, amount) -> chunk.put(Integer.parseInt(y), ore, amount)));
				return chunk;
			}).orElseGet(() -> new ChunkOres(false));
		}
	}

	private static final IAttachmentSerializer<ChunkOres> SERIALIZER = new IAttachmentSerializer<ChunkOres>() {
		@Override
		public ChunkOres read(IAttachmentHolder holder, ValueInput input) {
			return ChunkOres.load(input);
		}

		@Override
		public boolean write(ChunkOres attachment, ValueOutput output) {
			return attachment.save(output);
		}
	};

	/** The attachment (registered by OreFlowers); a chunk without one hasn't been counted. */
	public static final AttachmentType<ChunkOres> ORE_DATA = AttachmentType.builder(() -> new ChunkOres(false)).serialize(SERIALIZER).build();

	public static int sliceOf(int y) {
		return y - Math.floorMod(y, SLICE_HEIGHT);
	}

	/** The chunk at pos, if it is loaded (or, in worldgen, part of the region). */
	@Nullable
	private static ChunkAccess chunk(LevelAccessor world, BlockPos pos) {
		return world.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.EMPTY, false);
	}

	@Override
	public int getOreData(LevelAccessor world, BlockPos pos, BlockWrapper ore) {
		ChunkAccess chunk = chunk(world, pos);
		ChunkOres ores = chunk == null ? null : chunk.getExistingDataOrNull(ORE_DATA);
		return ores == null ? 0 : ores.get(sliceOf(pos.getY()), ore.block);
	}

	@Override
	public void adjustOreData(LevelAccessor world, BlockPos pos, BlockWrapper ore, int amount) {
		ChunkAccess chunk = chunk(world, pos);
		ChunkOres ores = chunk == null ? null : chunk.getExistingDataOrNull(ORE_DATA);
		if(ores == null || !ores.counted) return;
		synchronized(ores) {
			int y = sliceOf(pos.getY());
			do {
				int v = ores.get(y, ore.block);
				ores.put(y, ore.block, v + amount);
				amount = Math.min(v + amount, 0);
				y -= SLICE_HEIGHT;
			} while(amount < 0 && y >= world.getMinY());
		}
		chunk.markUnsaved();
	}

	/** Replaces a chunk's counts (when its ores are generated or recounted). */
	public void putChunk(ChunkAccess chunk, ChunkOres ores) {
		chunk.setData(ORE_DATA, ores);
		chunk.markUnsaved();
	}

	/** @return false if the chunk's ores haven't been counted (or were counted by an old version). */
	public boolean hasChunk(ChunkAccess chunk) {
		ChunkOres ores = chunk.getExistingDataOrNull(ORE_DATA);
		return ores != null && ores.counted;
	}
}
