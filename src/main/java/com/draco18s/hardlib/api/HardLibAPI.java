package com.draco18s.hardlib.api;

import com.draco18s.hardlib.api.interfaces.IFlowerData;
import com.draco18s.hardlib.api.interfaces.IHardAnimals;
import com.draco18s.hardlib.api.interfaces.IHardCrops;
import com.draco18s.hardlib.api.interfaces.IHardOreProcessing;
import com.draco18s.hardlib.api.interfaces.IHardOres;
import com.draco18s.hardlib.api.interfaces.IOreData;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HardLibAPI {
	public static IHardOreProcessing oreMachines;
	public static IFlowerData oreFlowers;
	public static IOreData oreData;
	public static IHardOres hardOres;
	public static IHardCrops hardCrops;
	public static IHardAnimals animalManager;
	/**
	 * Reference for the {@link com.draco18s.industry.item.ItemCastingMold}<br>
	 * Set during the FMLPreInitializationEvent (preInit) phase.  Check for null before using.
	 */
	public static Item itemMold;
}
