package com.draco18s.farming;

import org.apache.logging.log4j.Logger;

import com.draco18s.farming.block.BlockCropWeeds;
import com.draco18s.farming.block.BlockSaltOre;
import com.draco18s.farming.block.BlockTanner;
import com.draco18s.farming.entities.TileEntityTanner;
import com.draco18s.farming.item.ItemHydrometer;
import com.draco18s.farming.item.ItemThermometer;
import com.draco18s.farming.util.CropManager;
import com.draco18s.farming.world.WorldGenerator;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid="harderfarming", name="HardFarming", version="{@version:farm}", dependencies = "required-after:hardlib")
public class FarmingBase {
	@Instance("harderfarming")
	public static FarmingBase instance;
	
	@SidedProxy(clientSide="com.draco18s.farming.client.ClientProxy", serverSide="com.draco18s.farming.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;
	
	public static Block weeds;
	public static Block saltOre;
	public static Block tanningRack;

	public static Item thermometer;
	public static Item rainmeter;
	public static Item rawLeather;
	public static Item rawSalt;
	public static Item saltPile;

	public static Configuration config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		logger = event.getModLog();
		HardLibAPI.hardCrops = new CropManager();
		
		//weeds = new BlockCropWeeds();
		//EasyRegistry.registerBlock(weeds, "crop_weeds");

    	saltOre = new BlockSaltOre();
    	EasyRegistry.registerBlockWithItem(saltOre, "saltore");
    	tanningRack = new BlockTanner();
    	EasyRegistry.registerBlockWithItem(tanningRack, "tanner");
    	GameRegistry.registerTileEntity(TileEntityTanner.class, "tanning_rack");

    	thermometer = new ItemThermometer();
    	EasyRegistry.registerItem(thermometer, "thermometer");
    	rainmeter = new ItemHydrometer();
    	EasyRegistry.registerItem(rainmeter, "hydrometer");
    	rawLeather = (new Item()).setCreativeTab(CreativeTabs.MATERIALS);
    	EasyRegistry.registerItem(rawLeather, "rawleather");
    	rawSalt = (new Item()).setCreativeTab(CreativeTabs.MATERIALS);
    	EasyRegistry.registerItem(rawSalt, "rawsalt");
    	saltPile = (new Item()).setCreativeTab(CreativeTabs.MATERIALS);
    	EasyRegistry.registerItem(saltPile, "saltpile");
    	
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		FarmingEventHandler.doRawLeather = config.getBoolean("doRawLeather", "ANIMALS", true, "Raw leather (rawhide) requires curing on a tanning rack before it can be used.\n");
    	config.save();
    	
    	if(Loader.isModLoaded("harderores")) {
    		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(rawSalt), new ItemStack(saltPile, 2));
    		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(saltPile), new ItemStack(saltPile));
    	}
    	
    	GameRegistry.registerWorldGenerator(new WorldGenerator(), 2);
    	OreDictionary.registerOre("dustSalt", saltPile);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new FarmingEventHandler());
	}
}
