package com.draco18s.flowers.config;

import javax.annotation.Nonnull;

import com.draco18s.flowers.OreFlowers;

import net.minecraftforge.common.ForgeConfigSpec;

final class ServerConfig {
	@Nonnull
	final ForgeConfigSpec.BooleanValue poopBonemealFlowers;
	final ForgeConfigSpec.ConfigValue<Integer> configScanDepth;
	
	ServerConfig(@Nonnull final ForgeConfigSpec.Builder builder) {
		builder.push("general");
		configScanDepth = builder
				.comment("How deep bonemeal can scan")
				.translation(OreFlowers.MODID + ".config.configScanDepth")
				.define("configScanDepth", 3);
		poopBonemealFlowers = builder
				.comment("If ore flowers should spawn from poop-type bonemeal actions")
				.translation(OreFlowers.MODID + ".config.poopBonemealFlowers")
				.define("poopBonemealFlowers", true);
		builder.pop();
	}
}