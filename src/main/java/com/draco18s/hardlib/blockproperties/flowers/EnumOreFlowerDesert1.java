package com.draco18s.hardlib.blockproperties.flowers;

import com.draco18s.hardlib.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum EnumOreFlowerDesert1 implements IStringSerializable,IMetaLookup<EnumOreFlowerDesert1> {
	_1RED_SORREL(EnumOreType.IRON),
	_2GOLD(EnumOreType.GOLD),
	_3CHANDELIER_TREE(EnumOreType.DIAMOND),
	_4AVELOZ(EnumOreType.REDSTONE),
	_5TIN(EnumOreType.TIN),
	_6COPPER_FLOWER(EnumOreType.COPPER), //replace with Ocimum centraliafricanum
	_7SHEEPS_FESCUE(EnumOreType.LEAD),
	_8PRIMROSE(EnumOreType.URANIUM);

	private String name;
	private EnumOreType ore;
	
	EnumOreFlowerDesert1(EnumOreType oreType) {
		name = toString().toLowerCase().substring(2);
		ore = oreType;
	}
	
	@Override
	public String getID() {
		return "flower_type";
	}

	@Override
	public EnumOreFlowerDesert1 getByOrdinal(int i) {
		return EnumOreFlowerDesert1.values()[i];
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