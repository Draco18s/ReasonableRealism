package com.draco18s.hardlib.blockproperties.flowers;

import com.draco18s.hardlib.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum EnumOreFlower3 implements IStringSerializable,IMetaLookup<EnumOreFlower3> {
	_1ARROWHEAD(EnumOreType.OSMIUM);

	private String name;
	private EnumOreType ore;
	
	EnumOreFlower3(EnumOreType oreType) {
		name = toString().toLowerCase().substring(2);
		ore = oreType;
	}
	
	@Override
	public String getID() {
		return "flower_type";
	}

	@Override
	public EnumOreFlower3 getByOrdinal(int i) {
		return EnumOreFlower3.values()[i];
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

	public EnumOreType getOreType() {
		return this.ore;
	}
}