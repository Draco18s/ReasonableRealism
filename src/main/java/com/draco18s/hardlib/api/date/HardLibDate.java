package com.draco18s.hardlib.api.date;

import java.util.HashMap;
import java.util.Iterator;

import com.draco18s.hardlib.api.internal.StellarSkyIntegration;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.Loader;

public class HardLibDate {
	private static HashMap<Biome,BiomeWeatherData> biomeTemps = new HashMap<Biome,BiomeWeatherData>();
	
	public static void initBiomeData() {
		Iterator<Biome> it = Biome.REGISTRY.iterator();
		BiomeWeatherData dat;
		while(it.hasNext()) {
			Biome bio = it.next();
			Biome bio2 = bio;
			float temperature = bio2.getTemperature();
			float rainfall = bio2.getRainfall();
			if(bio.isMutation()) {
				bio2 = (Biome)Biome.MUTATION_TO_BASE_ID_MAP.getByValue(Biome.getIdForBiome(bio));
			}
			if(BiomeDictionary.hasType(bio2, Type.OCEAN) || BiomeDictionary.hasType(bio2, Type.RIVER)) {
				dat = new BiomeWeatherData(temperature, rainfall, 0.667f, 1);
			}
			else if(BiomeDictionary.hasType(bio2, Type.BEACH)) {
				temperature -= 0.3f;
				dat = new BiomeWeatherData(temperature, rainfall, 0.667f, 1);
			}
			else if(BiomeDictionary.hasType(bio2, Type.NETHER)) {
				temperature += 1f;
				rainfall -= 2f;
				dat = new BiomeWeatherData(temperature, rainfall, 1, 0.25f);
			}
			else if(BiomeDictionary.hasType(bio2, Type.SAVANNA)) {
				temperature -= 0.1f;
				rainfall += 0.1f;
				dat = new BiomeWeatherData(temperature, rainfall, 0.4f, 4);
			}
			else if(BiomeDictionary.hasType(bio2, Type.SANDY)) {
				if(temperature > 1.5f) {
					temperature -= 0.8f;
				}
				rainfall -= 0.3f;
				dat = new BiomeWeatherData(temperature, rainfall, 0.4f, 2.5f);
			}
			else if(BiomeDictionary.hasType(bio2, Type.SWAMP)) {
				temperature += 0.1f;
				rainfall += 0.3f;
				dat = new BiomeWeatherData(temperature, rainfall, 0.667f, 0.5f);
			}
			if(BiomeDictionary.hasType(bio2, Type.JUNGLE) || BiomeDictionary.hasType(bio2, Type.LUSH)) {
				dat = new BiomeWeatherData(temperature, rainfall, 0.667f, 0.5f);
			}
			else if(BiomeDictionary.hasType(bio2, Type.END)) {
				temperature -= 0.5f;
				rainfall -= 2.5f;
				dat = new BiomeWeatherData(temperature, rainfall, 1.5f, 1f);
			}
			else if(BiomeDictionary.hasType(bio2, Type.COLD)) {
				if(temperature >= 0.3) {
					temperature -= 0.2f;
				}
				temperature -= 0.1f;
				dat = new BiomeWeatherData(temperature, rainfall, 0.5f, 1);
			}
			else {
				temperature -= 0.2;
				dat = new BiomeWeatherData(temperature, rainfall, 1, 1);
			}
			if(BiomeDictionary.hasType(bio2, Type.MAGICAL)) {
				if(temperature > dat.temp)
					dat.temp += 0.1f;
				else if (temperature < dat.temp)
					dat.temp -= 0.1f;
				dat.rainScale /= 5;
				dat.tempScale /= 5;
			}
			biomeTemps.put(bio, dat);
		};
	}
	
	public static float getYearProgress(World world, float offset) {
		if(Loader.isModLoaded("stellarsky")) {
			return StellarSkyIntegration.getYearProgress(world, offset);
		}
		return 0;
	}
	
	public static double getYearLength(World world) {
		if(Loader.isModLoaded("stellarsky")) {
			return StellarSkyIntegration.getYearLength(world);
		}
		return 1;
	}
	
	public static float getSeasonRain(World world, long worldTime) {
		double yearLength = getYearLength(world);
		if(yearLength <= 24000) return 0.6f;
		double m = (worldTime + (yearLength/3))/(double)yearLength * 2 * Math.PI;
		m = Math.sin(m + Math.PI/2)*0.4 + 0.2;
		return (float)m;
	}

	public static float getSeasonTemp(World world, long worldTime) {
		double yearLength = getYearLength(world);
		if(yearLength <= 24000) return 0.6f;
		double m = worldTime/(double)yearLength * 2 * Math.PI;
		m = Math.sin(m + Math.PI/2)*0.6/* - 0.2*/;
		return (float)m;
	}
	
	public static float modifySeasonTemp(Biome biome, float base) {
		BiomeWeatherData dat = biomeTemps.get(biome);
		if(dat == null) return biome.getTemperature() + base;
		return dat.temp + (base * dat.tempScale);
	}
	
	public static float modifySeasonRain(Biome biome, float base) {
		BiomeWeatherData dat = biomeTemps.get(biome);
		if(dat == null) return biome.getRainfall() + base;
		return dat.rain + (base * dat.rainScale);
	}
}
