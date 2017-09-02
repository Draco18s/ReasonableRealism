package com.draco18s.hardlib.api;

import com.draco18s.hardlib.api.advancement.BreakBlockTrigger;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger;
import com.draco18s.hardlib.api.advancement.FoundOreTrigger;
import com.draco18s.hardlib.api.advancement.MillstoneTrigger;
import com.draco18s.hardlib.api.advancement.WorldTimeTrigger;
import com.draco18s.hardlib.api.interfaces.IFlowerData;
import com.draco18s.hardlib.api.interfaces.IHardAnimals;
import com.draco18s.hardlib.api.interfaces.IHardCrops;
import com.draco18s.hardlib.api.interfaces.IHardOreProcessing;
import com.draco18s.hardlib.api.interfaces.IHardOres;
import com.draco18s.hardlib.api.interfaces.IOreData;
import com.draco18s.hardlib.api.recipes.RecipeToolMold;

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
	 * Reference for the {@link com.draco18s.industry.item.ItemCastingMold ItemCastingMold}<br>
	 * Set during the FMLPreInitializationEvent (preInit) phase.  Check for null before using.<br>
	 * Casting Mold models can be registered by calling {@link com.draco18s.hardlib.api.recipes.RecipeToolMold#addMoldItem(com.draco18s.hardlib.api.recipes.RecipeToolMold.RecipeSubItem) RecipeToolMold#addMoldItem(RecipeSubItem)}
	 */
	public static Item itemMold;
	
	/**
	 * Advancement triggers. Some are specific to a given mod, others are fired by HardLib.
	 * @author Draco18s
	 *
	 */
	public static class Advancements {
		/**
		 * Fired by Harder Ores<br>
		 * total_world_time {<br>
		 *   <br>
		 * }<br>
		 */
		public static MillstoneTrigger MILL_BUILT;
		/**
		 * Fired by Ore Flowers<br>
		 * found_ore {<br>
		 *   <br>
		 * }<br>
		 */
		public static FoundOreTrigger FOUND_ORE;
		/**
		 * Fired by HardLib<br>
		 * break_block {<br>
		 * &nbsp;&nbsp; block:block (by registry name)<br>
		 * &nbsp;&nbsp; state:IBlockState<br>
		 * }<br>
		 */
		public static BreakBlockTrigger BLOCK_BREAK;
		/**
		 * Fired by HardLib<br>
		 * total_world_time {<br>
		 * &nbsp;&nbsp; duration:float (in ticks)<br>
		 * }<br>
		 */
		public static WorldTimeTrigger WORLD_TIME;
		/**
		 * Fired by HardLib<br>
		 * distance_traveled {<br>
		 * &nbsp;&nbsp; travel_type:TravelType (enum)<br>
		 * &nbsp;&nbsp; distance:float (in blocks)<br>
		 * }<br>
		 * Available travel types:
		 *  <b>WALK</b>,
		 *  <b>FLY</b>,
		 *  <b>HORSE</b>,
		 *  <b>PIG</b>,
		 *  <b>BOAT</b>,
		 *  <b>RAIL</b>
		 */
		public static DistanceTraveledTrigger DISTANCE_TRAVELED;
	}
}
