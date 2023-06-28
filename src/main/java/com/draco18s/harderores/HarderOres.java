package com.draco18s.harderores;

import java.awt.Color;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.draco18s.harderores.block.AxelBlock;
import com.draco18s.harderores.block.MillstoneBlock;
import com.draco18s.harderores.block.SifterBlock;
import com.draco18s.harderores.block.WindvaneBlock;
import com.draco18s.harderores.block.ore.HardOreBlock;
import com.draco18s.harderores.block.ore.LimoniteBlock;
import com.draco18s.harderores.enchantment.ProspectorEnchantment;
import com.draco18s.harderores.enchantment.PulverizeEnchantment;
import com.draco18s.harderores.enchantment.ShatterEnchantment;
import com.draco18s.harderores.enchantment.VeinCrackerEnchantment;
import com.draco18s.harderores.entity.AxelBlockEntity;
import com.draco18s.harderores.entity.MillstoneBlockEntity;
import com.draco18s.harderores.entity.SifterBlockEntity;
import com.draco18s.harderores.inventory.SifterContainerMenu;
import com.draco18s.harderores.item.HardOreItem;
import com.draco18s.harderores.loot.function.HarderSetCount;
import com.draco18s.harderores.network.PacketHandler;
import com.draco18s.harderores.proxy.ClientProxy;
import com.draco18s.harderores.recipe.OreProcessingRecipes;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.EasyRegistry.IBlockItemFactory;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.proxy.IProxy;
import com.draco18s.hardlib.proxy.ServerProxy;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

