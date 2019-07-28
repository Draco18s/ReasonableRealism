package com.draco18s.hardlib.api.interfaces.capability;

/**
 * Used by TEs to consume mechanical power produced by the windmill
 *
 */
public interface IMechanicalPower {
	public int getRawPower();
	public void setRawPower(int p);
	public int minimumTorque();
	public float getScaledPower(int p);
}