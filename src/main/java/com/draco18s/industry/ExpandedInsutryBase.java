package com.draco18s.industry;

import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.industry.block.BlockWoodenHopper;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid="expindustry", name="ExpandedIndustry", version="{@version:industry}"/*, dependencies = "required-after:HardLib"*/)
public class ExpandedInsutryBase {
	@Instance("expindustry")
	public static ExpandedInsutryBase instance;
	
	public static Block blockWoodHopper;
	
	/*@SidedProxy(clientSide="com.draco18s.hardlib.client.ClientEasyRegistry", serverSide="com.draco18s.ores.EasyRegistry")
	public static EasyRegistry proxy;*/
	
	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		//TODO: None of this works.
		blockWoodHopper = new BlockWoodenHopper();
		EasyRegistry.registerBlockWithItem(blockWoodHopper, "machine_wood_hopper");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IndustryGuiHandler());
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
