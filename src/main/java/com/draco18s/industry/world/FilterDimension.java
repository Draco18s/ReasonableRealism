package com.draco18s.industry.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class FilterDimension {
	public static final int DIMENSION_ID = Integer.MIN_VALUE+1;//DimensionManager.getNextFreeDimId();
	public static final String DIM_NAME = "Filter Dimension";
	public static final DimensionType FILTER_DIMENSION = DimensionType.register("FILTER", "_filter", DIMENSION_ID, WorldProviderVoid.class, false);
			
	public static void mainRegistry()
	{
		DimensionManager.registerDimension(DIMENSION_ID, FilterDimension.FILTER_DIMENSION);
	}
	
	public static boolean isTentDimension(World world)
	{
		return isTentDimension(world.provider.getDimension());
	}
	
	public static boolean isTentDimension(int id)
	{
		return id == FilterDimension.DIMENSION_ID;
	}
}
