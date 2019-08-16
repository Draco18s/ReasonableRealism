package com.draco18s.industry.proxy;

import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.world.World;

public class ServerProxy implements IProxy {
	public World getFilterWorld(World world) {
		//SidedProvider.STARTUPQUERY.get();
		return world.getServer().getWorld(ExpandedIndustry.ModDimensionType.FILTER_DIMENSION);
	}
}
