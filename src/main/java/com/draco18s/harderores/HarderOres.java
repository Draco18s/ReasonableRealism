package com.draco18s.harderores;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.flowers.OreBlockInfo;
import com.draco18s.harderores.block.AxelBlock;
import com.draco18s.harderores.block.MillstoneBlock;
import com.draco18s.harderores.block.PackagerBlock;
import com.draco18s.harderores.block.SifterBlock;
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
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod(HarderOres.MODID)
public class HarderOres {
	public static final String MODID = "harderores";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final IProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	private static final List<OreItems> ORE_ITEMS = new ArrayList<>();

	public HarderOres() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener((FMLCommonSetupEvent event) -> {
			LootFunctionManager.registerFunction(new BlockItemFunction.Serializer());
			LootFunctionManager.registerFunction(new HarderSetCount.Serializer());
		});

		modEventBus.addListener((FMLLoadCompleteEvent event) -> {
			for(OreItems ores : ORE_ITEMS) {
				HardLibAPI.oreMachines.addMillRecipe(new ItemStack(ores.chunk,1), new ItemStack(ores.tiny,2));
				HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(ores.tiny,8), new ItemStack(ores.pile,1));
			}
		});
		PacketHandler.register();
		HardLibAPI.oreMachines = new OreProcessingRecipes();
		//TODO: remove or relocate this dependency
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
		
		block = new HardOreBlock(1, new Color(0xd8af93), Block.Properties.create(Material.ROCK).hardnessAndResistance(Blocks.IRON_ORE.getDefaultState().getBlockHardness(null, null)*2, 5).harvestTool(ToolType.PICKAXE).harvestLevel(1).sound(SoundType.STONE));
		EasyRegistry.registerBlockWithVariants(block, "ore_hardiron", BlockProperties.ORE_DENSITY, HardOreItem::new, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		block = new HardOreBlock(3, new Color(0x5decf5), Block.Properties.create(Material.ROCK).hardnessAndResistance(Blocks.DIAMOND_ORE.getDefaultState().getBlockHardness(null, null)*4, 5).harvestTool(ToolType.PICKAXE).harvestLevel(2).sound(SoundType.STONE));
		EasyRegistry.registerBlockWithVariants(block, "ore_harddiamond", BlockProperties.ORE_DENSITY, HardOreItem::new, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		block = new HardOreBlock(1, new Color(0xfacf3b), Block.Properties.create(Material.ROCK).hardnessAndResistance(Blocks.GOLD_ORE.getDefaultState().getBlockHardness(null, null)*2, 5).harvestTool(ToolType.PICKAXE).harvestLevel(2).sound(SoundType.STONE));
		EasyRegistry.registerBlockWithVariants(block, "ore_hardgold", BlockProperties.ORE_DENSITY, HardOreItem::new, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));

		Item item = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(item, "orechunk_limonite");

		Item itemChunk = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemChunk, "orechunk_iron");
		Item itemTiny = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemTiny, "tinydust_iron");
		Item itemPile = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemPile, "largedust_iron");
		ORE_ITEMS.add(new OreItems(itemChunk,itemTiny,itemPile));
		
		itemChunk = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemChunk, "orechunk_gold");
		itemTiny = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemTiny, "tinydust_gold");
		itemPile = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemPile, "largedust_gold");
		ORE_ITEMS.add(new OreItems(itemChunk,itemTiny,itemPile));

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

	@ObjectHolder(HarderOres.MODID)
	public static class ModBlocks {
		public static final Block axel = null;
		public static final Block millstone = null;
		public static final Block windvane = null;
	}

	@ObjectHolder(HarderOres.MODID)
	public static class ModItems {
		public static final Item orechunk_diamond = null;
	}

	@ObjectHolder(HarderOres.MODID)
	public static class ModTileEntities {
		public static final TileEntityType<MillstoneTileEntity> millstone = null;
		public static final TileEntityType<AxelTileEntity> axel = null;
		public static final TileEntityType<SifterTileEntity> sifter = null;
		public static final TileEntityType<SifterTileEntity> packager = null;
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

	private static class OreItems {
		protected final Item chunk;
		protected final Item tiny;
		protected final Item pile;
		public OreItems(Item itemChunk, Item itemTiny, Item itemPile) {
			chunk = itemChunk;
			tiny = itemTiny;
			pile = itemPile;
		}
	}
	
	public static class ModItemTags {
		public static final Tag<Item> STONE_ANY = new ItemTags.Wrapper(new ResourceLocation("forge", "stoneany"));
	}
}