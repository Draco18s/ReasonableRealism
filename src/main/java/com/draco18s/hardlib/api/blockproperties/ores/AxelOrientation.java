package com.draco18s.hardlib.api.blockproperties.ores;

import com.draco18s.hardlib.api.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum AxelOrientation implements IStringSerializable, IMetaLookup<AxelOrientation> {
	NONE(0, "none"),
	GEARS(1, "gears"),
	HUB(2, "hub"),
	UP(3, "up");

	private final int meta;
	private final String name;

	private AxelOrientation(int meta, String name) {
		this.meta = meta;
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getID() {
		return "axel_orientation";
	}

	@Override
	public AxelOrientation getByOrdinal(int i) {
		return AxelOrientation.values()[i];
	}

	@Override
	public String getVariantName() {
		return this.name;
	}

	@Override
	public int getOrdinal() {
		return this.meta;
	}
}