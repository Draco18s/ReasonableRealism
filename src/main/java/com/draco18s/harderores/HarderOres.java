package com.draco18s.harderores;

import java.awt.Color;
import java.util.Iterator;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.harderores.block.AxelBlock;
import com.draco18s.harderores.block.MillstoneBlock;
import com.draco18s.harderores.block.PackagerBlock;
import com.draco18s.harderores.block.SifterBlock;
import com.draco18s.harderores.block.SluiceBlock;
import com.draco18s.harderores.block.SluiceOutput;
import com.draco18s.harderores.block.WindvaneBlock;
import com.draco18s.harderores.block.ore.HardOreBlock;
import com.draco18s.harderores.block.ore.LimoniteBlock;
import com.draco18s.harderores.enchantment.ProspectorEnchantment;
import com.draco18s.harderores.enchantment.PulverizeEnchantment;
import com.draco18s.harderores.enchantment.ShatterEnchantment;
import com.draco18s.harderores.enchantment.VeinCrackerEnchantment;
import com.draco18s.harderores.entity.AxelTileEntity;
import com.draco18s.harderores.entity.MillstoneTileEntity;
import com.draco18s.harderores.entity.PackagerTileEntity;
import com.draco18s.harderores.entity.SifterTileEntity;
import com.draco18s.harderores.entity.SluiceTileEntity;
import com.draco18s.harderores.integration.FlowerIntegration;
import com.draco18s.harderores.inventory.PackagerContainer;
import com.draco18s.harderores.inventory.SifterContainer;
import com.draco18s.harderores.item.HardOreItem;
import com.draco18s.harderores.item.ModItemTier;
import com.draco18s.harderores.loot.function.BlockItemFunction;
import com.draco18s.harderores.loot.function.HarderSetCount;
import com.draco18s.harderores.network.PacketHandler;
import com.draco18s.harderores.proxy.ClientProxy;
import com.draco18s.harderores.proxy.ServerProxy;
import com.draco18s.harderores.recipe.OreProcessingRecipes;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.proxy.IProxy;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@Mod(HarderOres.MODID)
public class HarderOres {
	public static final String MODID = "harderores";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final IProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

	public HarderOres() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener((FMLCommonSetupEvent event) -> {
			LootFunctionManager.registerFunction(new BlockItemFunction.Serializer());
			LootFunctionManager.registerFunction(new HarderSetCount.Serializer());
		});

