package com.draco18s.hardlib.interfaces;

/**
 * Used by TEs to consume mechanical power produced by the windmill
 * @author Major
 *
 */
public interface IMechanicalPower {
	public int getRawPower();
	public void setRawPower(int p);
	public int minimumTorque();
	public float getScaledPower(int p);
}
