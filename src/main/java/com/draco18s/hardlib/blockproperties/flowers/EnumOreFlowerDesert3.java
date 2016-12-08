package com.draco18s.hardlib.blockproperties.flowers;

import com.draco18s.hardlib.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum EnumOreFlowerDesert3 implements IStringSerializable,IMetaLookup<EnumOreFlowerDesert3> {
	_1PAINTBRUSH(EnumOreType.OSMIUM),
	_2QUARTZ(EnumOreType.QUARTZ);

	private String name;
	private EnumOreType ore;
	
	EnumOreFlowerDesert3(EnumOreType oreType) {
		name = toString().toLowerCase().substring(2);
		ore = oreType;
	}
	
	@Override
	public String getID() {
		return "flower_type";
	}

	@Override
	public EnumOreFlowerDesert3 getByOrdinal(int i) {
		return EnumOreFlowerDesert3.values()[i];
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