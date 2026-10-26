package com.draco18s.hardlib.api.interfaces;


import com.draco18s.hardlib.HardLib;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;

/**
 * Used by TEs to consume mechanical power produced by the windmill
 *
 */
public interface IMechanicalPower {
	/** Machines expose it by side; the mods register their block entities for it (RegisterCapabilitiesEvent). */
	public static final BlockCapability<IMechanicalPower, Direction> MECHANICAL_POWER_CAPABILITY = BlockCapability.createSided(HardLib.getRL("mechanical_power"), IMechanicalPower.class);
	public int getRawPower();
	public void setRawPower(int p);
	public int minimumTorque();
	public float getScaledPower(int p);
}