package com.draco18s.hardlib.api;

import com.draco18s.hardlib.api.advancement.BreakBlockTrigger;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger;
import com.draco18s.hardlib.api.advancement.FoundOreTrigger;
import com.draco18s.hardlib.api.advancement.MillstoneTrigger;
import com.draco18s.hardlib.api.advancement.WorldTimeTrigger;
import com.draco18s.hardlib.api.interfaces.IFlowerData;
import com.draco18s.hardlib.api.interfaces.IHardCrops;
import com.draco18s.hardlib.api.interfaces.IHardOreProcessing;
import com.draco18s.hardlib.api.interfaces.IHardOres;
import com.draco18s.hardlib.api.interfaces.IMechanicalPower;
import com.draco18s.hardlib.api.recipe.GrindingRecipe;
import com.draco18s.hardlib.api.recipe.SiftingRecipe;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.registries.ObjectHolder;

public class HardLibAPI {
	public static final Capability<IMechanicalPower> MECHANICAL_POWER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	public static final TagKey<Item> STONE_ANY = Tags.Items.STONE;
	
	public static IHardOres hardOres;
	public static IHardOreProcessing oreMachines;
	public static IHardCrops hardCrops;
	public static IFlowerData oreFlowers;

	public static class RecipeSerializers {
		@ObjectHolder(registryName = "minecraft:recipe_serializer", value = "hardlib:tag_output")
		public static RecipeSerializer<?> TAG_OUTPUT;
		@ObjectHolder(registryName = "minecraft:recipe_serializer", value = "hardlib:sifting")
		public static RecipeSerializer<?> SIFTING;
		@ObjectHolder(registryName = "minecraft:recipe_serializer", value = "hardlib:grinding")
		public static RecipeSerializer<?> GRINDING;
	}
	
	public static class RecipeTypes {
		@ObjectHolder(registryName = "minecraft:recipe_type", value = "hardlib:sifting")
		public static RecipeType<SiftingRecipe> SIFTING = null;
		@ObjectHolder(registryName = "minecraft:recipe_type", value = "hardlib:grinding")
		public static RecipeType<GrindingRecipe> GRINDING = null;
	}
	
	//public static class ModItemTags {
		//public static TagKey<Item> STONE = new TagKey<Item>(ForgeRegistries.Keys.ITEMS, new ResourceLocation(HardLib.MODID, "stone"));
	//}

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
