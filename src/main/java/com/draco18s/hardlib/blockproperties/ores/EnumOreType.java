package com.draco18s.hardlib.blockproperties.ores;

import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum EnumOreType implements IMetaLookup<EnumOreType> {
	LIMONITE, /*0*/
	FLOUR,	/*1*/
	SUGAR,	/*2*/
	//placeholders entries
	BONEMEAL, /*3*/
	SEED,	 /*4*/
	PULP,	 /*5*/
	PLANT,	/*6*/
	OIL,	  /*7*/
	
	/*Flower1*/
	IRON,	 /*8*/
	GOLD,	 /*9*/
	DIAMOND,  /*10*/
	REDSTONE, /*11*/
	TIN,	  /*12*/
	COPPER,   /*13*/
	LEAD,	 /*14*/
	URANIUM,  /*15*/
	
	/*Flower 2*/
	SILVER,   /*16*/
	NICKEL,   /*17*/
	ALUMINUM, /*18*/
	PLATINUM, /*19*/ //TODO: new dist.  Thin, horizontal layers, stacked; frequently found with (nickel, iron, copper), Palladium, Rhodium, Ruthenium, Iridium, Osmium.  Possible volcanic collumn
	ZINC,	  /*20*/ //TODO: new dist
	FLUORITE, /*21*/
	CADMIUM,  /*22*/
	THORIUM,  /*23*/
	
	/*Flower 3*/
	OSMIUM;   /*24*/
	
	
	public final int meta;
	public final String name;
	public boolean set = false;
	
	private EnumOreType() {
		meta = ordinal();
		name = toString().toLowerCase();
	}

	@Override
	public String getVariantName() {
		return name;
	}
	
	@Override
	public int getOrdinal() {
		return meta;
	}
	
	@Override
	public EnumOreType getByOrdinal(int i) {
		return this.values()[i];
	}

	@Override
	public String getID() {
		return "ore_type";
	}
}
