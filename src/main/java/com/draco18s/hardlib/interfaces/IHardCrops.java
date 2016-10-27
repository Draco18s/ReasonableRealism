package com.draco18s.hardlib.interfaces;

import com.draco18s.hardlib.internal.CropWeatherOffsets;

import net.minecraft.block.Block;

public interface IHardCrops {
	public void putCropWeather(Block b, CropWeatherOffsets off);
	public boolean isCropBlock(Block block);
	public CropWeatherOffsets getCropOffsets(Block block);
}
