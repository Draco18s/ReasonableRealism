package com.draco18s.hardlib.api.capability;

import com.draco18s.hardlib.api.interfaces.IMechanicalPower;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class RawMechanicalPowerHandler implements IMechanicalPower, ValueIOSerializable {
	protected int powerLevel;

	public RawMechanicalPowerHandler() {

	}

	@Override
	public void setRawPower(int p) {
		powerLevel = p;
	}

	@Override
	public int getRawPower() {
		return powerLevel;
	}

	@Override
	public int minimumTorque() {
		return 0;
	}

	@Override
	public float getScaledPower(int p) {
		return p;
	}

	@Override
	public void serialize(ValueOutput output) {
		output.putInt("mechpower", powerLevel);
	}

	@Override
	public void deserialize(ValueInput input) {
		powerLevel = input.getIntOr("mechpower", 0);
	}
}