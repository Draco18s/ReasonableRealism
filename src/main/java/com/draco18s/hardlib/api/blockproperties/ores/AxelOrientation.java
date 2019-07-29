package com.draco18s.hardlib.api.blockproperties.ores;

import net.minecraft.util.IStringSerializable;

public enum AxelOrientation implements IStringSerializable {
	NONE("none"),
	GEARS("gears"),
	HUB("hub"),
	UP("up");

	private final String name;

	private AxelOrientation(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}
}
