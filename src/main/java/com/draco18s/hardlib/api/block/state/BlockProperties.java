package com.draco18s.hardlib.api.block.state;

import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BlockProperties {
	public static final IntegerProperty ORE_DENSITY = IntegerProperty.create("ore_density", 1, 16);
	public static final EnumProperty<MillstoneOrientation> MILL_ORIENTATION = EnumProperty.<MillstoneOrientation>create("mill_orientation", MillstoneOrientation.class);
	public static final EnumProperty<AxelOrientation> AXEL_ORIENTATION = EnumProperty.<AxelOrientation>create("axel_orientation", AxelOrientation.class);
	/*public static final IntegerProperty SALT_LEVEL = IntegerProperty.create("salt_level",0,6);
	public static final EnumProperty<LeatherStatus> LEFT_LEATHER_STATE = EnumProperty.<LeatherStatus>create("leather_left", LeatherStatus.class);
	public static final EnumProperty<LeatherStatus> RIGHT_LEATHER_STATE = EnumProperty.<LeatherStatus>create("leather_right", LeatherStatus.class);
	/** Only used by Horsetail **/
	public static final BooleanProperty BLOOM_PHASE = BooleanProperty.create("bloom");
	
	public static Direction getFacingFromEntity(BlockPos pos, Player plyer) {
		return plyer.getDirection().getOpposite();
	}
}
