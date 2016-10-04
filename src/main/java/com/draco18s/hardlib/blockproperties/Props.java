package com.draco18s.hardlib.blockproperties;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.item.EnumDyeColor;

public class Props {
	public static final PropertyInteger ORE_DENSITY = PropertyInteger.create("ore_density", 0, 15);
	public static final PropertyEnum<MillstoneOrientation> MILL_ORIENTATION = PropertyEnum.<MillstoneOrientation>create("mill_orientation", MillstoneOrientation.class);
	public static final PropertyEnum<AxelOrientation> AXEL_ORIENTATION = PropertyEnum.<AxelOrientation>create("axel_orientation", AxelOrientation.class);
	public static final PropertyBool FLOWER_STALK = PropertyBool.create("flower_stalk");
	public static final PropertyEnum<EnumOreFlower1> FLOWER_TYPE = PropertyEnum.<EnumOreFlower1>create("flower_type", EnumOreFlower1.class);
	public static final PropertyEnum<EnumOreFlower2> DESERT_FLOWER_TYPE = PropertyEnum.<EnumOreFlower2>create("flower_type", EnumOreFlower2.class);
}
