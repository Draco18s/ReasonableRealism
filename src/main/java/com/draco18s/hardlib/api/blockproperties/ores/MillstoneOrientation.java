package com.draco18s.hardlib.api.blockproperties.ores;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;

public enum MillstoneOrientation implements StringRepresentable
{
	NONE("none", false, false, 				BlockPos.ZERO),
	CENTER("center", false, true, 			BlockPos.ZERO),
	NORTH("north", true, false, 			BlockPos.ZERO.south()),
	SOUTH("south", true, false, 			BlockPos.ZERO.north()),
	EAST("east", true, false, 				BlockPos.ZERO.west()),
	WEST("west", true, false, 				BlockPos.ZERO.east()),
	SOUTH_EAST("south_east", false, false, 	BlockPos.ZERO.north().west()),
	SOUTH_WEST("south_west", false, false, 	BlockPos.ZERO.north().east()),
	NORTH_WEST("north_west", false, false, 	BlockPos.ZERO.south().east()),
	NORTH_EAST("north_east", false, false, 	BlockPos.ZERO.south().west());

	private final String name;
	public final boolean canAcceptInput;
	public final boolean canAcceptOutput;
	public final BlockPos offset;

	private MillstoneOrientation(String name, boolean input, boolean output, BlockPos toCenter) {
		this.name = name;
		this.canAcceptInput = input;
		this.canAcceptOutput = output;
		this.offset = toCenter;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}