package com.draco18s.flowers.config;

import net.minecraftforge.fml.config.ModConfig;

public final class ConfigHelper {
	public static ModConfig clientConfig;
	public static ModConfig serverConfig;
	
	public static boolean poopBonemealFlowers;
	public static int configScanDepth;

	public static void bakeClient(final ModConfig config) {
		clientConfig = config;

		//naturalFluidTextures = ConfigHolder.CLIENT.naturalFluidTextures.get();
	}

	public static void bakeServer(final ModConfig config) {
		serverConfig = config;
		
		//poopBonemealFlowers = ConfigHolder.SERVER.poopBonemealFlowers.get();
		//configScanDepth = ConfigHolder.SERVER.configScanDepth.get();
	}

	public static void setValueAndSave(final ModConfig modConfig, final String path, final Object newValue) {
		modConfig.getConfigData().set(path, newValue);
		modConfig.save();
	}

	public static void setPoopBonemealFlowers(final boolean enabled) {
		setValueAndSave(serverConfig, "general.poopBonemealFlowers", enabled);
	}
}