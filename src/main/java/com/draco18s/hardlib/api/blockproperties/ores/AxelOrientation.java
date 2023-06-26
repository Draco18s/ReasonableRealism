package com.draco18s.hardlib.api.blockproperties.ores;

import net.minecraft.util.StringRepresentable;

public enum AxelOrientation implements StringRepresentable {
	NONE("none"),
	GEARS("gears"),
	HUB("hub"),
	UP("up");

	private final String name;

	private AxelOrientation(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
