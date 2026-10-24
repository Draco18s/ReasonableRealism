package com.draco18s.hardlib.api.interfaces;

import com.draco18s.hardlib.api.internal.BlockWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

/**
 * How much of each registered ore lies in each 8-block-tall slice of a chunk, counted when the chunk's ores were
 * generated and saved with the chunk.
 */
public interface IOreData {
	/**
	 * The ore amount in the slice holding pos (a block position).
	 */
	public int getOreData(LevelAccessor world, BlockPos pos, BlockWrapper ore);

	/**
	 * Changes the ore amount in the slice holding pos (a block position). Removing more than the slice holds takes
	 * the rest from the slices below it.
	 */
	public void adjustOreData(LevelAccessor world, BlockPos pos, BlockWrapper ore, int amount);
}
