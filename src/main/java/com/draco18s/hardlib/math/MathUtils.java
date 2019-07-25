package com.draco18s.hardlib.math;

import java.util.Random;

public class MathUtils {
	public static float[] RandomInUnitCircle(Random rn) {
		float t = (float)Math.PI * (2*rn.nextFloat());
		float u = rn.nextFloat()+rn.nextFloat();
		float r = (u>1)?2-u:u;

		return new float[] {r*(float)Math.cos(t), r*(float)Math.sin(t)};
	}
}