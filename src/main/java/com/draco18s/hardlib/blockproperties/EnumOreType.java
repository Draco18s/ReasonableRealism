package com.draco18s.hardlib.blockproperties;

import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.util.IStringSerializable;

public enum EnumOreType implements IMetaLookup<EnumOreType> {
	LIMONITE(""), /*0*/
	FLOUR(""),    /*1*/
	SUGAR(""),    /*2*/
	//placeholders entries
	BONEMEAL(""), /*3*/
	SEED(""),     /*4*/
	PULP(""),     /*5*/
	PLANT(""),    /*6*/
	OIL(""),      /*7*/
	
	/*Flower1*/
	IRON("poorjoe"),     	/*8*/
	GOLD("horsetail"),     	/*9*/
	DIAMOND("vallozia"),  	/*10*/
	REDSTONE("flame_lily"), /*11*/
	TIN("tansy"),      		/*12*/
	COPPER("hauman"),   	/*13*/
	LEAD("leadplant"),     	/*14*/
	URANIUM("primrose"),  	/*15*/
	
	/*Flower 2*/
	SILVER(""),   /*16*/
	NICKEL(""),   /*17*/
	ALUMINUM(""), /*18*/
	PLATINUM(""), /*19*/
	ZINC(""),     /*20*/
	FLUORITE(""), /*21*/
	CADMIUM(""),  /*22*/
	THORIUM(""),  /*23*/
	
	/*Flower 3*/
	OSMIUM("");   /*24*/
	
	
	public int meta;
	public String name;
	public String flowerName;
	
	private EnumOreType(String flower) {
		meta = ordinal();
		name = toString().toLowerCase();
		flowerName = flower;
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
	
	public String getFlowerName() {
		return flowerName;
	}
}
