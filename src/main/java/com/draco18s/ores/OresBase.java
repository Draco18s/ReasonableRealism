package com.draco18s.ores;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;

import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.hardlib.internal.IMetaLookup;
import com.draco18s.ores.block.*;
import com.draco18s.ores.item.*;

@Mod(modid="HarderOres", name="HarderOres", version="{@version:ore}"/*, dependencies = "required-after:HardLib"*/)
public class OresBase {
	@Instance
	public static OresBase instance;
	
	@SidedProxy(clientSide="com.draco18s.ores.client.ClientProxy", serverSide="com.draco18s.ores.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;
	
	public static Block oreIron;
	
	public static Item rawOre;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		/**
		 * To make this work you will need:
		 *  - EnumOreType (or similar) extending IMetaLookup
		 *  - Everything in the common and client proxies (many util methods)
		 *  - ItemOreBlock (or similar)
		 *  - both blockstate json files
		 *  - models/block/item json file (critical!)
		 */
		
		oreIron = new BlockHardOreBase();
		proxy.registerBlockWithCustomItem(oreIron, new ItemOreBlock(oreIron), "hardiron");
		
		rawOre = new ItemRawOre();
		proxy.RegisterItemWithVariants(rawOre, "orechunks", EnumOreType.IRON);
		
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
