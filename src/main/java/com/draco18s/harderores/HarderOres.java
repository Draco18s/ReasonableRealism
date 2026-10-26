package com.draco18s.harderores;

import java.awt.Color;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.draco18s.harderores.block.AxelBlock;
import com.draco18s.harderores.block.MillstoneBlock;
import com.draco18s.harderores.block.PackagerBlock;
import com.draco18s.harderores.block.SifterBlock;
import com.draco18s.harderores.block.SluiceBlock;
import com.draco18s.harderores.block.WindvaneBlock;
import com.draco18s.harderores.block.ore.HardOreBlock;
import com.draco18s.harderores.block.ore.LimoniteBlock;
import com.draco18s.harderores.entity.AxelBlockEntity;
import com.draco18s.harderores.entity.MillstoneBlockEntity;
import com.draco18s.harderores.entity.PackagerBlockEntity;
import com.draco18s.harderores.entity.SifterBlockEntity;
import com.draco18s.harderores.entity.SluiceBlockEntity;
import com.draco18s.harderores.inventory.PackagerContainerMenu;
import com.draco18s.harderores.inventory.SifterContainerMenu;
import com.draco18s.harderores.item.HardOreItem;
import com.draco18s.harderores.loot.function.HarderSetCount;
import com.draco18s.harderores.network.PacketHandler;
import com.draco18s.harderores.proxy.ClientProxy;
import com.draco18s.harderores.recipe.GrindingRecipe;
import com.draco18s.harderores.recipe.OreProcessingRecipes;
import com.draco18s.harderores.recipe.PackingRecipe;
import com.draco18s.harderores.recipe.SiftingRecipe;
import com.draco18s.hardlib.CogHelper;
import com.draco18s.hardlib.EasyRegistry.IBlockItemFactory;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.ObjectHolder;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.interfaces.IMechanicalPower;
import com.draco18s.hardlib.proxy.IProxy;
import com.draco18s.hardlib.proxy.ServerProxy;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod(HarderOres.MODID)
@EventBusSubscriber(modid = HarderOres.MODID, bus = EventBusSubscriber.Bus.MOD)
public class HarderOres {
	public static final String MODID = "harderores";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final IProxy PROXY = FMLEnvironment.dist.isClient() ? new ClientProxy() : new ServerProxy();

