package com.draco18s.farming;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.draco18s.farming.block.CropWeedsBlock;
import com.draco18s.farming.block.MulchedFarmlandBlock;
import com.draco18s.farming.block.WeedyFarmlandBlock;
import com.draco18s.farming.block.CropWinterWheatBlock;
import com.draco18s.farming.block.SaltOreBlock;
import com.draco18s.farming.block.TannerBlock;
import com.draco18s.farming.entity.MilkSupply;
import com.draco18s.farming.entity.TannerBlockEntity;
import com.draco18s.farming.integration.HarvestcraftIntegration;
import com.draco18s.farming.item.ButcherKnife;
import com.draco18s.farming.item.ClimateGaugeItem;
import com.draco18s.farming.item.WeedSeedsItem;
import com.draco18s.farming.item.WinterSeedsItem;
import com.draco18s.farming.loot.GrassLootModifier;
import com.draco18s.farming.loot.RawLeatherModifier;
import com.draco18s.farming.loot.TemperatureCheck;
import com.draco18s.farming.util.CropManager;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.ObjectHolder;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;
import com.draco18s.hardlib.util.Tuple;
import com.mojang.serialization.MapCodec;

import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(HarderFarming.MODID)
@EventBusSubscriber(modid = HarderFarming.MODID)
public class HarderFarming {
	public static final String MODID = "harderfarming";
	
