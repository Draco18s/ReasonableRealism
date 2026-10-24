package com.draco18s.farming.util;

import java.util.HashMap;

import com.draco18s.hardlib.api.interfaces.IHardCrops;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;

import net.minecraft.world.level.block.Block;

/** The climate each crop prefers, as offsets from the biome's temperature and rainfall. */
public class CropManager implements IHardCrops {
	private final HashMap<Block,CropWeatherOffsets> cropWeatherOffsets = new HashMap<Block,CropWeatherOffsets>();

	@Override
	public void putCropWeather(Block b, CropWeatherOffsets off) {
		if(b == null) {
			new RuntimeException("Attempted to set weather offsets for a null block").printStackTrace();
			return;
		}
		cropWeatherOffsets.put(b, off);
	}

	@Override
	public boolean isCropBlock(Block block) {
		return cropWeatherOffsets.containsKey(block);
	}

	@Override
	public CropWeatherOffsets getCropOffsets(Block block) {
		return cropWeatherOffsets.get(block);
	}
}
