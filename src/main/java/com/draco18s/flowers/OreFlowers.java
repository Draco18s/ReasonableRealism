package com.draco18s.flowers;

import org.apache.logging.log4j.Logger;

import com.draco18s.flowers.block.BlockOreFlower1;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.ores.item.ItemOreFlower1;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="OreFlowers", name="OreFlowers", version="{@version:flowers}"/*, dependencies = "required-after:HardLib"*/)
public class OreFlowers {
	@Instance("OreFlowers")
	public static OreFlowers instance;
	
	public static Block oreFlowers1;
	
	//@SidedProxy(clientSide="com.draco18s.ores.client.ClientProxy", serverSide="com.draco18s.ores.CommonProxy")
	//public static CommonProxy proxy;
	
	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		oreFlowers1 = new BlockOreFlower1();
		EasyRegistry.registerBlockWithCustomItem(oreFlowers1, new ItemOreFlower1(oreFlowers1), "oreFlowers1");
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
