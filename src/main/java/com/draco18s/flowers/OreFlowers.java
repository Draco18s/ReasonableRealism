package com.draco18s.flowers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.flowers.block.BloomingFlower;
import com.draco18s.flowers.block.SimpleFlower;
import com.draco18s.flowers.block.TwoTallFlower;
import com.draco18s.flowers.config.ConfigHolder;
import com.draco18s.flowers.item.ItemStickyBlob;
import com.draco18s.flowers.proxy.ClientProxy;
import com.draco18s.flowers.proxy.IProxy;
import com.draco18s.flowers.proxy.ServerProxy;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ObjectHolder;

@Mod(OreFlowers.MODID)
public class OreFlowers {
	public static final String MODID = "oreflowers";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final IProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	
	public OreFlowers() {
		HardLibAPI.oreFlowers = new FlowerDataHandler();
		
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener((FMLCommonSetupEvent event) -> {
			final ModLoadingContext modLoadingContext = ModLoadingContext.get();
			//modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
			modLoadingContext.registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
			//ConfigHolder.loadConfig(ConfigHolder.CLIENT_SPEC, FMLPaths.CONFIGDIR.get().resolve(MODID+".toml"));
			ConfigHolder.loadConfig(ConfigHolder.COMMON_SPEC, FMLPaths.CONFIGDIR.get().resolve(MODID+".toml"));
			PROXY.registerConfigGui(modLoadingContext);
			if(HardLibAPI.oreMachines != null)
				HardLibAPI.oreMachines.addMillRecipe(new ItemStack(ModItems.aveloz), new ItemStack(ModItems.sticky_goo));
		});
		/*modEventBus.addListener((ModConfig.ModConfigEvent event) -> {
			final ModConfig config = event.getConfig();
			if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
				ConfigHelper.bakeClient(config);
			} else if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
				ConfigHelper.bakeServer(config);
			}
		});*/
		
		Item item;
		item = new ItemStickyBlob();
		EasyRegistry.registerItem(item, "sticky_goo");
		Block block;

		//simple flowers
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "poorjoe", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "vallozia", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "hauman", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "leadplant", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "red_amaranth", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "shrub_violet", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "affine", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "clover", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "camellia", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "malva", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "melastoma", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "broadleaf_arrowhead", new Item.Properties().group(ItemGroup.DECORATIONS));
		
		//horsetail
		block = new BloomingFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "horsetail", new Item.Properties().group(ItemGroup.DECORATIONS));
		//TODO: what ground material should be
		//block = new SimpleFlower(PlantType.Plains);
		//EasyRegistry.registerBlock(block, "stoneroot", new Item.Properties().group(ItemGroup.DECORATIONS));
		
		//simple desert flowers
		block = new SimpleFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "red_sorrel", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "copper_flower", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "sheeps_fescue", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "primrose", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "rapeseed", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "milkwort", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "madwort", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "zilla", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new SimpleFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "marigold", new Item.Properties().group(ItemGroup.DECORATIONS));
		
		//Two-tall flowers
		block = new TwoTallFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "tansy", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new TwoTallFlower(PlantType.Plains);
		EasyRegistry.registerBlock(block, "flame_lily", new Item.Properties().group(ItemGroup.DECORATIONS));
		
		//two-tall desert flowers
		block = new TwoTallFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "chandelier_tree", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new TwoTallFlower(PlantType.Desert);
		EasyRegistry.registerBlock(block, "aveloz", new Item.Properties().group(ItemGroup.DECORATIONS));
		
		/* Alternative notes
		 * https://remotedesktop.google.com/access/session/9640bcda-1b91-1aac-a941-fae34c89c43a
		 */
	}
	
	@ObjectHolder(OreFlowers.MODID)
	public static class ModBlocks {
		public static final Block poorjoe = null;  
		public static final Block vallozia = null;
		public static final Block hauman = null;
		public static final Block leadplant = null;
		public static final Block red_amaranth = null;
		public static final Block shrub_violet = null;
		public static final Block affine = null;
		public static final Block clover = null;
		public static final Block camellia = null;
		public static final Block melastoma = null;
		public static final Block broadleaf_arrowhead = null;
		public static final Block tansy = null;
		public static final Block flame_lily = null;
		
		public static final Block red_sorrel = null;
		public static final Block copper_flower = null;
		public static final Block sheeps_fescue = null;
		public static final Block primrose = null;
		public static final Block rapeseed = null;
		public static final Block milkwart = null;
		public static final Block madwort = null;
		public static final Block zilla = null;
		public static final Block marigold = null;
		public static final Block aveloz = null;
		public static final Block chandelier_tree = null;
	}

	@ObjectHolder(OreFlowers.MODID)
	public static class ModItems {
		public static final Item aveloz = null;
		public static final Item sticky_goo = null;
	}
}