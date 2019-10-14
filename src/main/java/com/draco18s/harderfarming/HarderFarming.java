package com.draco18s.harderfarming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.harderfarming.block.CropWeedsBlock;
import com.draco18s.harderfarming.block.CropWinterWheatBlock;
import com.draco18s.harderfarming.block.SaltOreBlock;
import com.draco18s.harderfarming.block.TannerBlock;
import com.draco18s.harderfarming.config.ConfigHolder;
import com.draco18s.harderfarming.entity.TannerTileEntity;
import com.draco18s.harderfarming.integration.IntegrationHarvestcraft;
import com.draco18s.harderfarming.item.ButcherKnifeItem;
import com.draco18s.harderfarming.item.HydrometerItem;
import com.draco18s.harderfarming.item.ThermometerItem;
import com.draco18s.harderfarming.item.WinterSeedsItem;
import com.draco18s.harderfarming.loot.TemperatureCheck;
import com.draco18s.harderfarming.util.CropManager;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.date.HardLibDate;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod(HarderFarming.MODID)
public class HarderFarming {
	public static final String MODID = "harderfarming";
	public static final Logger LOGGER = LogManager.getLogger();
	//public static final IProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	
	public HarderFarming() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener((FMLCommonSetupEvent event) -> {

		});

		modEventBus.addListener((FMLLoadCompleteEvent event) -> {
			LootConditionManager.registerCondition(new TemperatureCheck.Serializer());
			HardLibDate.initBiomeData();
			RegisterCropData();
			if(HardLibAPI.oreMachines != null) {
				HardLibAPI.oreMachines.addMillRecipe(new ItemStack(ModItems.raw_salt,1), new ItemStack(ModItems.salt_pile,2));
				HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(ModItems.salt_pile), new ItemStack(ModItems.salt_pile), true);
				
				HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.BONE,1), new ItemStack(Items.BONE_MEAL, 2));
				HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT,1), new ItemStack(ModItems.tinydust_wheat, 4));
				HardLibAPI.oreMachines.addMillRecipe(() -> ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","seeds")), new ItemStack(ModItems.tinydust_wheat, 1));
				HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(ModItems.tinydust_wheat, 8), new ItemStack(ModItems.largedust_wheat), true);
				HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.SUGAR_CANE,1), new ItemStack(ModItems.tinydust_sugar, 6));
				HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(ModItems.tinydust_sugar, 8), new ItemStack(Items.SUGAR), true);
			}
		});
		modEventBus.addListener((ModConfig.ModConfigEvent event) -> {
			final ModConfig config = event.getConfig();
			if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
				//ConfigHelper.bakeClient(config);
			} else if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
				//ConfigHelper.bakeServer(config);
			}
		});

		final ModLoadingContext modLoadingContext = ModLoadingContext.get();
		modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
		modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
		
		HardLibAPI.hardCrops = new CropManager();
		//PacketHandler.register();
		Block block;
		Item item;
		
		block = new CropWeedsBlock();
		EasyRegistry.registerBlock(block, "crop_weeds");
		item = new BlockNamedItem(block, new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(item, "weed_seeds");
		
		block = new CropWinterWheatBlock();
		EasyRegistry.registerBlock(block, "crop_winter_wheat");
		item = new WinterSeedsItem(block);
		EasyRegistry.registerItem(item, "winter_wheat_seeds");
		
		block = new SaltOreBlock();
		EasyRegistry.registerBlock(block, "salt_ore", new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		
		item = new ButcherKnifeItem(ItemTier.IRON);
		EasyRegistry.registerItem(item, "butcher_knife");
		
		item = (new Item(new Item.Properties().group(ItemGroup.MATERIALS)));
		EasyRegistry.registerItem(item, "raw_salt");
		item = (new Item(new Item.Properties().group(ItemGroup.MATERIALS)));
		EasyRegistry.registerItem(item, "salt_pile");
		item = (new ThermometerItem());
		EasyRegistry.registerItem(item, "thermometer");
		item = (new HydrometerItem());
		EasyRegistry.registerItem(item, "hydrometer");
		item = (new Item(new Item.Properties().group(ItemGroup.MATERIALS)));
		EasyRegistry.registerItem(item, "raw_leather");

		item = new Item(new Item.Properties().group(ItemGroup.MISC));
		EasyRegistry.registerItem(item, "tinydust_flour");
		item = new Item(new Item.Properties().group(ItemGroup.MISC));
		EasyRegistry.registerItem(item, "largedust_flour");

		item = (new Item(new Item.Properties().group(ItemGroup.MISC)));
		EasyRegistry.registerItem(item, "tinydust_sugar");
		
		block = new TannerBlock();
		EasyRegistry.registerBlock(block, "tanner", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(TannerTileEntity::new, block), HarderFarming.MODID, "tanner");
		
	}

	private void RegisterCropData() {
		CropWeatherOffsets off = new CropWeatherOffsets(0.2f,0,0.5f,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.PUMPKIN_STEM, off);//primarily october growth
		off = new CropWeatherOffsets(0,0,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.WHEAT, off);//no offsets!
		off = new CropWeatherOffsets(0.8f,0.2f,0,0);
		HardLibAPI.hardCrops.putCropWeather(ModBlocks.crop_winter_wheat, off);//grows best when cold
		off = new CropWeatherOffsets(-0.4f,0,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.MELON_STEM, off);//cold sensitive
		off = new CropWeatherOffsets(0.7f,0,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.POTATOES, off);//potatoes are a "cool season" crop
		off = new CropWeatherOffsets(0.1f,0.2f,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.CARROTS, off);//carrots take 4 months to mature, ideal growth between 60 and 70 F
		off = new CropWeatherOffsets(-0.4f,-0.3f,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.SUGAR_CANE, off);//reeds grow warm and wet
		off = new CropWeatherOffsets(0.25f,0.1f,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.BEETROOTS, off);//beets grow cool and slightly dry
		off = new CropWeatherOffsets(-0.2f,-0.6f,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.COCOA, off);//warm and VERY wet
		off = new CropWeatherOffsets(-1.2f,1.2f,0,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.NETHER_WART, off);//HOT HOT HOT
		off = new CropWeatherOffsets(0.6f,-0.1f,0.3f,0);
		HardLibAPI.hardCrops.putCropWeather(Blocks.SWEET_BERRY_BUSH, off);//Cool and wet
		if(ModList.get().isLoaded("harvestcraft")) {
			IntegrationHarvestcraft.registerCrops();
		}
	}

	@ObjectHolder(HarderFarming.MODID)
	public static class ModBlocks {
		public static final Block crop_weeds = null;
		public static final Block crop_winter_wheat = null;
	}

	@ObjectHolder(HarderFarming.MODID)
	public static class ModItems {
		public static final Item tinydust_sugar = null;
		public static final Item winter_wheat_seeds = null;
		public static final Item weed_seeds = null;
		public static final Item butcher_knife = null;
		public static final Item raw_salt = null;
		public static final Item salt_pile = null;
		public static final Item largedust_wheat = null;
		public static final Item tinydust_wheat = null;
		public static final Item raw_leather = null;
	}

	@ObjectHolder(HarderFarming.MODID)
	public static class ModTileEntities {
		public static final TileEntityType<TannerTileEntity> tanner = null;
	}

	@ObjectHolder(HarderFarming.MODID)
	public static class ModContainerTypes {
		//public static final ContainerType<SifterContainer> sifter = null;
	}

	@ObjectHolder(HarderFarming.MODID)
	public static class ModEnchantments {
	}
	
	public static class ModItemTags {
		public static final Tag<Item> SALT = new ItemTags.Wrapper(new ResourceLocation("forge", "dusts/salt"));
	}
}