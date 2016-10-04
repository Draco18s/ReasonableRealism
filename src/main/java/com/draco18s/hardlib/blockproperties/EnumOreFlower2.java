package com.draco18s.hardlib.blockproperties;

import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum EnumOreFlower2 implements IStringSerializable,IMetaLookup<EnumOreFlower2> {
	RED_SORREL(EnumOreType.IRON),
	_GOLD(EnumOreType.GOLD),
	CHANDELIER_TREE(EnumOreType.DIAMOND),
	_REDSTONE(EnumOreType.REDSTONE),
	_TIN(EnumOreType.TIN),
	ALPINE_CATCHFLY(EnumOreType.COPPER),
	SHEEPS_FESCUE(EnumOreType.LEAD),
	PRIMROSE(EnumOreType.URANIUM);

	private String name;
	private EnumOreType ore;
	
	EnumOreFlower2(EnumOreType oreType) {
		name = toString().toLowerCase();
		ore = oreType;
	}
	
	@Override
	public String getID() {
		return "flower_type";
	}

	@Override
	public EnumOreFlower2 getByOrdinal(int i) {
		return EnumOreFlower2.values()[i];
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