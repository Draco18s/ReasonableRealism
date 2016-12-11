package com.draco18s.hardlib.api.blockproperties.farming;

import com.draco18s.hardlib.api.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum LeatherStatus implements IStringSerializable,IMetaLookup<LeatherStatus> {
	NONE,
	RAW,
	CURED;

	private final String name;

	private LeatherStatus() {
		this.name = toString().toLowerCase();
	}

	@Override
	public String getID() {
		return "leather";
	}

	@Override
	public LeatherStatus getByOrdinal(int i) {
		return LeatherStatus.values()[i];
	}

	@Override
	public String getVariantName() {
		return name;
	}

	@Override
	public int getOrdinal() {
		return this.ordinal();
	}

	@Override
	public String getName() {
		return name;
	}
}