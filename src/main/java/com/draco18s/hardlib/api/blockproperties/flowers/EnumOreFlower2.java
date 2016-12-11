package com.draco18s.hardlib.api.blockproperties.flowers;

import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.api.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum EnumOreFlower2 implements IStringSerializable,IMetaLookup<EnumOreFlower2> {
	_1MUSTARD(EnumOreType.SILVER),
	_2SHRUB_VIOLET(EnumOreType.NICKEL),
	_3AFFINE(EnumOreType.ALUMINUM),
	_4PLATINUM(EnumOreType.PLATINUM),
	_5CLOVER(EnumOreType.ZINC),
	_6CAMELLIA(EnumOreType.FLUORITE),
	_7MALVA(EnumOreType.CADMIUM),
	_8MELASTOMA(EnumOreType.THORIUM);

	private String name;
	private EnumOreType ore;
	
	EnumOreFlower2(EnumOreType oreType) {
		name = toString().toLowerCase().substring(2);
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