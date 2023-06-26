package com.draco18s.hardlib.api.blockproperties.farming;

import net.minecraft.util.StringRepresentable;

public enum LeatherStatus implements StringRepresentable {
	NONE,
	RAW,
	CURED;

	private final String name;

	private LeatherStatus() {
		this.name = toString().toLowerCase();
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
