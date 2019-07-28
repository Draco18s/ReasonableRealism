package com.draco18s.hardlib.api;

import com.draco18s.hardlib.api.advancement.BreakBlockTrigger;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger;
import com.draco18s.hardlib.api.advancement.FoundOreTrigger;
import com.draco18s.hardlib.api.advancement.MillstoneTrigger;
import com.draco18s.hardlib.api.advancement.WorldTimeTrigger;
import com.draco18s.hardlib.api.interfaces.IHardOreProcessing;

public class HardLibAPI {
	public static IHardOreProcessing oreMachines;
	/**
	 * Advancement triggers. Some are specific to a given mod, others are fired by HardLib.
	 * @author Draco18s
	 *
	 */
	public static class Advancements {
		
		/**
		 * Fired by Harder Ores. Json details:<br>
		 * harderores:millstone_grind {<br>
		 *   <br>
		 * }<br>
		 */
		public static MillstoneTrigger MILL_BUILT;
		/**
		 * Fired by Ore Flowers. Json details:<br>
		 * oreflowers:found_ore {<br>
		 *   <br>
		 * }<br>
		 */
		public static FoundOreTrigger FOUND_ORE;
		/**
		 * Fired by HardLib. Json details:<br>
		 * hardlib:break_block {<br>
		 * &nbsp;&nbsp; block:block (by registry name)<br>
		 * &nbsp;&nbsp;&nbsp;&nbsp; - or -<br>
		 * &nbsp;&nbsp; state:BlockState<br>
		 * }<br>
		 */
		public static BreakBlockTrigger BLOCK_BREAK;
		/**
		 * Fired by HardLib. Json details:<br>
		 * hardlib:total_world_time {<br>
		 * &nbsp;&nbsp; duration:float (in ticks)<br>
		 * }<br>
		 */
		public static WorldTimeTrigger WORLD_TIME;
		/**
		 * Fired by HardLib. Json details:<br>
		 * hardlib:distance_traveled {<br>
		 * &nbsp;&nbsp; travel_type:TravelType (enum)<br>
		 * &nbsp;&nbsp; distance:float (in blocks)<br>
		 * }<br>
		 * Available travel types:
		 *  <b>WALK</b>,
		 *  <b>WALK_ON_WATER</b>,
		 *  <b>WALK_UNDER_WATER</b>,
		 *  <b>FLY</b>,
		 *  <b>HORSE</b>,
		 *  <b>PIG</b>,
		 *  <b>BOAT</b>,
		 *  <b>RAIL</b>
		 */
		public static DistanceTraveledTrigger DISTANCE_TRAVELED;
	}
}
