package com.draco18s.hardlib.api.interfaces;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Used by TEs to consume mechanical power produced by the windmill
 *
 */
@AutoRegisterCapability()
public interface IMechanicalPower {
	public static Capability<IMechanicalPower> MECHANICAL_POWER_CAPABILITY = null;
	public int getRawPower();
	public void setRawPower(int p);
	public int minimumTorque();
	public float getScaledPower(int p);
}