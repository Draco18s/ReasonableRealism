package com.draco18s.hardlib.api.blockproperties.flowers;

import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.api.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum EnumOreFlowerDesert2 implements IStringSerializable,IMetaLookup<EnumOreFlowerDesert2> {
	_1RAPESEED(EnumOreType.SILVER),//rapeseed
	_2MILKWORT(EnumOreType.NICKEL),//Milkwort Jewelflower
	_3ALUMINUM(EnumOreType.ALUMINUM),
	_4MADWORT(EnumOreType.PLATINUM),//Alyssum desertorum
	_5ZILLA(EnumOreType.ZINC),//Zilla spinosa
	_6FLUORITE(EnumOreType.FLUORITE),
	_7MARIGOLD(EnumOreType.CADMIUM),//Tagetes erecta
	_8THORIUM(EnumOreType.THORIUM);

	private String name;
	private EnumOreType ore;
	
	EnumOreFlowerDesert2(EnumOreType oreType) {
		name = toString().toLowerCase().substring(2);
		ore = oreType;
	}
	
	@Override
	public String getID() {
		return "flower_type";
	}

	@Override
	public EnumOreFlowerDesert2 getByOrdinal(int i) {
		return EnumOreFlowerDesert2.values()[i];
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