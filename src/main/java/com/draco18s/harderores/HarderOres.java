package com.draco18s.harderores;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.harderores.block.AxelBlock;
import com.draco18s.harderores.block.MillstoneBlock;
import com.draco18s.harderores.block.SifterBlock;
import com.draco18s.harderores.block.WindvaneBlock;
import com.draco18s.harderores.block.ore.HardOreBlock;
import com.draco18s.harderores.block.ore.LimoniteBlock;
import com.draco18s.harderores.entity.AxelTileEntity;
import com.draco18s.harderores.entity.MillstoneTileEntity;
import com.draco18s.harderores.entity.SifterTileEntity;
import com.draco18s.harderores.inventory.SifterContainer;
import com.draco18s.harderores.item.HardOreItem;
import com.draco18s.harderores.loot.function.BlockItemFunction;
import com.draco18s.harderores.loot.function.HarderSetCount;
import com.draco18s.harderores.recipe.OreProcessingRecipes;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.proxy.ClientProxy;
import com.draco18s.hardlib.proxy.IProxy;
import com.draco18s.hardlib.proxy.ServerProxy;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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
			//HardLibAPI.oreMachines.addMillRecipe(new ItemStack(HarderOres.ModItems.orechunk_iron,1), new ItemStack(HarderOres.ModItems.tinydust_iron,2));
		});
		HardLibAPI.oreMachines = new OreProcessingRecipes();

		Block block = new LimoniteBlock(Block.Properties.create(Material.EARTH).hardnessAndResistance(3, 1).harvestTool(ToolType.SHOVEL).harvestLevel(0).sound(SoundType.WET_GRASS));
		EasyRegistry.registerBlock(block, "ore_limonite", new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		block = new MillstoneBlock();
		EasyRegistry.registerBlock(block, "millstone", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(MillstoneTileEntity::new, block), HarderOres.MODID, "millstone");
		//ModTileEntities.millstone = (TileEntityType<MillstoneTileEntity>) type;
		block = new AxelBlock();
		EasyRegistry.registerBlock(block, "axel", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(AxelTileEntity::new, block), HarderOres.MODID, "axel");
		block = new WindvaneBlock();
		EasyRegistry.registerBlock(block, "windvane", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SifterBlock();
		EasyRegistry.registerBlock(block, "sifter", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(SifterTileEntity::new, block), HarderOres.MODID, "sifter");
		
		block = new HardOreBlock(1, Block.Properties.create(Material.ROCK).hardnessAndResistance(Blocks.IRON_ORE.getDefaultState().getBlockHardness(null, null)*2, 5).harvestTool(ToolType.PICKAXE).harvestLevel(1).sound(SoundType.STONE));
		EasyRegistry.registerBlockWithVariants(block, "ore_hardiron", BlockProperties.ORE_DENSITY, HardOreItem::new, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		
		Item item = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(item, "orechunk_limonite");

		Item itemChunk = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemChunk, "orechunk_iron");
		Item itemTiny = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemTiny, "tinydust_iron");
		Item itemPile = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(itemPile, "largedust_iron");
		ORE_ITEMS.add(new OreItems(itemChunk,itemTiny,itemPile));
		
		EasyRegistry.registerOther(IForgeContainerType.create(SifterContainer::new), new ResourceLocation(HarderOres.MODID,"sifter"));
	}

	//@EventBusSubscriber(modid = HarderOres.MODID, bus = EventBusSubscriber.Bus.MOD)
	//private static class EventHandlers {
	//	
	//}

	@ObjectHolder(HarderOres.MODID)
	public static class ModBlocks {
		public static final Block axel = null;
		public static final Block millstone = null;
		public static final Block windvane = null;
	}

	@ObjectHolder(HarderOres.MODID)
	public static class ModItems {
		//public static final Item orechunk_iron = null;
		//public static final Item tinydust_iron = null;
		//public static final Item largedust_iron = null;
	}

	@ObjectHolder(HarderOres.MODID)
	public static class ModTileEntities {
		public static final TileEntityType<MillstoneTileEntity> millstone = null;
		public static final TileEntityType<AxelTileEntity> axel = null;
		public static final TileEntityType<SifterTileEntity> sifter = null;
	}

	@ObjectHolder(HarderOres.MODID)
	public static class ModContainerTypes {
		public static final ContainerType<SifterContainer> SIFTER = null;
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
}