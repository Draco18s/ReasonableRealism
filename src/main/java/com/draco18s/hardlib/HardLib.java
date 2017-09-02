package com.draco18s.hardlib;

import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.advancement.BreakBlockTrigger;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger;
import com.draco18s.hardlib.api.advancement.FoundOreTrigger;
import com.draco18s.hardlib.api.advancement.MillstoneTrigger;
import com.draco18s.hardlib.api.advancement.WorldTimeTrigger;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid="hardlib", name="HardLib", version="{@version:lib}"/*, dependencies = "required-after:"*/)
public class HardLib {
	@Instance("hardlib")
	public static HardLib instance;
	
	@SidedProxy(clientSide="com.draco18s.hardlib.client.ClientEasyRegistry", serverSide="com.draco18s.hardlib.EasyRegistry")
	public static EasyRegistry proxy;
	
	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		OreDictionary.registerOre("stoneAny", new ItemStack(Blocks.STONE, 1, 0));
		OreDictionary.registerOre("stoneAny", new ItemStack(Blocks.STONE, 1, 1));
		OreDictionary.registerOre("stoneAny", new ItemStack(Blocks.STONE, 1, 2));
		OreDictionary.registerOre("stoneAny", new ItemStack(Blocks.STONE, 1, 3));
		OreDictionary.registerOre("stoneAny", new ItemStack(Blocks.STONE, 1, 4));
		OreDictionary.registerOre("stoneAny", new ItemStack(Blocks.STONE, 1, 5));
		OreDictionary.registerOre("stoneAny", new ItemStack(Blocks.STONE, 1, 6));
		MinecraftForge.EVENT_BUS.register(proxy);
		
		HardLibAPI.Advancements.MILL_BUILT = (MillstoneTrigger) EasyRegistry.registerAdvancementTrigger(new MillstoneTrigger());
		HardLibAPI.Advancements.FOUND_ORE = (FoundOreTrigger) EasyRegistry.registerAdvancementTrigger(new FoundOreTrigger());
		HardLibAPI.Advancements.BLOCK_BREAK = (BreakBlockTrigger) EasyRegistry.registerAdvancementTrigger(new BreakBlockTrigger());
		HardLibAPI.Advancements.WORLD_TIME = (WorldTimeTrigger) EasyRegistry.registerAdvancementTrigger(new WorldTimeTrigger());
		HardLibAPI.Advancements.DISTANCE_TRAVELED = (DistanceTraveledTrigger) EasyRegistry.registerAdvancementTrigger(new DistanceTraveledTrigger());
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		CogHelper.unpackConfigs();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
