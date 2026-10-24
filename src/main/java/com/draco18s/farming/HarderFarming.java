package com.draco18s.farming;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.draco18s.farming.block.CropWeedsBlock;
import com.draco18s.farming.block.CropWinterWheatBlock;
import com.draco18s.farming.block.SaltOreBlock;
import com.draco18s.farming.block.TannerBlock;
import com.draco18s.farming.entity.TannerBlockEntity;
import com.draco18s.farming.integration.HarvestcraftIntegration;
import com.draco18s.farming.item.ButcherKnife;
import com.draco18s.farming.item.ClimateGaugeItem;
import com.draco18s.farming.item.WinterSeedsItem;
import com.draco18s.farming.loot.GrassLootModifier;
import com.draco18s.farming.loot.RawLeatherModifier;
import com.draco18s.farming.loot.TemperatureCheck;
import com.draco18s.farming.util.CropManager;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;
import com.mojang.serialization.Codec;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

@Mod(HarderFarming.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class HarderFarming {
	public static final String MODID = "harderfarming";
	
	public HarderFarming() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FarmingConfig.SPEC);
		HardLibAPI.hardCrops = new CropManager();
		EasyRegistry.registerBlock(SaltOreBlock::new, getRL("ore_salt"), new Item.Properties());
		RegistryObject<Block> wheat = EasyRegistry.registerBlock(CropWinterWheatBlock::new, getRL("crop_winter_wheat"));
		EasyRegistry.registerItem(() -> new ButcherKnife(Tiers.IRON), getRL("butcher_knife"));
		EasyRegistry.registerItem(() -> new WinterSeedsItem(wheat.get()), getRL("winter_wheat_seeds"));
		RegistryObject<Block> weeds = EasyRegistry.registerBlock(CropWeedsBlock::new, getRL("crop_weeds"));
		EasyRegistry.registerItem(() -> new ItemNameBlockItem(weeds.get(), new Item.Properties()), getRL("weed_seeds"));
		RegistryObject<Block> tanner = EasyRegistry.registerBlock(TannerBlock::new, getRL("tanner"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(TannerBlockEntity::new, tanner.get()).build(null), getRL("tanner"));
		EasyRegistry.registerItem(ClimateGaugeItem.Thermometer::new, getRL("thermometer"));
		EasyRegistry.registerItem(ClimateGaugeItem.Hydrometer::new, getRL("hydrometer"));
		EasyRegistry.registerOther(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, 
			new Tuple<ResourceLocation,Supplier<Codec<? extends IGlobalLootModifier>>>(getRL("grass_modifier"),GrassLootModifier.CODEC::get)	
		);
		EasyRegistry.registerOther(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
			new Tuple<ResourceLocation,Supplier<Codec<? extends IGlobalLootModifier>>>(getRL("raw_leather"),RawLeatherModifier.CODEC::get)
		);
		ModLootConditionTypes.can_snow = EasyRegistry.registerOther(Registries.LOOT_CONDITION_TYPE, new Tuple<ResourceLocation,Supplier<LootItemConditionType>>(
				getRL("can_snow"),() -> new LootItemConditionType(new TemperatureCheck.Serializer())));
		
		String[] itemNames = {
				"tinydust_sugar",
				"tinydust_flour", "largedust_flour",
				"salt_chunk", "salt",
				"raw_leather",
		};
		for(String it : itemNames) {
			EasyRegistry.registerItem(() -> new Item(new Item.Properties()), getRL(it));
		}
	}
	
	public static class Tags {
		public static class Blocks{
			public static final TagKey<Block> MINABLE_WITH_KNIFE = new TagKey<Block>(ForgeRegistries.Keys.BLOCKS, HardLib.getRL("minecraft", "mineable/knife"));
		}
		public static class Items {
			/** What a tanning rack accepts as salt. */
			public static final TagKey<Item> SALT = new TagKey<Item>(ForgeRegistries.Keys.ITEMS, HardLib.getRL("forge", "dusts/salt"));
		}
	}
	
	public static class ModTriggers {
		/** Harvesting a crop planted in rows between different crops (see EventHandler.onCropHarvest). */
		public static PlayerTrigger crop_rotation;
	}

	public static class ModLootConditionTypes {
		public static RegistryObject<LootItemConditionType> can_snow = null;
	}
	
	public static class ModBlocks {
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"crop_winter_wheat")
		public static final Block crop_winter_wheat = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_salt")
		public static final Block ore_salt = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"crop_weeds")
		public static final Block crop_weeds = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"tanner")
		public static final Block tanner = null;
	}

	public static class ModBlockEntities {
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"tanner")
		public static final BlockEntityType<TannerBlockEntity> tanner = null;
	}
	
	public static class ModItems {
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"winter_wheat_seeds")
		public static final Item winter_wheat_seeds = null;

		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_sugar")
		public static final Item tinydust_sugar = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_flour")
		public static final Item tinydust_flour = null;

		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"largedust_flour")
		public static final Item largedust_flour = null;
		
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"salt_chunk")
		public static final Item salt_chunk = null;
		
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"salt")
		public static final Item salt = null;
		
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"butcher_knife")
		public static final Item butcher_knife = null;

		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"weed_seeds")
		public static final Item weed_seeds = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"raw_leather")
		public static final Item raw_leather = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"thermometer")
		public static final Item thermometer = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"hydrometer")
		public static final Item hydrometer = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tanner")
		public static final Item tanner = null;
	}
	
	@SubscribeEvent
	public static void registerCreativeTab(CreativeModeTabEvent.Register event) {
		event.registerCreativeModeTab(getRL("creative_tab"), builder -> builder
			.title(Component.translatable("itemGroup." + MODID))
			.icon(() -> new ItemStack(ModItems.winter_wheat_seeds))
			.displayItems((params, output) -> {
				Set<Item> items = new LinkedHashSet<Item>();
				Collections.addAll(items, ModItems.winter_wheat_seeds, ModItems.weed_seeds, ModItems.tinydust_flour, ModItems.largedust_flour,
						ModItems.tinydust_sugar, ModBlocks.ore_salt.asItem(), ModItems.salt_chunk, ModItems.salt, ModItems.tanner, ModItems.raw_leather,
						ModItems.butcher_knife, ModItems.thermometer, ModItems.hydrometer);
				// Then the rest of farming: tools, bone meal, and every plantable crop (vanilla's and other mods'), then produce.
				Collections.addAll(items, Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE,
						Items.BONE_MEAL, Items.COMPOSTER, Items.HAY_BLOCK);
				for(Item item : ForgeRegistries.ITEMS) {
					if(item instanceof BlockItem blockItem && isCropBlock(blockItem.getBlock())) {
						items.add(item);
					}
				}
				Collections.addAll(items, Items.WHEAT, Items.BEETROOT, Items.PUMPKIN, Items.MELON, Items.MELON_SLICE);
				items.stream().filter(item -> item.isEnabled(params.enabledFeatures())).forEach(output::accept);
			}));
	}

	/** Blocks grown from a planted item: crops, stems, cocoa, nether wart, sugar cane, berries. */
	private static boolean isCropBlock(Block block) {
		return block instanceof CropBlock || block instanceof StemBlock || block instanceof CocoaBlock || block instanceof NetherWartBlock
				|| block instanceof SugarCaneBlock || block instanceof SweetBerryBushBlock || block instanceof CaveVinesBlock;
	}

	@SubscribeEvent
	public static void addItemsToCreativeTab(final CreativeModeTabEvent.BuildContents event) {
		CreativeModeTab tab = event.getTab();
		if(tab == CreativeModeTabs.FOOD_AND_DRINKS) {
			event.accept(ModItems.tinydust_sugar);
			event.accept(ModItems.tinydust_flour);
			event.accept(ModItems.largedust_flour);
		}
		if(tab == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(ModItems.thermometer);
			event.accept(ModItems.hydrometer);
		}
		if(tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			event.accept(ModItems.tanner);
		}
		if(tab == CreativeModeTabs.INGREDIENTS) {
			event.accept(ModItems.raw_leather);
		}
		if(tab == CreativeModeTabs.NATURAL_BLOCKS) {
			event.accept(ModBlocks.ore_salt);
			event.accept(ModItems.weed_seeds);
		}
	}

	@SubscribeEvent
	public static void onCommonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			registerCropData();
			HarvestcraftIntegration.registerCrops();
			ModTriggers.crop_rotation = EasyRegistry.registerAdvancementTrigger(new PlayerTrigger(getRL("crop_rotation")));
		});
	}

	/**
	 * The climate each crop prefers, as offsets from the biome's: positive temperature is "cold native",
	 * positive rainfall is "dry native". (1.12 also shifted these by season; the season offsets are not used.)
	 */
	private static void registerCropData() {
		HardLibAPI.hardCrops.putCropWeather(net.minecraft.world.level.block.Blocks.PUMPKIN_STEM, new CropWeatherOffsets(0.2f,0,0,0));
		HardLibAPI.hardCrops.putCropWeather(net.minecraft.world.level.block.Blocks.WHEAT, new CropWeatherOffsets(0,0,0,0));//no offsets!
		HardLibAPI.hardCrops.putCropWeather(ModBlocks.crop_winter_wheat, new CropWeatherOffsets(0.8f,0.2f,0,0));//grows best when cold
		HardLibAPI.hardCrops.putCropWeather(net.minecraft.world.level.block.Blocks.MELON_STEM, new CropWeatherOffsets(-0.4f,0,0,0));//cold sensitive
		HardLibAPI.hardCrops.putCropWeather(net.minecraft.world.level.block.Blocks.POTATOES, new CropWeatherOffsets(0.7f,0,0,0));//potatoes are a "cool season" crop
		HardLibAPI.hardCrops.putCropWeather(net.minecraft.world.level.block.Blocks.CARROTS, new CropWeatherOffsets(0.1f,0.2f,0,0));//ideal growth between 60 and 70 F
		HardLibAPI.hardCrops.putCropWeather(net.minecraft.world.level.block.Blocks.SUGAR_CANE, new CropWeatherOffsets(-0.4f,-0.3f,0,0));//reeds grow warm and wet
		HardLibAPI.hardCrops.putCropWeather(net.minecraft.world.level.block.Blocks.BEETROOTS, new CropWeatherOffsets(0.25f,0.1f,0,0));//beets grow cool and slightly dry
		HardLibAPI.hardCrops.putCropWeather(net.minecraft.world.level.block.Blocks.COCOA, new CropWeatherOffsets(-0.2f,-0.6f,0,0));//warm and VERY wet
		HardLibAPI.hardCrops.putCropWeather(net.minecraft.world.level.block.Blocks.NETHER_WART, new CropWeatherOffsets(-1.2f,1.2f,0,0));//HOT HOT HOT
		HardLibAPI.hardCrops.putCropWeather(net.minecraft.world.level.block.Blocks.SWEET_BERRY_BUSH, new CropWeatherOffsets(0.6f,-0.1f,0,0));//Cool and wet
	}

	public static ResourceLocation getRL(String name) {
		return HardLib.getRL(MODID, name);
	}
}