		modEventBus.addListener((FMLLoadCompleteEvent event) -> {
			HardLibAPI.oreMachines.addSiftRecipe(() -> ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/tiny/iron")), 8, new ItemStack(HarderOres.ModItems.largedust_iron,1));
			HardLibAPI.oreMachines.addSiftRecipe(() -> ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/tiny/gold")), 8, new ItemStack(HarderOres.ModItems.largedust_gold,1));

			HardLibAPI.oreMachines.addMillRecipe(() -> ItemTags.getCollection().getOrCreate(new ResourceLocation("harderores","chunks/iron")), new ItemStack(HarderOres.ModItems.tinydust_iron,2));
			HardLibAPI.oreMachines.addMillRecipe(() -> ItemTags.getCollection().getOrCreate(new ResourceLocation("harderores","chunks/gold")), new ItemStack(HarderOres.ModItems.tinydust_gold,2));

			FlowerIntegration.registerFlowerGen();
			replaceOreGenerators();
		});
		PacketHandler.register();
		HardLibAPI.oreMachines = new OreProcessingRecipes();
		HardLibAPI.hardOres = new OreBlockInfo();

		Block block = new LimoniteBlock(Block.Properties.create(Material.EARTH).hardnessAndResistance(3, 1).harvestTool(ToolType.SHOVEL).harvestLevel(0).sound(SoundType.WET_GRASS));
		EasyRegistry.registerBlock(block, "ore_limonite", new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		block = new MillstoneBlock();
		EasyRegistry.registerBlock(block, "millstone", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(MillstoneTileEntity::new, block), HarderOres.MODID, "millstone");
		block = new AxelBlock();
		EasyRegistry.registerBlock(block, "axel", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(AxelTileEntity::new, block), HarderOres.MODID, "axel");
		block = new WindvaneBlock();
		EasyRegistry.registerBlock(block, "windvane", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SluiceBlock();
		EasyRegistry.registerBlock(block, "sluice", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(SluiceTileEntity::new, block), HarderOres.MODID, "sluice");

		block = new HardOreBlock(1, new Color(0xd8af93), Block.Properties.create(Material.ROCK).hardnessAndResistance(Blocks.IRON_ORE.getDefaultState().getBlockHardness(null, null)*2, 5).harvestTool(ToolType.PICKAXE).harvestLevel(1).sound(SoundType.STONE));
		EasyRegistry.registerBlockWithVariants(block, "ore_hardiron", BlockProperties.ORE_DENSITY, HardOreItem::new, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		block = new HardOreBlock(3, new Color(0x5decf5), Block.Properties.create(Material.ROCK).hardnessAndResistance(Blocks.DIAMOND_ORE.getDefaultState().getBlockHardness(null, null)*4, 5).harvestTool(ToolType.PICKAXE).harvestLevel(2).sound(SoundType.STONE));
		EasyRegistry.registerBlockWithVariants(block, "ore_harddiamond", BlockProperties.ORE_DENSITY, HardOreItem::new, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		block = new HardOreBlock(1, new Color(0xfacf3b), Block.Properties.create(Material.ROCK).hardnessAndResistance(Blocks.GOLD_ORE.getDefaultState().getBlockHardness(null, null)*2, 5).harvestTool(ToolType.PICKAXE).harvestLevel(2).sound(SoundType.STONE));
		EasyRegistry.registerBlockWithVariants(block, "ore_hardgold", BlockProperties.ORE_DENSITY, HardOreItem::new, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));

		block = new SluiceOutput();
		EasyRegistry.registerBlock(block, "sluice_output");

		Item item = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(item, "orechunk_limonite");

		Item itemChunk = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemChunk, "orechunk_iron");
		Item itemTiny = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemTiny, "tinydust_iron");
		Item itemPile = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemPile, "largedust_iron");

		itemChunk = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemChunk, "orechunk_gold");
		itemTiny = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemTiny, "tinydust_gold");
		itemPile = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemPile, "largedust_gold");

		itemChunk = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemChunk, "orechunk_diamond");

		block = new SifterBlock();
		EasyRegistry.registerBlock(block, "sifter", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(SifterTileEntity::new, block), HarderOres.MODID, "sifter");
		EasyRegistry.registerOther(IForgeContainerType.create(SifterContainer::new), new ResourceLocation(HarderOres.MODID,"sifter"));

		block = new PackagerBlock();
		EasyRegistry.registerBlock(block, "packager", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(PackagerTileEntity::new, block), HarderOres.MODID, "packager");
		EasyRegistry.registerOther(IForgeContainerType.create(PackagerContainer::new), new ResourceLocation(HarderOres.MODID,"packager"));

		item = new PickaxeItem(ModItemTier.DIAMOND_STUD, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS));
		EasyRegistry.registerItem(item, "diamondstud_pickaxe");
		item = new ShovelItem(ModItemTier.DIAMOND_STUD, 1.5F, -3.0F, new Item.Properties().group(ItemGroup.TOOLS));
		EasyRegistry.registerItem(item, "diamondstud_shovel");
		item = new HoeItem(ModItemTier.DIAMOND_STUD, 0.0F, (new Item.Properties()).group(ItemGroup.TOOLS));
		EasyRegistry.registerItem(item, "diamondstud_hoe");
		item = new AxeItem(ModItemTier.DIAMOND_STUD, 5.0F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS));
		EasyRegistry.registerItem(item, "diamondstud_axe");

		EquipmentSlotType[] slots = new EquipmentSlotType[] { EquipmentSlotType.OFFHAND };
		Enchantment ench = new ProspectorEnchantment(slots);
		EasyRegistry.registerOther(ench, new ResourceLocation(HarderOres.MODID,"prospector"));
		slots = new EquipmentSlotType[] { EquipmentSlotType.MAINHAND };
		ench = new VeinCrackerEnchantment(slots);
		EasyRegistry.registerOther(ench, new ResourceLocation(HarderOres.MODID,"cracker"));
		slots = new EquipmentSlotType[] { EquipmentSlotType.MAINHAND };
		ench = new ShatterEnchantment(slots);
		EasyRegistry.registerOther(ench, new ResourceLocation(HarderOres.MODID,"shatter"));
		slots = new EquipmentSlotType[] { EquipmentSlotType.MAINHAND };
		ench = new PulverizeEnchantment(slots);
		EasyRegistry.registerOther(ench, new ResourceLocation(HarderOres.MODID,"pulverize"));

	}

	private void replaceOreGenerators() {
		replaceGenerator(Blocks.IRON_ORE, ModBlocks.ore_hardiron);
	}

	@SuppressWarnings("unused")
	private void replaceGenerator(Block vanillaOre, Block replacementOre) {
		Iterator<Biome> list = ForgeRegistries.BIOMES.iterator();
		while(list.hasNext()) {
			OreFeatureConfig oreConfig = null;
			ConfiguredPlacement<?> placementConfig = null;
			Biome biome = list.next();
			Iterator<ConfiguredFeature<?>> it = biome.getFeatures(Decoration.UNDERGROUND_ORES).iterator();
			while(it.hasNext()) {
				ConfiguredFeature<?> feature = it.next();
				if(feature.config instanceof DecoratedFeatureConfig) {
					DecoratedFeatureConfig dfconfig = (DecoratedFeatureConfig)feature.config;
					if(dfconfig.feature.config instanceof OreFeatureConfig) {
						OreFeatureConfig oreConfigl = (OreFeatureConfig)dfconfig.feature.config;
						if(oreConfigl.state.getBlock() == vanillaOre) {
							oreConfig = oreConfigl;
							placementConfig = (ConfiguredPlacement<?>)dfconfig.decorator;
							HarderOres.LOGGER.log(Level.DEBUG, "Replacing " + vanillaOre.getRegistryName() + " ore generator.");
							it.remove();
							break;
						}
					}
				}
			}
			if(oreConfig != null) {
				biome.addFeature(Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(
						Feature.ORE, oreConfig, (Placement<IPlacementConfig>)placementConfig.decorator, (IPlacementConfig)placementConfig.config
						));
				HarderOres.LOGGER.log(Level.DEBUG, "Replaced!");
			}
		}
	}

	@ObjectHolder(HarderOres.MODID)
	public static class ModBlocks {
		public static final Block axel = null;
		public static final Block millstone = null;
		public static final Block windvane = null;
		public static final Block sluice = null;
		public static final Block sluice_output = null;
		public static final Block ore_hardiron = null;
	}

	@ObjectHolder(HarderOres.MODID)
	public static class ModItems {
		public static final Item orechunk_diamond = null;
		public static final Item largedust_iron = null;
		public static final Item largedust_gold = null;
		public static final Item tinydust_iron = null;
		public static final Item tinydust_gold = null;
	}

	@ObjectHolder(HarderOres.MODID)
	public static class ModTileEntities {
		public static final TileEntityType<MillstoneTileEntity> millstone = null;
		public static final TileEntityType<AxelTileEntity> axel = null;
		public static final TileEntityType<SifterTileEntity> sifter = null;
		public static final TileEntityType<SifterTileEntity> packager = null;
		public static final TileEntityType<SluiceTileEntity> sluice = null;
	}

	@ObjectHolder(HarderOres.MODID)
	public static class ModContainerTypes {
		public static final ContainerType<SifterContainer> sifter = null;
		public static final ContainerType<PackagerContainer> packager = null;
	}

	@ObjectHolder(HarderOres.MODID)
	public static class ModEnchantments {
		public static final Enchantment prospector = null;
		public static final Enchantment shatter = null;
		public static final Enchantment pulverize = null;
		public static final Enchantment cracker = null;
	}

	public static class ModItemTags {
		//public static Tag<Item> TINY_IRON_DUST = new ItemTags.Wrapper(new ResourceLocation("forge", "ingots/iron"));
		public static final Tag<Item> STONE_ANY = new ItemTags.Wrapper(new ResourceLocation("hardlib", "stoneany"));
	}
}