	public HarderFarming() {
		registerCreativeTab();
		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.LOCAL, FarmingConfig.SPEC);
		HardLibAPI.hardCrops = new CropManager();
		ModTriggers.crop_rotation = EasyRegistry.registerAdvancementTrigger(getRL("crop_rotation"), new PlayerTrigger());
		EasyRegistry.registerBlock(SaltOreBlock::new, getRL("ore_salt"), new Item.Properties());
		DeferredHolder<Block, Block> wheat = EasyRegistry.registerBlock(CropWinterWheatBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT), getRL("crop_winter_wheat"));
		EasyRegistry.registerItem(props -> new ButcherKnife(ToolMaterial.IRON, props), getRL("butcher_knife"));
		EasyRegistry.registerItem(props -> new WinterSeedsItem(wheat.get(), props), getRL("winter_wheat_seeds"));
		EasyRegistry.registerBlock(CropWeedsBlock::new, getRL("crop_weeds"));
		// Weeds grow in the farmland, under any crop (crop_weeds is only kept for worlds that have some).
		EasyRegistry.registerBlock(WeedyFarmlandBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND), getRL("weedy_farmland"));
		// Carpet or leaf litter laid among a crop.
		EasyRegistry.registerBlock(MulchedFarmlandBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND), getRL("mulched_farmland"));
		EasyRegistry.registerItem(WeedSeedsItem::new, getRL("weed_seeds"));
		DeferredHolder<Block, Block> tanner = EasyRegistry.registerBlock(TannerBlock::new, getRL("tanner"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> new BlockEntityType<>(TannerBlockEntity::new, tanner.get()), getRL("tanner"));
		EasyRegistry.registerItem(ClimateGaugeItem.Thermometer::new, getRL("thermometer"));
		EasyRegistry.registerItem(ClimateGaugeItem.Hydrometer::new, getRL("hydrometer"));
		EasyRegistry.registerOther(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, 
			new Tuple<Identifier,Supplier<MapCodec<? extends IGlobalLootModifier>>>(getRL("grass_modifier"),GrassLootModifier.CODEC::get)	
		);
		EasyRegistry.registerOther(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
			new Tuple<Identifier,Supplier<MapCodec<? extends IGlobalLootModifier>>>(getRL("raw_leather"),RawLeatherModifier.CODEC::get)
		);
		EasyRegistry.registerOther(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, new Tuple<Identifier, Supplier<AttachmentType<?>>>(getRL("milk"), () -> MilkSupply.MILK));
		ModLootConditionTypes.can_snow =EasyRegistry.registerOther(Registries.LOOT_CONDITION_TYPE, new Tuple<Identifier,Supplier<MapCodec<? extends LootItemCondition>>>(
				getRL("can_snow"),() -> TemperatureCheck.CODEC));
		
		// Milk keeps (and stacks) as cheese: a bucket of milk and salt.
		EasyRegistry.registerItem(props -> new Item(props.food(new FoodProperties.Builder().nutrition(1).saturationModifier(1.5f).build())), getRL("cheese"));

		String[] itemNames = {
				"tinydust_sugar",
				"tinydust_flour", "largedust_flour",
				"salt_chunk", "salt",
				"raw_leather",
		};
		for(String it : itemNames) {
			EasyRegistry.registerItem(Item::new, getRL(it));
		}
	}
	
	public static class Tags {
		public static class Blocks{
			public static final TagKey<Block> MINABLE_WITH_KNIFE = new TagKey<Block>(Registries.BLOCK, HardLib.getRL("minecraft", "mineable/knife"));
			/** What a cow grazes on to make milk (see MilkSupply). */
			public static final TagKey<Block> COW_FORAGE = new TagKey<Block>(Registries.BLOCK, getRL("cow_forage"));
			/** A mooshroom also grazes on mycelium. */
			public static final TagKey<Block> MOOSHROOM_FORAGE = new TagKey<Block>(Registries.BLOCK, getRL("mooshroom_forage"));
		}
		public static class Items {
			/** What a tanning rack accepts as salt. */
			public static final TagKey<Item> SALT = new TagKey<Item>(Registries.ITEM, HardLib.getRL("c", "dusts/salt"));
		}
	}
	
	public static class ModTriggers {
		/** Harvesting a crop planted in rows between different crops (see EventHandler.onCropHarvest). */
		public static PlayerTrigger crop_rotation;
	}

	public static class ModLootConditionTypes {
		public static DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<? extends LootItemCondition>> can_snow = null;
	}
	
	public static class ModBlocks {
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"crop_winter_wheat")
		public static Block crop_winter_wheat;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_salt")
		public static Block ore_salt;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"crop_weeds")
		public static Block crop_weeds;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"tanner")
		public static Block tanner;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"weedy_farmland")
		public static Block weedy_farmland;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"mulched_farmland")
		public static Block mulched_farmland;
	}

	public static class ModBlockEntities {
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"tanner")
		public static BlockEntityType<TannerBlockEntity> tanner;
	}
	
	public static class ModItems {
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"winter_wheat_seeds")
		public static Item winter_wheat_seeds;

		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_sugar")
		public static Item tinydust_sugar;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_flour")
		public static Item tinydust_flour;

		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"largedust_flour")
		public static Item largedust_flour;
		
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"salt_chunk")
		public static Item salt_chunk;
		
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"salt")
		public static Item salt;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"cheese")
		public static Item cheese;
		
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"butcher_knife")
		public static Item butcher_knife;

		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"weed_seeds")
		public static Item weed_seeds;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"raw_leather")
		public static Item raw_leather;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"thermometer")
		public static Item thermometer;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"hydrometer")
		public static Item hydrometer;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tanner")
		public static Item tanner;
	}
	
	private static void registerCreativeTab() {
		EasyRegistry.registerCreativeTab(getRL("creative_tab"), builder -> builder
			.title(Component.translatable("itemGroup." + MODID))
			.icon(() -> new ItemStack(ModItems.winter_wheat_seeds))
			.displayItems((params, output) -> {
				Set<Item> items = new LinkedHashSet<Item>();
				Collections.addAll(items, ModItems.winter_wheat_seeds, ModItems.weed_seeds, ModItems.tinydust_flour, ModItems.largedust_flour,
						ModItems.tinydust_sugar, ModBlocks.ore_salt.asItem(), ModItems.salt_chunk, ModItems.salt, ModItems.cheese, ModItems.tanner, ModItems.raw_leather,
						ModItems.butcher_knife, ModItems.thermometer, ModItems.hydrometer);
				// Then the rest of farming: tools, bone meal, and every plantable crop (vanilla's and other mods'), then produce.
				Collections.addAll(items, Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE,
						Items.BONE_MEAL, Items.COMPOSTER, Items.HAY_BLOCK);
				for(Item item : BuiltInRegistries.ITEM) {
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
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.tanner, TannerBlockEntity::getItemHandler);
	}

	@SubscribeEvent
	public static void addItemsToCreativeTab(final BuildCreativeModeTabContentsEvent event) {
		ResourceKey<CreativeModeTab> tab = event.getTabKey();
		if(tab == CreativeModeTabs.FOOD_AND_DRINKS) {
			event.accept(ModItems.tinydust_sugar);
			event.accept(ModItems.tinydust_flour);
			event.accept(ModItems.largedust_flour);
			event.accept(ModItems.cheese);
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

	public static Identifier getRL(String name) {
		return HardLib.getRL(MODID, name);
	}
}
