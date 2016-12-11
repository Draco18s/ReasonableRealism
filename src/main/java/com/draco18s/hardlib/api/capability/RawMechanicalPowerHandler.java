package com.draco18s.hardlib.api.capability;

import com.draco18s.hardlib.api.interfaces.IMechanicalPower;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class RawMechanicalPowerHandler implements IMechanicalPower, INBTSerializable<NBTTagCompound> {
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
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("mechpower", powerLevel);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		powerLevel = tag.getInteger("mechpower");
	}
}
