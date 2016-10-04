package com.draco18s.hardlib.blockproperties;

import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum EnumOreFlower1 implements IStringSerializable,IMetaLookup<EnumOreFlower1> {
	POORJOE(EnumOreType.IRON),
	HORSETAIL(EnumOreType.GOLD),
	VALLOZIA(EnumOreType.DIAMOND),
	FLAME_LILY(EnumOreType.REDSTONE),
	TANSY(EnumOreType.TIN),
	HAUMAN(EnumOreType.COPPER),
	LEADPLANT(EnumOreType.LEAD),
	RED_AMARANTH(EnumOreType.URANIUM);

	private String name;
	private EnumOreType ore;
	
	EnumOreFlower1(EnumOreType oreType) {
		name = toString().toLowerCase();
		ore = oreType;
	}
	
	@Override
	public String getID() {
		return "flower_type";
	}

	@Override
	public EnumOreFlower1 getByOrdinal(int i) {
		return EnumOreFlower1.values()[i];
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