@Mod(HarderOres.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class HarderOres {
	public static final String MODID = "harderores";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final IProxy PROXY = DistExecutor.safeRunForDist(()->ClientProxy::new, ()->ServerProxy::new);

	@SuppressWarnings("deprecation")
	public HarderOres() {
		/*final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener((FMLLoadCompleteEvent event) -> {
			FlowerIntegration.registerFlowerGen();
			replaceOreGenerators();
		});*/
		PacketHandler.register();
		HardLibAPI.oreMachines = new OreProcessingRecipes();
		//HardLibAPI.hardOres = new OreBlockInfo();
		EasyRegistry.registerBlock(() -> new LimoniteBlock(Block.Properties.of(Material.DIRT).strength(3, 1).sound(SoundType.WET_GRASS).requiresCorrectToolForDrops()), getRL("ore_limonite"), new Item.Properties());
		
		IBlockItemFactory<BlockItem> blockItemFactory = (Block blockIn, Item.Properties builder) -> new HardOreItem(blockIn, builder);
		float hardMult = 1.5f;
		float resistMult = 1.0f;
		
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(2, new Color(0x3a685a), Block.Properties.of(Material.STONE).strength(Blocks.COPPER_ORE.defaultDestroyTime() * hardMult, Blocks.COPPER_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_hardcopper"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(5, new Color(0x5decf5), Block.Properties.of(Material.STONE).strength(Blocks.DIAMOND_ORE.defaultDestroyTime() * hardMult, Blocks.DIAMOND_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_harddiamond"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(2, new Color(0xfacf3b), Block.Properties.of(Material.STONE).strength(Blocks.GOLD_ORE.defaultDestroyTime() * hardMult, Blocks.GOLD_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_hardgold"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(2, new Color(0xd8af93), Block.Properties.of(Material.STONE).strength(Blocks.IRON_ORE.defaultDestroyTime() * hardMult, Blocks.IRON_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_hardiron"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(1, new Color(0x3a685a), Block.Properties.of(Material.STONE).strength(Blocks.DEEPSLATE_COPPER_ORE.defaultDestroyTime() * hardMult, Blocks.DEEPSLATE_COPPER_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_harddeepslate_copper"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(3, new Color(0x5decf5), Block.Properties.of(Material.STONE).strength(Blocks.DEEPSLATE_DIAMOND_ORE.defaultDestroyTime() * hardMult, Blocks.DEEPSLATE_DIAMOND_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_harddeepslate_diamond"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(1, new Color(0xfacf3b), Block.Properties.of(Material.STONE).strength(Blocks.DEEPSLATE_GOLD_ORE.defaultDestroyTime() * hardMult, Blocks.DEEPSLATE_GOLD_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_harddeepslate_gold"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());
		EasyRegistry.registerBlockWithVariants(() -> new HardOreBlock(1, new Color(0xd8af93), Block.Properties.of(Material.STONE).strength(Blocks.DEEPSLATE_IRON_ORE.defaultDestroyTime() * hardMult, Blocks.DEEPSLATE_IRON_ORE.getExplosionResistance() * resistMult).sound(SoundType.STONE).requiresCorrectToolForDrops()), getRL("ore_harddeepslate_iron"), BlockProperties.ORE_DENSITY, blockItemFactory, new Item.Properties());

		String[] itemNames = {
				"orechunk_limonite",
				"orechunk_copper", "tinydust_copper", "largedust_copper", "copper_nugget",
				"orechunk_diamond",
				"orechunk_gold", "tinydust_gold", "largedust_gold",
				"orechunk_iron", "tinydust_iron", "largedust_iron",
		};
		for(String it : itemNames) {
			EasyRegistry.registerItem(() -> new Item(new Item.Properties()), getRL(it));
		}

		EasyRegistry.registerItem(() -> new PickaxeItem(ModItemTiers.DIAMOND_STUD, 1, -2.8F, new Item.Properties()), getRL("diamond_studded_pick"));
		EasyRegistry.registerItem(() -> new AxeItem(ModItemTiers.DIAMOND_STUD, 1.5F, -3.0F, new Item.Properties()), getRL("diamond_studded_axe"));
		EasyRegistry.registerItem(() -> new ShovelItem(ModItemTiers.DIAMOND_STUD, 5.0F, -3.0F, new Item.Properties()), getRL("diamond_studded_shovel"));
		EasyRegistry.registerItem(() -> new HoeItem(ModItemTiers.DIAMOND_STUD, 2, -0.5F, new Item.Properties()), getRL("diamond_studded_hoe"));
		
		RegistryObject<Block> sifter = EasyRegistry.registerBlock(SifterBlock::new, getRL("machine_sifter"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(SifterBlockEntity::new, sifter.get()).build(null), getRL("machine_sifter"));
		EasyRegistry.registerMenuType(getRL("machine_sifter"),() -> new MenuType<>(SifterContainerMenu::new, FeatureFlags.DEFAULT_FLAGS));
		
		RegistryObject<Block> millstone = EasyRegistry.registerBlock(MillstoneBlock::new, getRL("machine_millstone"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(MillstoneBlockEntity::new, millstone.get()).build(null), getRL("machine_millstone"));
		RegistryObject<Block> axel = 
		EasyRegistry.registerBlock(AxelBlock::new, getRL("machine_axel"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(AxelBlockEntity::new, axel.get()).build(null), getRL("machine_axel"));
		EasyRegistry.registerBlock(WindvaneBlock::new, getRL("machine_windvane"), new Item.Properties());
		
		EasyRegistry.registerOther(ForgeRegistries.Keys.ENCHANTMENTS, new Tuple<ResourceLocation,Supplier<Enchantment>>(getRL("prospector"),() -> new ProspectorEnchantment(new EquipmentSlot[] { EquipmentSlot.OFFHAND })));
		EasyRegistry.registerOther(ForgeRegistries.Keys.ENCHANTMENTS, new Tuple<ResourceLocation,Supplier<Enchantment>>(getRL("cracker"),() -> new VeinCrackerEnchantment(new EquipmentSlot[] { EquipmentSlot.MAINHAND })));
		EasyRegistry.registerOther(ForgeRegistries.Keys.ENCHANTMENTS, new Tuple<ResourceLocation,Supplier<Enchantment>>(getRL("shatter"),() -> new ShatterEnchantment(new EquipmentSlot[] { EquipmentSlot.MAINHAND })));
		EasyRegistry.registerOther(ForgeRegistries.Keys.ENCHANTMENTS, new Tuple<ResourceLocation,Supplier<Enchantment>>(getRL("pulverize"),() -> new PulverizeEnchantment(new EquipmentSlot[] { EquipmentSlot.MAINHAND })));
		
		EasyRegistry.registerOther(ForgeRegistries.Keys.PARTICLE_TYPES, new Tuple<ResourceLocation,Supplier<ParticleType<?>>>(getRL("prospector_dust"), () -> getParticleType(false, BlockParticleOption.DESERIALIZER, BlockParticleOption::codec)));
		EasyRegistry.registerOther(ForgeRegistries.Keys.PARTICLE_TYPES, new Tuple<ResourceLocation,Supplier<ParticleType<?>>>(getRL("prospector_radar"), () -> getParticleType(false, BlockParticleOption.DESERIALIZER, BlockParticleOption::codec)));
		
		LootFunctions.harderSetCountReg = EasyRegistry.registerOther(Registries.LOOT_FUNCTION_TYPE, new Tuple<ResourceLocation,Supplier<LootItemFunctionType>>(
				getRL("set_count"),() -> new LootItemFunctionType(new HarderSetCount.Serializer())));
	}
	
	@SuppressWarnings("deprecation")
	private static <T extends ParticleOptions> ParticleType<T> getParticleType(boolean bypassParticleLimit, ParticleOptions.Deserializer<T> deserializer, final Function<ParticleType<T>, Codec<T>> codec) {
		return new ParticleType<T>(bypassParticleLimit, deserializer) {
			public Codec<T> codec() {
				return codec.apply(this);
			}
		};
	}

	public static class LootFunctions {
		public static RegistryObject<LootItemFunctionType> harderSetCountReg;
	}
	
	public static class ModItemTiers {
		public static final Supplier<Ingredient> itm = () -> Ingredient.of(ModItems.orechunk_diamond);
		public static final Tier DIAMOND_STUD = new ForgeTier(3, 750, 7f, 2f, 5, BlockTags.NEEDS_DIAMOND_TOOL, itm);
	}
	
	/*public HarderOres() {
		modEventBus.addListener((FMLLoadCompleteEvent event) -> {
			FlowerIntegration.registerFlowerGen();
			replaceOreGenerators();
		});
		PacketHandler.register();
		HardLibAPI.hardOres = new OreBlockInfo();

		block = new SluiceBlock();
		EasyRegistry.registerBlock(block, "sluice", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(SluiceTileEntity::new, block), HarderOres.MODID, "sluice");

		block = new SluiceOutput();
		EasyRegistry.registerBlock(block, "sluice_output");

		block = new PackagerBlock();
		EasyRegistry.registerBlock(block, "packager", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(PackagerTileEntity::new, block), HarderOres.MODID, "packager");
		EasyRegistry.registerOther(IForgeContainerType.create(PackagerContainer::new), new ResourceLocation(HarderOres.MODID,"packager"));
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

	public static ResourceLocation getRL(String name) {
		return new ResourceLocation(MODID, name);
	}

	public static class ModBlocks {
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"machine_axel")
		public static final Block machine_axel = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"machine_millstone")
		public static final Block machine_millstone = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"machine_windvane")
		public static final Block machine_windvane = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"machine_sifter")
		public static final Block machine_sifter = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_harddiamond")
		public static final Block ore_harddiamond = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_hardcopper")
		public static final Block ore_hardcopper = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_hardiron")
		public static final Block ore_hardiron = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_hardgold")
		public static final Block ore_hardgold = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_limonite")
		public static final Block ore_limonite = null;

		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_harddeepslate_copper")
		public static final Block ore_harddeepslate_copper = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_harddeepslate_diamond")
		public static final Block ore_harddeepslate_diamond = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_harddeepslate_iron")
		public static final Block ore_harddeepslate_iron = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"ore_harddeepslate_gold")
		public static final Block ore_harddeepslate_gold = null;

		public static final Block sluice = null;
		public static final Block sluice_output = null;
	}

	public static class ModItems {
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"diamond_studded_pick")
		public static final Item diamond_studded_pick = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"diamond_studded_axe")
		public static final Item diamond_studded_axe = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"diamond_studded_shovel")
		public static final Item diamond_studded_shovel = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"diamond_studded_hoe")
		public static final Item diamond_studded_hoe = null;
		
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"orechunk_diamond")
		public static final Item orechunk_diamond = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"orechunk_copper")
		public static final Item orechunk_copper = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"orechunk_gold")
		public static final Item orechunk_gold = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"orechunk_iron")
		public static final Item orechunk_iron = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"orechunk_limonite")
		public static final Item orechunk_limonite = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"largedust_copper")
		public static final Item largedust_copper = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"largedust_iron")
		public static final Item largedust_iron = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"largedust_gold")
		public static final Item largedust_gold = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_copper")
		public static final Item tinydust_copper = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_iron")
		public static final Item tinydust_iron = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_gold")
		public static final Item tinydust_gold = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"copper_nugget")
		public static final Item copper_nugget = null;
	}

	public static class ModBlockEntities {
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"machine_sifter")
		public static BlockEntityType<SifterBlockEntity> machine_sifter;
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"machine_millstone")
		public static final BlockEntityType<MillstoneBlockEntity> machine_millstone = null;
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"machine_axel")
		public static final BlockEntityType<AxelBlockEntity> machine_axel = null;
		//public static final BlockEntityType<SifterTileEntity> packager = null;
		//public static final BlockEntityType<SluiceTileEntity> sluice = null;
	}

	public static class ModParticleTypes {
		@ObjectHolder(registryName = "minecraft:particle_type", value = MODID+":"+"prospector_dust")
		public static final ParticleType<BlockParticleOption> prospector_dust = null;
		@ObjectHolder(registryName = "minecraft:particle_type", value = MODID+":"+"prospector_radar")
		public static final ParticleType<BlockParticleOption> prospector_radar = null;
	}

	public static class ModContainerTypes {
		@ObjectHolder(registryName = "minecraft:menu", value = MODID+":"+"machine_sifter")
		public static final MenuType<SifterContainerMenu> machine_sifter = null;
		//public static final MenuType<PackagerContainer> packager = null;
	}

	public static class ModEnchantments {
		@ObjectHolder(registryName = "minecraft:enchantment", value = MODID+":"+"prospector")
		public static final Enchantment prospector = null;
		@ObjectHolder(registryName = "minecraft:enchantment", value = MODID+":"+"cracker")
		public static final Enchantment cracker = null;
		/**
		 * Ore Breaker
		 */
		@ObjectHolder(registryName = "minecraft:enchantment", value = MODID+":"+"shatter")
		public static final Enchantment shatter = null;
		@ObjectHolder(registryName = "minecraft:enchantment", value = MODID+":"+"pulverize")
		public static final Enchantment pulverize = null;
	}

	public static class ModItemTags {
		public static TagKey<Item> TINY_COPPER_DUST = new TagKey<Item>(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "dust/tiny/copper"));
		public static TagKey<Item> TINY_GOLD_DUST = new TagKey<Item>(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "dust/tiny/gold"));
		public static TagKey<Item> TINY_IRON_DUST = new TagKey<Item>(ForgeRegistries.Keys.ITEMS, new ResourceLocation("forge", "dust/tiny/iron"));
	}

	public static class ModFeatures {
		//public static final Feature<?> hardOre = null;
		//public static final Structure<?> ore_vein = null;
	}
	
	public static class ModStructurePieceTypes {
		//public static IStructurePieceType OVMotherLoad;
		//public static IStructurePieceType OreVein;
	}
	
	/*@SubscribeEvent
	public static void registerCreativeTab(CreativeModeTabEvent.Register event) {
		event.registerCreativeModeTab(getRL("creative_tab"), (builder) -> {
			//builder.icon(() -> new ItemStack(ModBlocks.machine_wood_hopper));
			builder.displayItems(new DisplayItemsGenerator() {
				@Override
				public void accept(ItemDisplayParameters params, Output output) {
					//output.accept(new ItemStack(ModBlocks.powered_rail_bridge, 1));
				}
			});
		});
	}*/
	
	@SubscribeEvent
	public static void addItemsToCreativeTab(final CreativeModeTabEvent.BuildContents event)
    {
		CreativeModeTab tab = event.getTab();
        if (tab == CreativeModeTabs.BUILDING_BLOCKS) {
        	event.accept(new ItemStack(ModBlocks.ore_limonite, 1));
        	ItemStack stack = new ItemStack(ModBlocks.ore_hardcopper, 1);
        	HardOreBlock.setNbtOnStack(stack, BlockProperties.ORE_DENSITY, 16);
        	event.accept(stack);
        	stack = new ItemStack(ModBlocks.ore_harddiamond, 1);
        	HardOreBlock.setNbtOnStack(stack, BlockProperties.ORE_DENSITY, 16);
        	event.accept(stack);
        	stack = new ItemStack(ModBlocks.ore_hardgold, 1);
        	HardOreBlock.setNbtOnStack(stack, BlockProperties.ORE_DENSITY, 16);
        	event.accept(stack);
        	stack = new ItemStack(ModBlocks.ore_hardiron, 1);
        	HardOreBlock.setNbtOnStack(stack, BlockProperties.ORE_DENSITY, 16);
        	event.accept(stack);

        	stack = new ItemStack(ModBlocks.ore_harddeepslate_copper, 1);
        	HardOreBlock.setNbtOnStack(stack, BlockProperties.ORE_DENSITY, 16);
        	event.accept(stack);
        	stack = new ItemStack(ModBlocks.ore_harddeepslate_diamond, 1);
        	HardOreBlock.setNbtOnStack(stack, BlockProperties.ORE_DENSITY, 16);
        	event.accept(stack);
        	stack = new ItemStack(ModBlocks.ore_harddeepslate_iron, 1);
        	HardOreBlock.setNbtOnStack(stack, BlockProperties.ORE_DENSITY, 16);
        	event.accept(stack);
        	stack = new ItemStack(ModBlocks.ore_harddeepslate_gold, 1);
        	HardOreBlock.setNbtOnStack(stack, BlockProperties.ORE_DENSITY, 16);
        	event.accept(stack);
        }
        if(tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
        	event.accept(ModBlocks.machine_millstone);
        	event.accept(ModBlocks.machine_axel);
        	event.accept(ModBlocks.machine_windvane);
        	event.accept(ModBlocks.machine_sifter);
        }
        if (tab == CreativeModeTabs.INGREDIENTS) {
        	event.accept(new ItemStack(ModItems.orechunk_limonite, 1));
        	event.accept(new ItemStack(ModItems.orechunk_copper, 1));
        	event.accept(new ItemStack(ModItems.tinydust_copper, 1));
        	event.accept(new ItemStack(ModItems.largedust_copper, 1));
        	event.accept(new ItemStack(ModItems.orechunk_gold, 1));
        	event.accept(new ItemStack(ModItems.tinydust_gold, 1));
        	event.accept(new ItemStack(ModItems.largedust_gold, 1));
        	event.accept(new ItemStack(ModItems.orechunk_iron, 1));
        	event.accept(new ItemStack(ModItems.tinydust_iron, 1));
        	event.accept(new ItemStack(ModItems.largedust_iron, 1));
        	event.accept(new ItemStack(ModItems.orechunk_diamond, 1));
        }
    }
}