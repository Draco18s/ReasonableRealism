package com.draco18s.harderfarming.config;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ConfigHolder {

	@Nonnull
	public static final ForgeConfigSpec CLIENT_SPEC;
	@Nonnull
	public static final ForgeConfigSpec SERVER_SPEC;
	@Nonnull
	static final ClientConfig CLIENT;
	@Nonnull
	static final ServerConfig SERVER;
	static {
		{
			final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
			CLIENT = specPair.getLeft();
			CLIENT_SPEC = specPair.getRight();
		}
		{
			final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
			SERVER = specPair.getLeft();
			SERVER_SPEC = specPair.getRight();
		}
	}
}