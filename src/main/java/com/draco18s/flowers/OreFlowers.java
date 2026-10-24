package com.draco18s.flowers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.draco18s.flowers.block.BloomingFlower;
import com.draco18s.flowers.block.SimpleFlower;
import com.draco18s.flowers.block.TwoTallFlower;
import com.draco18s.flowers.item.StickyBlobItem;
import com.draco18s.flowers.util.OreDataStore;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

/**
 * Flowers that grow over ore deposits. Each ore has a plains flower and a desert one; the rest are decorative
 * (1.12 used them for other mods' ores).
 */
@Mod(OreFlowers.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class OreFlowers {
	public static final String MODID = "oreflowers";

	/** Every flower block, in creative-tab order. */
	public static final Map<String, RegistryObject<Block>> FLOWERS = new LinkedHashMap<String, RegistryObject<Block>>();
	public static final OreDataStore oreData = new OreDataStore();

	public OreFlowers() {
		HardLibAPI.oreFlowers = new FlowerDataHandler();
		HardLibAPI.oreData = oreData;
		OreFlowerDictator.defaultDictator = new OreFlowerDictator(200, 30);
		OreFlowerDictator.closeDictator = new OreFlowerDictator(200, 15);
		OreFlowerDictator.rareDictator = new OreFlowerDictator(400, 30);
		OreFlowerDictator.closeRareDictator = new OreFlowerDictator(400, 15);
		OreFlowerDictator.commonDictator = new OreFlowerDictator(100, 30);
		OreFlowerDictator.closeCommonDictator = new OreFlowerDictator(100, 15);

		EasyRegistry.registerItem(StickyBlobItem::new, getRL("sticky_goo"));

		String[] plains = {
				"poorjoe",//iron
				"vallozia",//diamond
				"hauman", "leadplant", "red_amaranth", "shrub_violet", "affine", "clover", "camellia", "malva", "melastoma", "broadleaf_arrowhead",
		};
		for(String name : plains) {
			registerFlower(() -> new SimpleFlower(PlantType.PLAINS), name);
		}
		registerFlower(() -> new BloomingFlower(PlantType.PLAINS), "horsetail");//gold

		String[] desert = {
				"red_sorrel",//iron
				"copper_flower", "sheeps_fescue", "primrose", "rapeseed", "milkwort", "madwort", "zilla", "marigold",
		};
		for(String name : desert) {
			registerFlower(() -> new SimpleFlower(PlantType.DESERT), name);
		}

		registerFlower(() -> new TwoTallFlower(PlantType.PLAINS), "tansy");
		registerFlower(() -> new TwoTallFlower(PlantType.PLAINS), "flame_lily");//redstone
		registerFlower(() -> new TwoTallFlower(PlantType.DESERT), "chandelier_tree");//diamond
		registerFlower(() -> new TwoTallFlower(PlantType.DESERT), "aveloz");//redstone
	}

	/**
	 * Which plants indicate which ores (1.12's table), by forge:ores/* block tag. Rebuilt whenever the server's tags
	 * load. Each ore has a plains plant and a desert plant; each grows only on its own soil, so both are tried.
	 * The values are: plants per cluster, cluster width, and the bonemeal "high concentration" threshold.
	 */
	static void registerOres() {
		FlowerDataHandler flowers = new FlowerDataHandler();
		addOre(flowers, "iron",      8, 11, 7, OreFlowerDictator.defaultDictator,   "poorjoe", "red_sorrel");
		addOre(flowers, "gold",      9, 11, 6, OreFlowerDictator.defaultDictator,   "horsetail", null);
		addOre(flowers, "diamond",   8, 15, 5, OreFlowerDictator.commonDictator,    "vallozia", "chandelier_tree");
		addOre(flowers, "redstone",  8, 15, 5, OreFlowerDictator.closeRareDictator, "flame_lily", "aveloz");
		addOre(flowers, "copper",    4, 10, 6, OreFlowerDictator.defaultDictator,   "hauman", "copper_flower");
		addOre(flowers, "tin",       8,  5, 6, OreFlowerDictator.rareDictator,      "tansy", null);
		addOre(flowers, "lead",      4,  7, 5, OreFlowerDictator.defaultDictator,   "leadplant", "sheeps_fescue");
		addOre(flowers, "uranium",   3,  7, 4, OreFlowerDictator.defaultDictator,   "red_amaranth", "primrose");
		addOre(flowers, "silver",   11,  5, 5, OreFlowerDictator.defaultDictator,   null, "rapeseed");
		addOre(flowers, "nickel",    6,  8, 5, OreFlowerDictator.defaultDictator,   "shrub_violet", "milkwort");
		addOre(flowers, "aluminum",  5, 10, 5, OreFlowerDictator.defaultDictator,   "affine", null);
		addOre(flowers, "platinum",  8, 12, 5, OreFlowerDictator.rareDictator,      null, "madwort");
		addOre(flowers, "zinc",      8, 10, 6, OreFlowerDictator.rareDictator,      "clover", "zilla");
		addOre(flowers, "fluorite",  7, 10, 4, OreFlowerDictator.defaultDictator,   "camellia", null);
		addOre(flowers, "cadmium",   6, 10, 6, OreFlowerDictator.defaultDictator,   "malva", "marigold");
		addOre(flowers, "thorium",   5,  8, 4, OreFlowerDictator.defaultDictator,   "melastoma", null);
		addOre(flowers, "osmium",    5,  9, 5, OreFlowerDictator.defaultDictator,   "broadleaf_arrowhead", null);
		HardLibAPI.oreFlowers = flowers;
	}

	private static void addOre(FlowerDataHandler flowers, String ore, int numFlowers, int clusterSize, int threshold, OreFlowerDictator dictator, @Nullable String flower, @Nullable String desertFlower) {
		TagKey<Block> tag = BlockTags.create(new ResourceLocation("forge", "ores/" + ore));
		for(Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
			Block block = holder.value();
			// Hard ores count by density; a vanilla-style ore block counts as about half a full hard ore block.
			BlockWrapper wrap = block.defaultBlockState().hasProperty(BlockProperties.ORE_DENSITY) ? new BlockWrapper(block, BlockProperties.ORE_DENSITY) : new BlockWrapper(block, 9);
			if(flowers.getDataForOre(wrap) != null) continue;
			for(String name : new String[] {flower, desertFlower}) {
				if(name == null) continue;
				flowers.addOreFlowerData(wrap, dictator, new OreFlowerData(FLOWERS.get(name).get().defaultBlockState(), numFlowers, clusterSize, threshold));
			}
		}
	}

	private static void registerFlower(Supplier<Block> block, String name) {
		FLOWERS.put(name, EasyRegistry.registerBlock(block, getRL(name), new Item.Properties()));
	}

	public static class ModBlocks {
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"aveloz")
		public static final Block aveloz = null;
	}

	public static class ModItems {
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"sticky_goo")
		public static final Item sticky_goo = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"aveloz")
		public static final Item aveloz = null;
	}

	@SubscribeEvent
	public static void addItemsToCreativeTab(final CreativeModeTabEvent.BuildContents event) {
		if(event.getTab() == CreativeModeTabs.NATURAL_BLOCKS) {
			FLOWERS.values().forEach(flower -> event.accept(flower.get()));
		}
		if(event.getTab() == CreativeModeTabs.INGREDIENTS) {
			event.accept(ModItems.sticky_goo);
		}
	}

	private static ResourceLocation getRL(String name) {
		return new ResourceLocation(MODID, name);
	}
}
