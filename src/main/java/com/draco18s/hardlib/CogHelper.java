package com.draco18s.hardlib;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.Loader;

public class CogHelper {
	private static List<String> extraModules = new ArrayList<String>();

	/**
	 * Call to add a module for extraction
	 * @param name
	 */
	public static void addCogModule(String name) {
		extraModules.add(name);
	}

	/**
	 * Called by HardLib.
	 * @param configName
	 * @param destination
	 * @return
	 */
	public static void unpackConfigs() {
		File configPath = getConfigDir();
		File modulesDir = new File(configPath, "modules");

		File defaultModulesDir = new File(modulesDir, "mods");
		if (defaultModulesDir.exists()) {
			File[] defaultModules = defaultModulesDir.listFiles();
			for (File defaultModule : defaultModules) {
				defaultModule.delete();
			}
		} else {
			configPath.mkdir();
			modulesDir.mkdir();
			defaultModulesDir.mkdir();
		}

		for (String module : extraModules) {
			File writeFile = new File(defaultModulesDir, module);
			if(!writeFile.exists()) {
				unpackConfigFile(module, writeFile);
			}
		}
	}
	
	private static boolean unpackConfigFile(String configName, File destination) {
		String resourceName = "cog_config/" + configName;
		try {
			InputStream ex = HardLib.class.getClassLoader().getResourceAsStream(resourceName);
			BufferedOutputStream streamOut = new BufferedOutputStream(new FileOutputStream(destination));
			byte[] buffer = new byte[1024];
			boolean len = false;
			int len1;

			while ((len1 = ex.read(buffer)) >= 0)
			{
				streamOut.write(buffer, 0, len1);
			}

			ex.close();
			streamOut.close();
			return true;
		}
		catch (Exception var6) {
			throw new RuntimeException("Failed to unpack resource \'" + resourceName + "\'", var6);
		}
	}

	private static File getConfigDir() {
		return new File(Loader.instance().getConfigDir(), "CustomOreGen");
	}
}
