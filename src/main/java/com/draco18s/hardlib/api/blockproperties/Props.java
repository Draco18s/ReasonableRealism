package com.draco18s.hardlib.api.blockproperties;

import com.draco18s.hardlib.api.blockproperties.farming.LeatherStatus;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlower1;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlower2;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlower3;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlowerDesert1;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlowerDesert2;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlowerDesert3;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

public class Props {
	public static final PropertyInteger ORE_DENSITY = PropertyInteger.create("ore_density", 1, 16);
	public static final PropertyEnum<MillstoneOrientation> MILL_ORIENTATION = PropertyEnum.<MillstoneOrientation>create("mill_orientation", MillstoneOrientation.class);
	public static final PropertyEnum<AxelOrientation> AXEL_ORIENTATION = PropertyEnum.<AxelOrientation>create("axel_orientation", AxelOrientation.class);
	public static final PropertyBool FLOWER_STALK = PropertyBool.create("flower_stalk");
	public static final PropertyEnum<EnumOreFlower1> FLOWER_TYPE = PropertyEnum.<EnumOreFlower1>create("flower_type", EnumOreFlower1.class);
	public static final PropertyEnum<EnumOreFlower2> FLOWER_TYPE2 = PropertyEnum.<EnumOreFlower2>create("flower_type", EnumOreFlower2.class);
	public static final PropertyEnum<EnumOreFlower3> FLOWER_TYPE3 = PropertyEnum.<EnumOreFlower3>create("flower_type", EnumOreFlower3.class);
	public static final PropertyEnum<EnumOreFlowerDesert1> DESERT_FLOWER_TYPE = PropertyEnum.<EnumOreFlowerDesert1>create("flower_type", EnumOreFlowerDesert1.class);
	public static final PropertyEnum<EnumOreFlowerDesert2> DESERT_FLOWER_TYPE2 = PropertyEnum.<EnumOreFlowerDesert2>create("flower_type", EnumOreFlowerDesert2.class);
	public static final PropertyEnum<EnumOreFlowerDesert3> DESERT_FLOWER_TYPE3 = PropertyEnum.<EnumOreFlowerDesert3>create("flower_type", EnumOreFlowerDesert3.class);
	public static final PropertyInteger SALT_LEVEL = PropertyInteger.create("salt_level",0,6);
	public static final PropertyEnum<LeatherStatus> LEFT_LEATHER_STATE = PropertyEnum.<LeatherStatus>create("leather_left", LeatherStatus.class);
	public static final PropertyEnum<LeatherStatus> RIGHT_LEATHER_STATE = PropertyEnum.<LeatherStatus>create("leather_right", LeatherStatus.class);
	public static final PropertyBool FOUNDRY_LIT = PropertyBool.create("lit");
	public static final PropertyBool HAS_2D_ITEM = PropertyBool.create("item");
}
