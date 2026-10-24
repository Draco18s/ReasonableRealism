package com.draco18s.hardlib.math;

import net.minecraft.util.RandomSource;

public class MathUtils {
	/** A random point in the unit circle, {x, y}. */
	public static float[] RandomInUnitCircle(RandomSource rn) {
		float t = (float)Math.PI * (2*rn.nextFloat());
		float u = rn.nextFloat()+rn.nextFloat();
		float r = (u>1)?2-u:u;

		return new float[] {r*(float)Math.cos(t), r*(float)Math.sin(t)};
	}
}