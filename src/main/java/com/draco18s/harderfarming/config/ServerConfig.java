package com.draco18s.harderfarming.config;

import javax.annotation.Nonnull;

import com.draco18s.harderfarming.HarderFarming;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
	@Nonnull
	final ForgeConfigSpec.BooleanValue doRawLeather;
	
	public ServerConfig(@Nonnull final ForgeConfigSpec.Builder builder) {
		builder.push("general");
		doRawLeather = builder
				.comment("If leather drops are instead rawhide")
				.translation(HarderFarming.MODID + ".config.doRawLeather")
				.define("doRawLeather", true);
		builder.pop();
	}
}
