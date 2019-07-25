package com.draco18s.harderores;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.harderores.block.ore.HardOreBlock;
import com.draco18s.harderores.block.ore.LimoniteBlock;
import com.draco18s.harderores.loot.function.BlockItemFunction;
import com.draco18s.harderores.loot.function.HarderSetCount;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.proxy.ClientProxy;
import com.draco18s.hardlib.proxy.IProxy;
import com.draco18s.hardlib.proxy.ServerProxy;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
		Block b = new HardOreBlock(1, Block.Properties.create(Material.ROCK).hardnessAndResistance(Blocks.IRON_ORE.getDefaultState().getBlockHardness(null, null)*2, 5).harvestTool(ToolType.PICKAXE).harvestLevel(1));
		EasyRegistry.registerBlockWithVariants(b, "ore_hardiron", BlockProperties.ORE_DENSITY, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		b = new LimoniteBlock(Block.Properties.create(Material.EARTH).hardnessAndResistance(3, 1).harvestTool(ToolType.SHOVEL).harvestLevel(0));
		EasyRegistry.registerBlock(b, "ore_limonite", new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		
		Item i = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(i, "orechunk_iron");
		i = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
		EasyRegistry.registerItem(i, "orechunk_limonite");
	}
	
	@EventBusSubscriber(modid = HarderOres.MODID, bus = EventBusSubscriber.Bus.MOD)
	private static class EventHandlers {
		
	}
}