	@SuppressWarnings("deprecation")
	public HarderOres() {
		registerCreativeTab();
		/*final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener((FMLLoadCompleteEvent event) -> {
			FlowerIntegration.registerFlowerGen();
			replaceOreGenerators();
		});*/
		CogHelper.addCogModule("HarderVanillaOres.xml");
		CogHelper.addCogModule("HarderLimonite.xml");
		HardLibAPI.oreMachines = new OreProcessingRecipes();
		HardLibAPI.hardOres = new OreBlockInfo();
		EasyRegistry.registerBlock(() -> new LimoniteBlock(Block.Properties.of().mapColor(MapColor.DIRT).strength(3, 1).sound(SoundType.WET_GRASS).requiresCorrectToolForDrops()), getRL("ore_limonite"), new Item.Properties());
		
		IBlockItemFactory<BlockItem> blockItemFactory = (Block blockIn, Item.Properties builder) -> new HardOreItem(blockIn, builder);
		float hardMult = 1.5f;
		float resistMult = 1.0f;
		
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(2, new Color(0x3a685a), Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(Blocks.COPPER_ORE.defaultDestroyTime() * hardMult, Blocks.COPPER_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_hardcopper"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(5, new Color(0x5decf5), Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(Blocks.DIAMOND_ORE.defaultDestroyTime() * hardMult, Blocks.DIAMOND_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_harddiamond"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(2, new Color(0xfacf3b), Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(Blocks.GOLD_ORE.defaultDestroyTime() * hardMult, Blocks.GOLD_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_hardgold"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(2, new Color(0xd8af93), Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(Blocks.IRON_ORE.defaultDestroyTime() * hardMult, Blocks.IRON_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_hardiron"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(1, new Color(0x3a685a), Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(Blocks.DEEPSLATE_COPPER_ORE.defaultDestroyTime() * hardMult, Blocks.DEEPSLATE_COPPER_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_harddeepslate_copper"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(3, new Color(0x5decf5), Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(Blocks.DEEPSLATE_DIAMOND_ORE.defaultDestroyTime() * hardMult, Blocks.DEEPSLATE_DIAMOND_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_harddeepslate_diamond"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(1, new Color(0xfacf3b), Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(Blocks.DEEPSLATE_GOLD_ORE.defaultDestroyTime() * hardMult, Blocks.DEEPSLATE_GOLD_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_harddeepslate_gold"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(1, new Color(0xd8af93), Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(Blocks.DEEPSLATE_IRON_ORE.defaultDestroyTime() * hardMult, Blocks.DEEPSLATE_IRON_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_harddeepslate_iron"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());

		DeferredHolder<Block, Block> sluice = EasyRegistry.registerBlock(SluiceBlock::new, getRL("sluice"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(SluiceBlockEntity::new, sluice.get()).build(null), getRL("sluice"));
		
		String[] itemNames = {
				// Hard ores drop vanilla raw ore (raw_iron...); diamond has no raw item, so it keeps its chunk.
				// Limonite is a poor surface iron ore (swamps, shovel); its chunk smelts into raw iron.
				"orechunk_limonite",
				"tinydust_copper", "largedust_copper", "copper_nugget",
				"orechunk_diamond",
				"tinydust_gold", "largedust_gold",
				"tinydust_iron", "largedust_iron",
		};
		for(String it : itemNames) {
			EasyRegistry.registerItem(() -> new Item(new Item.Properties()), getRL(it));
		}

		EasyRegistry.registerItem(() -> new PickaxeItem(ModItemTiers.DIAMOND_STUD, new Item.Properties().attributes(DiggerItem.createAttributes(ModItemTiers.DIAMOND_STUD, 1, -2.8F))), getRL("diamond_studded_pick"));
		EasyRegistry.registerItem(() -> new AxeItem(ModItemTiers.DIAMOND_STUD, new Item.Properties().attributes(DiggerItem.createAttributes(ModItemTiers.DIAMOND_STUD, 1.5F, -3.0F))), getRL("diamond_studded_axe"));
		EasyRegistry.registerItem(() -> new ShovelItem(ModItemTiers.DIAMOND_STUD, new Item.Properties().attributes(DiggerItem.createAttributes(ModItemTiers.DIAMOND_STUD, 5.0F, -3.0F))), getRL("diamond_studded_shovel"));
		EasyRegistry.registerItem(() -> new HoeItem(ModItemTiers.DIAMOND_STUD, new Item.Properties().attributes(DiggerItem.createAttributes(ModItemTiers.DIAMOND_STUD, 2, -0.5F))), getRL("diamond_studded_hoe"));
		
		DeferredHolder<Block, Block> sifter = EasyRegistry.registerBlock(SifterBlock::new, getRL("machine_sifter"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(SifterBlockEntity::new, sifter.get()).build(null), getRL("machine_sifter"));
		EasyRegistry.registerMenuType(getRL("machine_sifter"),() -> new MenuType<>(SifterContainerMenu::new, FeatureFlags.DEFAULT_FLAGS));
		
		DeferredHolder<Block, Block> millstone = EasyRegistry.registerBlock(MillstoneBlock::new, getRL("machine_millstone"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(MillstoneBlockEntity::new, millstone.get()).build(null), getRL("machine_millstone"));
		DeferredHolder<Block, Block> axel = 
		EasyRegistry.registerBlock(AxelBlock::new, getRL("machine_axel"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(AxelBlockEntity::new, axel.get()).build(null), getRL("machine_axel"));
		EasyRegistry.registerBlock(WindvaneBlock::new, getRL("machine_windvane"), new Item.Properties());
		DeferredHolder<Block, Block> packager = EasyRegistry.registerBlock(PackagerBlock::new, getRL("packager"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(PackagerBlockEntity::new, packager.get()).build(null), getRL("packager"));
		EasyRegistry.registerMenuType(getRL("packager"),() -> new MenuType<>(PackagerContainerMenu::new, FeatureFlags.DEFAULT_FLAGS));
		
		
		EasyRegistry.registerOther(Registries.PARTICLE_TYPE, new Tuple<ResourceLocation,Supplier<ParticleType<?>>>(getRL("prospector_dust"), () -> getBlockParticleType(false)));
		EasyRegistry.registerOther(Registries.PARTICLE_TYPE, new Tuple<ResourceLocation,Supplier<ParticleType<?>>>(getRL("prospector_radar"), () -> getBlockParticleType(false)));
		
		EasyRegistry.registerOther(Registries.RECIPE_TYPE, new Tuple<ResourceLocation,Supplier<RecipeType<?>>>(getRL("sifting"), () -> RecipeType.simple(getRL("sifting"))));
		EasyRegistry.registerOther(Registries.RECIPE_SERIALIZER, new Tuple<ResourceLocation,Supplier<RecipeSerializer<?>>>(getRL("sifting"), () -> new SiftingRecipe.Serializer()));
		EasyRegistry.registerOther(Registries.RECIPE_TYPE, new Tuple<ResourceLocation,Supplier<RecipeType<?>>>(getRL("grinding"), () -> RecipeType.simple(getRL("grinding"))));
		EasyRegistry.registerOther(Registries.RECIPE_SERIALIZER, new Tuple<ResourceLocation,Supplier<RecipeSerializer<?>>>(getRL("grinding"), () -> new GrindingRecipe.Serializer()));
		EasyRegistry.registerOther(Registries.RECIPE_TYPE, new Tuple<ResourceLocation,Supplier<RecipeType<?>>>(getRL("packing"), () -> RecipeType.simple(getRL("packing"))));
		EasyRegistry.registerOther(Registries.RECIPE_SERIALIZER, new Tuple<ResourceLocation,Supplier<RecipeSerializer<?>>>(getRL("packing"), () -> new PackingRecipe.Serializer()));
		
		LootFunctions.harderSetCountReg = EasyRegistry.registerOther(Registries.LOOT_FUNCTION_TYPE, new Tuple<ResourceLocation,Supplier<LootItemFunctionType<?>>>(
				getRL("set_count"),() -> new LootItemFunctionType<>(HarderSetCount.CODEC)));
	}
	
	/** A particle type carrying a block state (as vanilla's block particles do). */
	private static ParticleType<BlockParticleOption> getBlockParticleType(boolean overrideLimiter) {
		return new ParticleType<BlockParticleOption>(overrideLimiter) {
			@Override
			public MapCodec<BlockParticleOption> codec() {
				return BlockParticleOption.codec(this);
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, BlockParticleOption> streamCodec() {
				return BlockParticleOption.streamCodec(this);
			}
		};
	}

	public static class LootFunctions {
		public static DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<?>> harderSetCountReg;
	}
	
	public static class ModItemTiers {
		public static final Supplier<Ingredient> stud_repair_item = () -> Ingredient.of(ModItems.orechunk_diamond);
		public static final Tier DIAMOND_STUD = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 750, 7f, 2f, 5, stud_repair_item);
	}
	
	public static class RecipeTypes {
		@ObjectHolder(registryName = "minecraft:recipe_type", value = MODID+":"+"sifting")
		public static RecipeType<SiftingRecipe> SIFTING;
		@ObjectHolder(registryName = "minecraft:recipe_type", value = MODID+":"+"grinding")
		public static RecipeType<GrindingRecipe> GRINDING;
		@ObjectHolder(registryName = "minecraft:recipe_type", value = MODID+":"+"packing")
		public static RecipeType<PackingRecipe> PACKING;
	}
	
	/** Recipe books of the sifter and packager screens (added to the enum by META-INF/enumextensions.json). */
	public static class RecipeBookTypes {
		public static final RecipeBookType SIFTER = RecipeBookType.valueOf("HARDERORES_SIFTER");
		public static final RecipeBookType PACKAGER = RecipeBookType.valueOf("HARDERORES_PACKAGER");
	}

	public static class RecipeSerializers {
		@ObjectHolder(registryName = "minecraft:recipe_serializer", value = MODID+":"+"sifting")
		public static RecipeSerializer<?> SIFTING;
		@ObjectHolder(registryName = "minecraft:recipe_serializer", value = MODID+":"+"grinding")
		public static RecipeSerializer<?> GRINDING;
		@ObjectHolder(registryName = "minecraft:recipe_serializer", value = MODID+":"+"packing")
		public static RecipeSerializer<?> PACKING;
	}
	
	/*public HarderOres() {
		block = new PackagerBlock();
		EasyRegistry.registerBlock(block, "packager", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(PackagerTileEntity::new, block), HarderOres.MODID, "packager");
		EasyRegistry.registerOther(IForgeContainerType.create(PackagerContainer::new), getRL("packager"));
	}*/

	/*private void replaceOreGenerators() {
		replaceGenerator(Blocks.IRON_ORE, ModBlocks.ore_hardiron);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
							it.remove();
							break;
						}
					}
				}
			}
			if(oreConfig != null) {
				//Feature feat = new HardOreFeature()
				Feature feat = ModFeatures.hardOre;
				//Feature feat2 = Registry.FEATURE.getValue(new ResourceLocation(HarderOres.MODID,"hardore")).orElse(null);
				OreFeatureConfig newConf = new OreFeatureConfig(oreConfig.target, replacementOre.getDefaultState(), oreConfig.size*2);
				biome.addFeature(Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(
						feat, newConf, (Placement<IPlacementConfig>)placementConfig.decorator, (IPlacementConfig)placementConfig.config
						));
				//HarderOres.LOGGER.log(Level.DEBUG, "Replaced with " + feat.getRegistryName() + "!");
			}
			biome.addStructure((Structure<OreVeinStructureConfig>)ModFeatures.ore_vein, new OreVeinStructureConfig(0.004));
			biome.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, Biome.createDecoratedFeature((Feature<OreVeinStructureConfig>)ModFeatures.ore_vein, new OreVeinStructureConfig(0.004), Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
		}
	}*/
	
	public static class Tags {
		public static class Blocks {
			//public static final TagKey<Block> GRANULAR = new TagKey<Block>(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(MODID, "granular"));
		}
		public static class Items {
			public static final TagKey<Item> GRANULAR = new TagKey<Item>(Registries.ITEM, getRL("granular"));
			public static TagKey<Item> ORE_CHUNKS = ItemTags.create(HardLib.getRL("c", "raw_materials/nuggets"));
			public static TagKey<Item> TINY_ORE_DUSTS = new TagKey<Item>(Registries.ITEM, HardLib.getRL("c", "dust/tiny/ore"));
			public static TagKey<Item> TINY_COPPER_DUST = new TagKey<Item>(Registries.ITEM, HardLib.getRL("c", "dust/tiny/copper"));
			public static TagKey<Item> TINY_GOLD_DUST = new TagKey<Item>(Registries.ITEM, HardLib.getRL("c", "dust/tiny/gold"));
			public static TagKey<Item> TINY_IRON_DUST = new TagKey<Item>(Registries.ITEM, HardLib.getRL("c", "dust/tiny/iron"));
		}
	}

	public static class ModBlocks {
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"machine_axel")
		public static Block machine_axel;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"machine_millstone")
		public static Block machine_millstone;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"machine_windvane")
		public static Block machine_windvane;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"machine_sifter")
		public static Block machine_sifter;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"packager")
		public static Block packager;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_harddiamond")
		public static Block ore_harddiamond;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_hardcopper")
		public static Block ore_hardcopper;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_hardiron")
		public static Block ore_hardiron;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_hardgold")
		public static Block ore_hardgold;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_limonite")
		public static Block ore_limonite;

		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_harddeepslate_copper")
		public static Block ore_harddeepslate_copper;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_harddeepslate_diamond")
		public static Block ore_harddeepslate_diamond;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_harddeepslate_iron")
		public static Block ore_harddeepslate_iron;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_harddeepslate_gold")
		public static Block ore_harddeepslate_gold;

		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"sluice")
		public static Block sluice;
		public static final Block sluice_output = null;
	}

	public static class ModItems {
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"diamond_studded_pick")
		public static Item diamond_studded_pick;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"diamond_studded_axe")
		public static Item diamond_studded_axe;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"diamond_studded_shovel")
		public static Item diamond_studded_shovel;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"diamond_studded_hoe")
		public static Item diamond_studded_hoe;
		
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"orechunk_diamond")
		public static Item orechunk_diamond;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"orechunk_limonite")
		public static Item orechunk_limonite;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"largedust_copper")
		public static Item largedust_copper;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"largedust_iron")
		public static Item largedust_iron;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"largedust_gold")
		public static Item largedust_gold;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_copper")
		public static Item tinydust_copper;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_iron")
		public static Item tinydust_iron;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_gold")
		public static Item tinydust_gold;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"copper_nugget")
		public static Item copper_nugget;
		
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"sluice")
		public static Item sluice;
	}

	public static class ModBlockEntities {
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"machine_sifter")
		public static BlockEntityType<SifterBlockEntity> machine_sifter;
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"machine_millstone")
		public static BlockEntityType<MillstoneBlockEntity> machine_millstone;
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"machine_axel")
		public static BlockEntityType<AxelBlockEntity> machine_axel;
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"sluice")
		public static BlockEntityType<SluiceBlockEntity> sluice;
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"packager")
		public static BlockEntityType<PackagerBlockEntity> packager;
	}

	public static class ModParticleTypes {
		@ObjectHolder(registryName = "minecraft:particle_type", value = MODID+":"+"prospector_dust")
		public static ParticleType<BlockParticleOption> prospector_dust;
		@ObjectHolder(registryName = "minecraft:particle_type", value = MODID+":"+"prospector_radar")
		public static ParticleType<BlockParticleOption> prospector_radar;
	}

	public static class ModContainerTypes {
		@ObjectHolder(registryName = "minecraft:menu", value = MODID+":"+"machine_sifter")
		public static MenuType<SifterContainerMenu> machine_sifter;
		@ObjectHolder(registryName = "minecraft:menu", value = MODID+":"+"packager")
		public static MenuType<PackagerContainerMenu> packager;
	}

	/** Data-driven since 1.21 (data/harderores/enchantment); read levels with HardLib.getEnchantmentLevel. */
	public static class ModEnchantments {
		public static final ResourceKey<Enchantment> prospector = key("prospector");
		public static final ResourceKey<Enchantment> cracker = key("cracker");
		/**
		 * Ore Breaker
		 */
		public static final ResourceKey<Enchantment> shatter = key("shatter");
		public static final ResourceKey<Enchantment> pulverize = key("pulverize");

		private static ResourceKey<Enchantment> key(String name) {
			return ResourceKey.create(Registries.ENCHANTMENT, getRL(name));
		}
	}

	public static class ModFeatures {
		//public static final Feature<?> hardOre = null;
		//public static final Structure<?> ore_vein = null;
	}
	
	public static class ModStructurePieceTypes {
		//public static IStructurePieceType OVMotherLoad;
		//public static IStructurePieceType OreVein;
	}
	
	/** Everything Harder Ores adds, including the hard ores (at full density) and books of its enchantments. */
	private static void registerCreativeTab() {
		EasyRegistry.registerCreativeTab(getRL("creative_tab"), builder -> builder
			.title(Component.translatable("itemGroup." + MODID))
			.icon(() -> new ItemStack(ModBlocks.machine_millstone))
			.displayItems((params, output) -> {
				output.accept(ModBlocks.machine_millstone);
				output.accept(ModBlocks.machine_axel);
				output.accept(ModBlocks.machine_windvane);
				output.accept(ModBlocks.machine_sifter);
				output.accept(ModBlocks.packager);
				output.accept(ModBlocks.sluice);
				for(Block ore : new Block[] {ModBlocks.ore_hardiron, ModBlocks.ore_hardgold, ModBlocks.ore_hardcopper, ModBlocks.ore_harddiamond,
						ModBlocks.ore_harddeepslate_iron, ModBlocks.ore_harddeepslate_gold, ModBlocks.ore_harddeepslate_copper, ModBlocks.ore_harddeepslate_diamond}) {
					output.accept(HardOreBlock.setNbtOnStack(new ItemStack(ore), BlockProperties.ORE_DENSITY, 16));
				}
				output.accept(ModBlocks.ore_limonite);
				output.accept(ModItems.orechunk_limonite);
				output.accept(ModItems.tinydust_iron);
				output.accept(ModItems.largedust_iron);
				output.accept(ModItems.tinydust_gold);
				output.accept(ModItems.largedust_gold);
				output.accept(ModItems.tinydust_copper);
				output.accept(ModItems.largedust_copper);
				output.accept(ModItems.copper_nugget);
				output.accept(ModItems.orechunk_diamond);
				output.accept(ModItems.diamond_studded_pick);
				output.accept(ModItems.diamond_studded_axe);
				output.accept(ModItems.diamond_studded_shovel);
				output.accept(ModItems.diamond_studded_hoe);
				params.holders().lookup(Registries.ENCHANTMENT).ifPresent(enchantments -> {
					for(ResourceKey<Enchantment> key : List.of(ModEnchantments.shatter, ModEnchantments.cracker, ModEnchantments.pulverize, ModEnchantments.prospector)) {
						enchantments.get(key).ifPresent(ench -> {
							for(int lv = 1; lv <= ench.value().getMaxLevel(); lv++) {
								output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, lv)));
							}
						});
					}
				});
			}));
	}
	
	@SubscribeEvent
	public static void registerPayloads(RegisterPayloadHandlersEvent event) {
		PacketHandler.register(event);
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.machine_sifter, SifterBlockEntity::getItemHandler);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.packager, PackagerBlockEntity::getItemHandler);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.machine_millstone, MillstoneBlockEntity::getItemHandler);
		event.registerBlockEntity(IMechanicalPower.MECHANICAL_POWER_CAPABILITY, ModBlockEntities.machine_millstone, MillstoneBlockEntity::getMechanicalPower);
		event.registerBlockEntity(IMechanicalPower.MECHANICAL_POWER_CAPABILITY, ModBlockEntities.machine_axel, AxelBlockEntity::getMechanicalPower);
	}

	@SubscribeEvent
	public static void addItemsToCreativeTab(final BuildCreativeModeTabContentsEvent event)
    {
		ResourceKey<CreativeModeTab> tab = event.getTabKey();
		if (tab == CreativeModeTabs.NATURAL_BLOCKS) {
			event.accept(new ItemStack(ModBlocks.ore_limonite, 1));
		}
        if (tab == CreativeModeTabs.BUILDING_BLOCKS) {
        	
        }
        if(tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
        	event.accept(ModBlocks.machine_millstone);
        	event.accept(ModBlocks.machine_axel);
        	event.accept(ModBlocks.machine_windvane);
        	event.accept(ModBlocks.machine_sifter);
        	event.accept(ModBlocks.packager);
        	event.accept(ModBlocks.sluice);
        }
        if (tab == CreativeModeTabs.INGREDIENTS) {
        	event.accept(new ItemStack(ModItems.orechunk_limonite, 1));
        	event.accept(new ItemStack(ModItems.tinydust_copper, 1));
        	event.accept(new ItemStack(ModItems.largedust_copper, 1));
        	event.accept(new ItemStack(ModItems.copper_nugget, 1));
        	event.accept(new ItemStack(ModItems.tinydust_gold, 1));
        	event.accept(new ItemStack(ModItems.largedust_gold, 1));
        	event.accept(new ItemStack(ModItems.tinydust_iron, 1));
        	event.accept(new ItemStack(ModItems.largedust_iron, 1));
        	event.accept(new ItemStack(ModItems.orechunk_diamond, 1));
        }
    }

	public static ResourceLocation getRL(String name) {
		return HardLib.getRL(MODID, name);
	}
}