package com.draco18s.hardlib.api.blockproperties.farming;

import net.minecraft.util.IStringSerializable;

public enum LeatherStatus implements IStringSerializable {
	NONE,
	RAW,
	CURED;

	private final String name;

	private LeatherStatus() {
		this.name = toString().toLowerCase();
	}

	@Override
	public String getName() {
		return name;
	}
}
