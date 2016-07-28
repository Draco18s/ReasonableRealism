package com.draco18s.hardlib.interfaces;

public interface IMechanicalPower {
	public int getRawPower();
	public void setRawPower(int p);
	public int minimumTorque();
	public float getScaledPower(int p);
}
