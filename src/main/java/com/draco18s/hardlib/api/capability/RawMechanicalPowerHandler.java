package com.draco18s.hardlib.api.capability;

import com.draco18s.hardlib.api.interfaces.capability.IMechanicalPower;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class RawMechanicalPowerHandler implements IMechanicalPower, INBTSerializable<CompoundNBT> {
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
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("mechpower", powerLevel);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		powerLevel = tag.getInt("mechpower");
	}
}