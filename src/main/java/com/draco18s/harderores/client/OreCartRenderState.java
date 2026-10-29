package com.draco18s.harderores.client;

import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.core.Direction;

public class OreCartRenderState extends MinecartRenderState {
	/** The cart's comparator signal (0-15). */
	public int fullness;
	/** The side of the track the chute points to. */
	public Direction dumpSide = Direction.NORTH;
}
