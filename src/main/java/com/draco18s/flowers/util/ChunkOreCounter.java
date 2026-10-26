package com.draco18s.flowers.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.interfaces.IFlowerData;
import com.draco18s.hardlib.api.internal.BlockWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;

/**
 * Counts each registered ore per 8-block slice of a chunk. A slice's amount is 2/3 its own ore plus 1/3 of the
 * slice below it (as 1.12 did), so ore just under a slice still shows a little.
 */
public class ChunkOreCounter {
	private static final int SLICES_PER_SECTION = SectionPos.SECTION_SIZE / OreDataStore.SLICE_HEIGHT;

	/**
	 * @param onOre - called for every ore block found (worldgen uses it to spawn flowers), or null
	 */
	public static OreDataStore.ChunkOres count(ChunkAccess chunk, IFlowerData flowers, @Nullable BiConsumer<BlockPos, BlockWrapper> onOre) {
		int sliceCount = chunk.getSectionsCount() * SLICES_PER_SECTION;
		int minY = chunk.getMinY();
		ChunkPos cpos = chunk.getPos();
		Map<Block, int[]> raw = new HashMap<Block, int[]>();

		for(int s = 0; s < chunk.getSectionsCount(); s++) {
			LevelChunkSection section = chunk.getSection(s);
			if(section.hasOnlyAir() || !section.getStates().maybeHas(state -> flowers.getOreWrapper(state.getBlock()) != null)) continue;
			for(int y = 0; y < SectionPos.SECTION_SIZE; y++) {
				int slice = s * SLICES_PER_SECTION + y / OreDataStore.SLICE_HEIGHT;
				for(int z = 0; z < 16; z++) {
					for(int x = 0; x < 16; x++) {
						BlockState state = section.getBlockState(x, y, z);
						BlockWrapper ore = flowers.getOreWrapper(state.getBlock());
						if(ore == null) continue;
						raw.computeIfAbsent(state.getBlock(), b -> new int[sliceCount])[slice] += ore.getOreValue(state);
						if(onOre != null) {
							onOre.accept(new BlockPos(cpos.getMinBlockX() + x, minY + s * SectionPos.SECTION_SIZE + y, cpos.getMinBlockZ() + z), ore);
						}
					}
				}
			}
		}

		OreDataStore.ChunkOres ores = new OreDataStore.ChunkOres();
		for(Map.Entry<Block, int[]> entry : raw.entrySet()) {
			int[] counts = entry.getValue();
			for(int i = 0; i < sliceCount; i++) {
				int below = i > 0 ? counts[i - 1] : 0;
				int amount = Math.round(counts[i] * (2f / 3f) + below * (1f / 3f));
				if(amount > 0) {
					ores.put(minY + i * OreDataStore.SLICE_HEIGHT, entry.getKey(), amount);
				}
			}
		}
		return ores;
	}
}
