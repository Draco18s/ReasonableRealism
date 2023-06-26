package com.draco18s.hardlib.api.internal;

import org.apache.logging.log4j.util.TriConsumer;

import com.draco18s.harderores.HarderOres;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelProvider;

public class OreNameHelper {
	private static String[] oreNames = {
			"copper",
			"diamond",
			"gold",
			"iron",
	};
	private static String[] stoneNames = {
			"",
			"deepslate",
	};
	
	public static void DoForTextureNames(TriConsumer<ResourceLocation,ResourceLocation,ResourceLocation> callback) {
		for(String ore : oreNames) {
			for(String stone : stoneNames) {
				String stoneName = stone;
				String stone_ = stone+"_";
				if(stone.isBlank()) {
					stoneName = "stone";
					stone_ = "";
				}
				
				ResourceLocation vanillaStoneTexture = mcRL(ModelProvider.BLOCK_FOLDER + "/" + stoneName);
				ResourceLocation vanillaOriginalOreTexture = mcRL(ModelProvider.BLOCK_FOLDER + "/" + stone_+ore+"_ore");
				ResourceLocation harderOreBlockName = new ResourceLocation(HarderOres.MODID, "ore_hard"+stone_+ore);
				callback.accept(vanillaStoneTexture, vanillaOriginalOreTexture, harderOreBlockName);
			}
		}
	}

	private static ResourceLocation mcRL(String path) {
		return new ResourceLocation("minecraft", path);
	}
}
