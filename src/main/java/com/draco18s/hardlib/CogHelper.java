package com.draco18s.hardlib;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Installs CustomOreGen modules shipped in this jar (under cog_config/) into CustomOreGen's
 * config/customoregen/modules/mods folder, which its default config imports. The modules are rewritten
 * on every launch so they follow the installed version; customizations belong in modules/custom.
 */
public class CogHelper {
	private static List<String> extraModules = new ArrayList<String>();

	/**
	 * Call to add a module for extraction, before common setup.
	 * @param name file name under cog_config/
	 */
	public static void addCogModule(String name) {
		extraModules.add(name);
	}

	/**
	 * Called by HardLib during common setup, before CustomOreGen first loads its config.
	 */
	public static void unpackConfigs() {
		Path modsDir = FMLPaths.CONFIGDIR.get().resolve("customoregen").resolve("modules").resolve("mods");
		for (String module : extraModules) {
			unpackConfigFile(module, modsDir.resolve(module));
		}
	}

	private static void unpackConfigFile(String configName, Path destination) {
		String resourceName = "cog_config/" + configName;
		try (InputStream in = CogHelper.class.getClassLoader().getResourceAsStream(resourceName)) {
			if (in == null) {
				throw new IOException("not found in the jar");
			}
			Files.createDirectories(destination.getParent());
			Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to unpack resource '" + resourceName + "'", e);
		}
	}
}
