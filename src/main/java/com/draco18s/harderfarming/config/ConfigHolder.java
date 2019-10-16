package com.draco18s.harderfarming.config;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ConfigHolder {

	@Nonnull
	public static final ForgeConfigSpec CLIENT_SPEC;
	@Nonnull
	public static final ForgeConfigSpec COMMON_SPEC;
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
			COMMON_SPEC = specPair.getRight();
		}
	}
	
	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path)
				.sync()
				.autosave()
				.writingMode(WritingMode.REPLACE)
				.build();

		configData.load();
		spec.setConfig(configData);
	}
}