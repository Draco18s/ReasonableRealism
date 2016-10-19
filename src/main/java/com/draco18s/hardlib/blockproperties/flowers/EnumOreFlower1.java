package com.draco18s.hardlib.blockproperties.flowers;

import com.draco18s.hardlib.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum EnumOreFlower1 implements IStringSerializable,IMetaLookup<EnumOreFlower1> {
	_1POORJOE(EnumOreType.IRON),
	_2HORSETAIL(EnumOreType.GOLD),
	_3VALLOZIA(EnumOreType.DIAMOND),
	_4FLAME_LILY(EnumOreType.REDSTONE),
	_5TANSY(EnumOreType.TIN),
	_6HAUMAN(EnumOreType.COPPER),
	_7LEADPLANT(EnumOreType.LEAD),
	_8RED_AMARANTH(EnumOreType.URANIUM);

	private String name;
	private EnumOreType ore;
	
	EnumOreFlower1(EnumOreType oreType) {
		name = toString().toLowerCase().substring(2);
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