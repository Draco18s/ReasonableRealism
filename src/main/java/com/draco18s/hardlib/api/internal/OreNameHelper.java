package com.draco18s.hardlib.api.internal;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.HardLib;

import org.apache.logging.log4j.util.TriConsumer;

import net.minecraft.resources.Identifier;

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
	
	public static void DoForTextureNames(TriConsumer<Identifier,Identifier,Identifier> callback) {
		for(String ore : oreNames) {
			for(String stone : stoneNames) {
				String stoneName = stone;
				String stone_ = stone+"_";
				if(stone.isBlank()) {
					stoneName = "stone";
					stone_ = "";
				}
				
				Identifier vanillaStoneTexture = mcRL("block/" + stoneName);
				Identifier vanillaOriginalOreTexture = mcRL("block/" + stone_+ore+"_ore");
				Identifier harderOreBlockName = HarderOres.getRL("ore_hard"+stone_+ore);
				callback.accept(vanillaStoneTexture, vanillaOriginalOreTexture, harderOreBlockName);
			}
		}
	}

	private static Identifier mcRL(String path) {
		return HardLib.getRL("minecraft", path);
	}
}
