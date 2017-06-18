package com.draco18s.farming;

import org.apache.logging.log4j.Logger;

import com.draco18s.farming.block.BlockCropWeeds;
import com.draco18s.farming.block.BlockCropWinterWheat;
import com.draco18s.farming.block.BlockSaltOre;
import com.draco18s.farming.block.BlockTanner;
import com.draco18s.farming.entities.EntityItemFrameReplacement;
import com.draco18s.farming.entities.TileEntityTanner;
import com.draco18s.farming.entities.capabilities.IMilking;
import com.draco18s.farming.entities.capabilities.MilkStorage;
import com.draco18s.farming.integration.IndustryIntegration;
import com.draco18s.farming.integration.IntegrationHarvestcraft;
import com.draco18s.farming.item.ItemAchieves;
import com.draco18s.farming.item.ItemButcherKnife;
import com.draco18s.farming.item.ItemHydrometer;
import com.draco18s.farming.item.ItemNewFrame;
import com.draco18s.farming.item.ItemThermometer;
import com.draco18s.farming.item.ItemWinterSeeds;
import com.draco18s.farming.loot.KilledByWither;
import com.draco18s.farming.util.AnimalUtil;
import com.draco18s.farming.util.CropManager;
import com.draco18s.farming.util.EnumFarmAchieves;
import com.draco18s.farming.util.FarmingAchievements;
import com.draco18s.farming.world.WorldGenerator;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;
import com.draco18s.hardlib.util.CapabilityUtils;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ExistingSubstitutionException;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.Type;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid="harderfarming", name="HardFarming", version="{@version:farm}", dependencies = "required-after:hardlib;after:expindustry")
public class FarmingBase {
	@Instance("harderfarming")
	public static FarmingBase instance;
	
	@SidedProxy(clientSide="com.draco18s.farming.client.ClientProxy", serverSide="com.draco18s.farming.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;
	
	public static Block winterWheat;
	public static Block weeds;
	public static Block saltOre;
	public static Block tanningRack;

	public static Item winterWheatSeeds;
	public static Item thermometer;
	public static Item rainmeter;
	public static Item rawLeather;
	public static Item rawSalt;
	public static Item saltPile;
	public static Item butcherKnife;
	public static Item itemFrameReplacement;
	public static Item itemAchievementIcons;

	public static Configuration config;

	
	@CapabilityInject(IMilking.class)
	public static final Capability<IMilking> MILKING_CAPABILITY = null;

	public static final ResourceLocation MILK_ID = new ResourceLocation("harderfarming", "MilkCap");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		logger = event.getModLog();
		HardLibAPI.hardCrops = new CropManager();
		HardLibAPI.animalManager = new AnimalUtil();
		FarmingEventHandler.doSlowCrops = config.getBoolean("EnableCropSlowing", "CROPS", true, "Enables or disables the slowdown of crop growth.\nIf enabled, base probability is 10% as frequent as vanilla (ten times slower).\nNote: please disable Gany's Surface's snow accumulation, if it is\ninstalled (mine results in a smoother variation between blocks).\n");
		FarmingEventHandler.doBiomeCrops = config.getBoolean("EnableCropSlowingByBiome", "CROPS", true, "Enables or disables the crop growth based on biome information (which is effected by seasons,\nif enabled and ignored if slow crops is disabled). Most (vanilla) biomes have some semblance of a\ngrowing season, though it will be harder to grow food in the cold and dry biomes. Growing plants\ninside uses an effective temperature halfway closer to the ideal value.  For extreme biomes\nthis might be required!\nIf disabled, base slowdown probability is used instead.\n");
		FarmingEventHandler.cropsWorst = config.getInt("SlowByBiomeLowerBound", "CROPS", 16, 8, 96, "Configures the worst possible growth rate for biome based crop growth.\nIn the worst possible conditions, the chance that crops will grow will not drop\nbelow 100/(value + 10) %\nGenerally speaking this occurs in the frozen biomes during the winter, most notably Cold Taiga.\nThere should be no need for this value to exceed 16 for any biome other than Cold Taiga (50+)\nand Cold Beach (20+).\n");
		
		
		weeds = new BlockCropWeeds();
		EasyRegistry.registerBlock(weeds, "crop_weeds");
		winterWheat = new BlockCropWinterWheat();
		EasyRegistry.registerBlock(winterWheat, "crop_winter_wheat");
		
		winterWheatSeeds = new ItemWinterSeeds(winterWheat, Blocks.FARMLAND);
		EasyRegistry.registerItem(winterWheatSeeds, "seeds_winter_wheat");
		
		saltOre = new BlockSaltOre();
		EasyRegistry.registerBlockWithItem(saltOre, "saltore");
		tanningRack = new BlockTanner();
		EasyRegistry.registerBlockWithItem(tanningRack, "tanner");
		GameRegistry.registerTileEntity(TileEntityTanner.class, "harderfarming:tanning_rack");

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
		
		butcherKnife = new ItemButcherKnife(ToolMaterial.IRON);
		EasyRegistry.registerItem(butcherKnife, "butcherknife");
		
		itemFrameReplacement = new ItemNewFrame(EntityItemFrameReplacement.class);
		itemFrameReplacement.setRegistryName(new ResourceLocation("minecraft","item_frame"));
		itemFrameReplacement.setUnlocalizedName("frame");
		try {
			GameRegistry.addSubstitutionAlias("minecraft:item_frame", Type.ITEM, itemFrameReplacement);
		} catch (ExistingSubstitutionException e) {
			e.printStackTrace();
		}
		EntityRegistry.registerModEntity(new ResourceLocation("harderfarming:item_frame_rep"), EntityItemFrameReplacement.class, "harderfarming:item_frame_rep", 0, this, 48, 10, false);
		
		itemAchievementIcons = new ItemAchieves();
		EasyRegistry.registerItemWithVariants(itemAchievementIcons, "achieve_icons", EnumFarmAchieves.KILL_WEEDS);
		
		((AnimalUtil) HardLibAPI.animalManager).parseConfig(config);
		
		if(Loader.isModLoaded("expindustry")) {
			IndustryIntegration.addMoldRecipes();
		}
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new FarmingEventHandler());
		FarmingEventHandler.doRawLeather = config.getBoolean("doRawLeather", "ANIMALS", true, "Raw leather (rawhide) requires curing on a tanning rack before it can be used.\n");
		config.save();
		
