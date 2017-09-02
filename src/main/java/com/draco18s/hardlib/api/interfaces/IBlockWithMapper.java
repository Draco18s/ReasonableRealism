package com.draco18s.hardlib.api.interfaces;

import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public interface IBlockWithMapper {
	/**
	 * Used to override the StateMapperBase with a custom mapper
	 * @return
	 */
	public StateMapperBase getStateMapper();
}
