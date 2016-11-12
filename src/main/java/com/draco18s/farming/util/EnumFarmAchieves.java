package com.draco18s.farming.util;

import com.draco18s.hardlib.internal.IMetaLookup;

public enum EnumFarmAchieves implements IMetaLookup<EnumFarmAchieves> {
	KILL_WEEDS,
	CROP_ROTATION,
	THERMOMETER;

	public final int meta;
	public final String name;
	
	private EnumFarmAchieves() {
		this.name = this.toString().toLowerCase();
		this.meta = this.ordinal();
	}
	
	@Override
	public String getID() {
		return "achievement_name";
	}

	@Override
	public EnumFarmAchieves getByOrdinal(int i) {
		return EnumFarmAchieves.values()[i];
	}

	@Override
	public String getVariantName() {
		return this.name;
	}

	@Override
	public int getOrdinal() {
		return this.ordinal();
	}

}
