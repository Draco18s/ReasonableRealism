package com.draco18s.farming;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="hardlib", name="HardLib", version="{@version:lib}"/*, dependencies = "required-after:HardLib"*/)
public class FamingBase {
	@Instance("hardlib")
	public static FamingBase instance;
	
	@SidedProxy(clientSide="com.draco18s.farming.client.ClientProxy", serverSide="com.draco18s.farming.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new FarmingEventHandler());
	}
}
