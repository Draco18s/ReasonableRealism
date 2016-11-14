package com.draco18s.industry;

import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.industry.block.BlockCartLoader;
import com.draco18s.industry.block.BlockDistributor;
import com.draco18s.industry.block.BlockPoweredRailBridge;
import com.draco18s.industry.block.BlockRailBridge;
import com.draco18s.industry.block.BlockTypeRail;
import com.draco18s.industry.block.BlockWoodenHopper;
import com.draco18s.industry.entities.TileEntityCartLoader;
import com.draco18s.industry.entities.TileEntityDistributor;
import com.draco18s.industry.entities.TileEntityWoodenHopper;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid="expindustry", name="ExpandedIndustry", version="{@version:industry}"/*, dependencies = "required-after:HardLib"*/)
public class ExpandedInsutryBase {
	@Instance("expindustry")
	public static ExpandedInsutryBase instance;
	
	public static Block blockWoodHopper;
	public static Block blockDistributor;
	public static Block blockCartLoader;
	public static Block blockRailBridge;
	public static Block blockTypeRail;
	public static Block blockRailBridgePowered;
	
	/*@SidedProxy(clientSide="com.draco18s.hardlib.client.ClientEasyRegistry", serverSide="com.draco18s.ores.EasyRegistry")
	public static EasyRegistry proxy;*/
	
	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		blockWoodHopper = new BlockWoodenHopper();
		EasyRegistry.registerBlockWithItem(blockWoodHopper, "machine_wood_hopper");
		blockDistributor = new BlockDistributor();
		EasyRegistry.registerBlockWithItem(blockDistributor, "machine_distributor");
		blockCartLoader = new BlockCartLoader();
		EasyRegistry.registerBlockWithItem(blockCartLoader, "machine_cart_loader");
		blockRailBridge = new BlockRailBridge();
		EasyRegistry.registerBlockWithItem(blockRailBridge, "rail_bridge");
		blockTypeRail = new BlockTypeRail();
		EasyRegistry.registerBlockWithItem(blockTypeRail, "type_rail");
		blockRailBridgePowered = new BlockPoweredRailBridge();
		EasyRegistry.registerBlockWithItem(blockRailBridgePowered, "rail_bridge_powered");

		GameRegistry.registerTileEntity(TileEntityWoodenHopper.class, "machine_wood_hopper");
		GameRegistry.registerTileEntity(TileEntityDistributor.class, "machine_distributor");
		GameRegistry.registerTileEntity(TileEntityCartLoader.class, "machine_cart_loader");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IndustryGuiHandler());
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		GameRegistry.addRecipe(new ShapedOreRecipe(blockWoodHopper, "p p", "p p", " p ", 'p', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(blockDistributor, " h ", " i ", "ppp", 'p', "plankWood", 'i', "ingotIron", 'h', Blocks.HOPPER));
		GameRegistry.addRecipe(new ShapedOreRecipe(blockCartLoader, "i i", "rhd", " i ", 'i', "ingotIron", 'h', Blocks.HOPPER, 'r', Blocks.REDSTONE_BLOCK, 'd', Items.REPEATER));
		GameRegistry.addRecipe(new ShapedOreRecipe(blockRailBridge, "R", "P", 'R', Blocks.RAIL, 'P', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(blockRailBridgePowered, "R", "P", 'R', Blocks.GOLDEN_RAIL, 'P', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTypeRail,6), "i i", "ipi", "iqi", 'p', Blocks.STONE_PRESSURE_PLATE, 'i', "ingotIron", 'q', Items.QUARTZ));
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
