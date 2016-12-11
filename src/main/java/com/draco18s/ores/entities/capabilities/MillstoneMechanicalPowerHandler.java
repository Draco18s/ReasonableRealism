package com.draco18s.ores.entities.capabilities;

import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;

public class MillstoneMechanicalPowerHandler extends RawMechanicalPowerHandler {

	public MillstoneMechanicalPowerHandler() {
	}
	
	@Override
	public int minimumTorque() {
		return 650;
	}

	@Override
	public float getScaledPower(int p) {
		float f = p / 40f;
		return ((float)Math.sqrt(f)-0.1f)*1.1f;
	}
}
