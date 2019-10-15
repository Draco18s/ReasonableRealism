package com.draco18s.harderores.integration;

import com.draco18s.flowers.OreFlowers;
import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;

public class FlowerIntegration {

	public static void registerFlowerGen() {
		BlockWrapper wrap;
		OreFlowerData data;
		wrap = new BlockWrapper(HarderOres.ModBlocks.ore_hardiron, BlockProperties.ORE_DENSITY);
		data = new OreFlowerData(OreFlowers.ModBlocks.poorjoe.getDefaultState(), 8, 11, 7);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(HarderOres.ModBlocks.ore_hardiron, BlockProperties.ORE_DENSITY);
		data = new OreFlowerData(OreFlowers.ModBlocks.red_sorrel.getDefaultState(), 8, 11, 7);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
	}
}
