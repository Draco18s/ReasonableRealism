package com.draco18s.industry;

import com.draco18s.industry.world.FilterDimension;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommonProxy {
	public World getFilterWorld() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(FilterDimension.DIMENSION_ID);
	}
}