		if(Loader.isModLoaded("harderores")) {
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(rawSalt), new ItemStack(saltPile, 2));
			HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(saltPile), new ItemStack(saltPile));
		}
		
		GameRegistry.registerWorldGenerator(new WorldGenerator(), 2);
		OreDictionary.registerOre("dustSalt", saltPile);
		OreDictionary.registerOre("foodSalt", saltPile);
		OreDictionary.registerOre("itemSalt", rawSalt);
		OreDictionary.registerOre("foodSalt", rawSalt);

		CropWeatherOffsets off = new CropWeatherOffsets(0.2f,0,0.5f,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.PUMPKIN_STEM, off);//primarily october growth
		off = new CropWeatherOffsets(0,0,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.WHEAT, off);//no offsets!
		off = new CropWeatherOffsets(0.8f,0.2f,0,0);
		HardLibAPI.hardCrops.putCropWeather(winterWheat, off);//grows best when cold
		off = new CropWeatherOffsets(-0.4f,0,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.MELON_STEM, off);//cold sensitive
		off = new CropWeatherOffsets(0.7f,0,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.POTATOES, off);//potatoes are a "cool season" crop
		off = new CropWeatherOffsets(0.1f,0.2f,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.CARROTS, off);//carrots take 4 months to mature, ideal growth between 60 and 70 F
		off = new CropWeatherOffsets(-0.4f,-0.3f,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.REEDS, off);//reeds grow warm and wet
		off = new CropWeatherOffsets(0.25f,0.1f,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.BEETROOTS, off);//beets grow cool and slightly dry
		off = new CropWeatherOffsets(-0.2f,-0.6f,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.COCOA, off);//warm and VERY wet
		off = new CropWeatherOffsets(-1.2f,1.2f,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.NETHER_WART, off);//HOT HOT HOT
		if(Loader.isModLoaded("harvestcraft")) {
			IntegrationHarvestcraft.registerCrops();
		}
		
		ItemStack glass = new ItemStack(Blocks.GLASS_PANE);

		if(OreDictionary.getOres("nuggetIron").size() > 0) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rainmeter), "ggg","gig","ggg",'g',glass,'i',"nuggetGold"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(thermometer), "ggg","gig","ggg",'g',glass,'i',"nuggetIron"));
		}
		else {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rainmeter), "ggg","gig","ggg",'g',glass,'i',"ingotGold"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(thermometer), "ggg","gig","ggg",'g',glass,'i',"ingotIron"));
		}
		if(FarmingEventHandler.doRawLeather) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(tanningRack), "sss","sts","s s",'s',"stickWood",'t',Items.STRING));
		}
		if(config.getBoolean("altKnifeRecipe", "GENERAL", false, "if the butcher's knife recipe conflicts with another repcie, toggle this.")) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(butcherKnife), " s","i ",'s',"stickWood",'i',Items.IRON_INGOT));
		}
		else {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(butcherKnife), "s "," i",'s',"stickWood",'i',Items.IRON_INGOT));
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		CapabilityManager.INSTANCE.register(IMilking.class, new MilkStorage(), new MilkStorage.Factory());
		FarmingAchievements.addCoreAchievements();
		LootConditionManager.registerCondition(new KilledByWither.Serializer());
	}
	
	public static IMilking getMilkData(EntityLivingBase entity) {
		return CapabilityUtils.getCapability(entity, MILKING_CAPABILITY, null);
	}
}
