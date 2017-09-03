package com.draco18s.hardlib.api.internal;

import net.minecraft.world.World;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.PeriodHelper;

public class StellarSkyIntegration {
	public static float getYearProgress(World world, float offset) {
		CelestialPeriod yearPeriod = PeriodHelper.getYearPeriod(world);
		long o = (long)(yearPeriod.getPeriodLength() * offset);
		return (float)yearPeriod.getOffset(world.getTotalWorldTime() + o, 0);
	}

	public static double getYearLength(World world) {
		return PeriodHelper.getYearPeriod(world).getPeriodLength();
	}
}
