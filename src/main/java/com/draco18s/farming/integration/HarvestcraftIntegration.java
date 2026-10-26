package com.draco18s.farming.integration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.draco18s.farming.HarderFarming;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

/**
 * Climate preferences for Pam's HarvestCraft crops (1.14's table; the season offsets are kept but unused). Crops are
 * matched by name in HarvestCraft's namespaces rather than by exact ID, since HarvestCraft 2 renamed them
 * ("harvestcraft:pamcornCrop" in 1.12, "pamhc2crops:corncrop"-style later). Nothing happens without HarvestCraft.
 */
public class HarvestcraftIntegration {
	private static final String[] NAMESPACES = {"pamhc2crops", "harvestcraft"};

	public static void registerCrops() {
		Map<String, CropWeatherOffsets> offsets = new HashMap<String, CropWeatherOffsets>();
		crop(offsets, "blackberry", -0.3f, 0.5f, 0, 0);
		crop(offsets, "blueberry", 0.0f, 0.0f, 0.25f, 0);
		crop(offsets, "candleberry", -0.4f, -0.2f, 0, 0);
		crop(offsets, "raspberry", 0.2f, 0.0f, 0, 0);
		crop(offsets, "strawberry", -0.3f, -0.2f, 0, 0);
		crop(offsets, "cactusfruit", -0.6f, 0.6f, 0, 0);
		crop(offsets, "asparagus", 0.0f, 0.4f, 0, 0);
		crop(offsets, "barley", 0.4f, 0.0f, 0, -0.25f);
		crop(offsets, "oats", 0.4f, -0.6f, 0, 0);
		crop(offsets, "rye", 0.4f, 0.2f, 0, 0);
		crop(offsets, "corn", 0.0f, 0.0f, -1f/6f, -1f/6f);
		crop(offsets, "bambooshoot", -0.4f, -0.4f, 0, 0);
		crop(offsets, "cantaloupe", -0.4f, 0.1f, 0, 0);
		crop(offsets, "cucumber", -0.1f, 0.0f, 0, 0);
		crop(offsets, "wintersquash", 0.8f, 0.2f, 0, 0);
		crop(offsets, "zucchini", 0.1f, -0.2f, 0, 0);
		crop(offsets, "beet", 0.3f, -0.3f, 0, 0);
		crop(offsets, "onion", -0.2f, 0.0f, 0.5f, 0.5f);
		crop(offsets, "parsnip", 0.2f, 0.0f, 0.25f, 0.25f);
		crop(offsets, "peanut", -0.6f, -0.2f, 0, 0);
		crop(offsets, "radish", 0.6f, 0.0f, 0, -0.25f);
		crop(offsets, "rutabaga", 0.0f, -0.2f, 0.5f, 0);
		crop(offsets, "sweetpotato", -0.5f, -0.5f, 0, 0);
		crop(offsets, "turnip", 0.2f, 0.3f, 0, 0);
		crop(offsets, "rhubarb", 0.25f, -0.2f, 0, 0);
		crop(offsets, "celery", 0.4f, -0.2f, 0, 0);
		crop(offsets, "garlic", 0.2f, -0.1f, 0, 0);
		crop(offsets, "ginger", -0.2f, -0.1f, 0, 0);
		crop(offsets, "spiceleaf", -0.2f, -0.2f, 0, 0);
		crop(offsets, "tealeaf", -0.2f, -0.3f, -1f/12f, 0);
		crop(offsets, "coffeebean", -0.4f, -0.4f, 0, 0);
		crop(offsets, "mustardseeds", -0.6f, -0.2f, 0, 0);
		crop(offsets, "broccoli", 0.2f, -0.2f, 0, -0.25f);
		crop(offsets, "cauliflower", 0.2f, -0.1f, 0, -1f/3f);
		crop(offsets, "leek", 0.3f, 0.0f, 1f/6f, 0);
		crop(offsets, "lettuce", 0.2f, 0.2f, 0, -1f/3f);
		crop(offsets, "scallion", 0.2f, -0.1f, 0, 0);
		crop(offsets, "artichoke", -0.2f, 0.5f, 0, 0);
		crop(offsets, "brusselsprout", 0.3f, 0.0f, -1f/6f, -1f/6f);
		crop(offsets, "cabbage", 0.9f, -0.2f, 0, 0);
		crop(offsets, "spinach", 0.7f, -0.2f, 0, 0);
		crop(offsets, "whitemushroom", -0.3f, -0.3f, 0, 0);
		crop(offsets, "bean", 0.0f, 0.0f, 0, 0);
		crop(offsets, "soybean", 0.0f, -0.25f, 0, 0);
		crop(offsets, "bellpepper", -0.3f, -0.1f, 0, 0);
		crop(offsets, "chilipepper", -0.5f, 0.0f, 0, 0);
		crop(offsets, "eggplant", -0.4f, 0.3f, 0, 0);
		crop(offsets, "okra", -0.2f, 0.0f, 0, 0);
		crop(offsets, "peas", 0.2f, 0.3f, 0, 0);
		crop(offsets, "tomato", -0.4f, 0.1f, 0, 0);
		crop(offsets, "cotton", -0.6f, 0.3f, 0, 0);
		crop(offsets, "pineapple", -0.6f, 0.6f, 0, 0);
		crop(offsets, "grape", -0.3f, -0.1f, 0, 0);
		crop(offsets, "kiwi", 0.3f, 0.0f, 0, 0);
		crop(offsets, "cranberry", 0.0f, -0.5f, 0, 0);
		crop(offsets, "rice", -0.2f, -0.8f, 0, 0);
		crop(offsets, "seaweed", 0.2f, 0.0f, 0, 0);
		int matched = 0;
		for(Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
			ResourceLocation id = entry.getKey().location();
			if(!isHarvestcraft(id.getNamespace())) continue;
			CropWeatherOffsets off = offsets.get(cropName(id.getPath()));
			if(off != null) {
				HardLibAPI.hardCrops.putCropWeather(entry.getValue(), off);
				matched++;
			}
		}
		if(matched > 0) {
			HardLib.LOGGER.info(HarderFarming.MODID + ": climate data added for " + matched + " HarvestCraft crops");
		}
	}

	private static void crop(Map<String, CropWeatherOffsets> offsets, String name, float temp, float rain, float tempSeason, float rainSeason) {
		offsets.put(name, new CropWeatherOffsets(temp, rain, tempSeason, rainSeason));
	}

	private static boolean isHarvestcraft(String namespace) {
		for(String ns : NAMESPACES) {
			if(ns.equals(namespace)) return true;
		}
		return false;
	}

	/** "pamcornCrop", "corncrop" and "corn_crop" are all "corn". */
	private static String cropName(String path) {
		String name = path.toLowerCase(Locale.ROOT);
		if(name.startsWith("pam")) name = name.substring(3);
		if(name.endsWith("_crop")) name = name.substring(0, name.length() - 5);
		else if(name.endsWith("crop")) name = name.substring(0, name.length() - 4);
		return name;
	}
}
