package com.draco18s.industry;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.recipes.RecipeToolMold;
import com.draco18s.hardlib.api.recipes.RecipeToolMold.RecipeSubItem;
import com.draco18s.hardlib.util.RecipesUtils;
import com.draco18s.industry.block.BlockCartLoader;
import com.draco18s.industry.block.BlockDistributor;
import com.draco18s.industry.block.BlockFilter;
import com.draco18s.industry.block.BlockFoundry;
import com.draco18s.industry.block.BlockPoweredRailBridge;
import com.draco18s.industry.block.BlockRailBridge;
import com.draco18s.industry.block.BlockTypeRail;
import com.draco18s.industry.block.BlockWoodenHopper;
import com.draco18s.industry.entities.TileEntityCartLoader;
import com.draco18s.industry.entities.TileEntityDistributor;
import com.draco18s.industry.entities.TileEntityFilter;
import com.draco18s.industry.entities.TileEntityFoundry;
import com.draco18s.industry.entities.TileEntityWoodenHopper;
import com.draco18s.industry.entities.capabilities.CastingItemStackHandler;
import com.draco18s.industry.item.ItemCastingMold;
import com.draco18s.industry.network.CtoSMessage;
import com.draco18s.industry.network.PacketHandlerServer;
import com.draco18s.industry.world.FilterDimension;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Mod(modid="expindustry", name="ExpandedIndustry", version="{@version:ind}", dependencies = "required-after:hardlib")
public class ExpandedIndustryBase {
	@Instance("expindustry")
	public static ExpandedIndustryBase instance;
	
	public static Block blockWoodHopper;
	public static Block blockDistributor;
	public static Block blockCartLoader;
	public static Block blockFilter;
	public static Block blockRailBridge;
	public static Block blockTypeRail;
	public static Block blockRailBridgePowered;

	public static Block blockFoundry;
	
	public static Item itemMold;
	
	@SidedProxy(clientSide="com.draco18s.industry.client.ClientProxy", serverSide="com.draco18s.industry.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;
	
	public static SimpleNetworkWrapper networkWrapper;
	public static HashMap<ChunkPos, Integer> ticketList;
	private static Ticket chunkLoaderTicket;

	private Configuration config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		blockWoodHopper = new BlockWoodenHopper();
		EasyRegistry.registerBlockWithItem(blockWoodHopper, "machine_wood_hopper");
		blockDistributor = new BlockDistributor();
		EasyRegistry.registerBlockWithItem(blockDistributor, "machine_distributor");
		blockCartLoader = new BlockCartLoader();
		EasyRegistry.registerBlockWithItem(blockCartLoader, "machine_cart_loader");
		blockFilter = new BlockFilter();
		EasyRegistry.registerBlockWithItem(blockFilter, "machine_filter");
		blockRailBridge = new BlockRailBridge();
		EasyRegistry.registerBlockWithItem(blockRailBridge, "rail_bridge");
		blockTypeRail = new BlockTypeRail();
		EasyRegistry.registerBlockWithItem(blockTypeRail, "type_rail");
		blockRailBridgePowered = new BlockPoweredRailBridge();
		EasyRegistry.registerBlockWithItem(blockRailBridgePowered, "rail_bridge_powered");
		
		blockFoundry = new BlockFoundry();
		EasyRegistry.registerBlockWithItem(blockFoundry, "machine_foundry");

		GameRegistry.registerTileEntity(TileEntityWoodenHopper.class, "expindustry:machine_wood_hopper");
		GameRegistry.registerTileEntity(TileEntityDistributor.class, "expindustry:machine_distributor");
		GameRegistry.registerTileEntity(TileEntityCartLoader.class, "expindustry:machine_cart_loader");
		GameRegistry.registerTileEntity(TileEntityFilter.class, "expindustry:machine_filter");
		GameRegistry.registerTileEntity(TileEntityFoundry.class, "expindustry:machine_foundry");
		
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.IRON_AXE), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.IRON_SHOVEL), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.IRON_PICKAXE), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.IRON_HOE), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.IRON_SWORD), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.IRON_HELMET), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.IRON_CHESTPLATE), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.IRON_LEGGINGS), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.IRON_BOOTS), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.SHEARS), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Items.BUCKET), (String)null));
		RecipeToolMold.allMoldItem(new RecipeSubItem(new ItemStack(Blocks.RAIL, 16), (String)null));
		
		HardLibAPI.itemMold = itemMold = new ItemCastingMold();
		//EasyRegistry.registerItem(itemMold, "casting_mold");
		EasyRegistry.registerItemWithCustomMeshDefinition((ItemCastingMold)itemMold, "casting_mold");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IndustryGuiHandler());
		FilterDimension.mainRegistry();
		MinecraftForge.EVENT_BUS.register(new IndustryEventHandler());
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		OreDictionary.registerOre("bucket", Items.BUCKET);
		
		RecipeSorter.register("sand_mold", RecipeToolMold.class, Category.SHAPELESS, "");
		//TODO: recipes
		RecipesUtils.setupDir(config);
		/*RecipesUtils.addShapedRecipe(blockWoodHopper, "p p", "p p", " p ", 'p', "plankWood");
		RecipesUtils.addShapedRecipe(blockDistributor, " h ", " i ", "ppp", 'p', "plankWood", 'i', "ingotIron", 'h', Blocks.HOPPER);
		RecipesUtils.addShapedRecipe(blockCartLoader, "i i", "rhd", " i ", 'i', "ingotIron", 'h', Blocks.HOPPER, 'r', Blocks.REDSTONE_BLOCK, 'd', Items.COMPARATOR);
		RecipesUtils.addShapedRecipe(blockRailBridge, "R", "P", 'R', Blocks.RAIL, 'P', "plankWood");
		RecipesUtils.addShapedRecipe(blockRailBridgePowered, "R", "P", 'R', Blocks.GOLDEN_RAIL, 'P', "plankWood");
		RecipesUtils.addShapedRecipe(new ItemStack(blockTypeRail,6), "i i", "ipi", "iqi", 'p', Blocks.STONE_PRESSURE_PLATE, 'i', "ingotIron", 'q', Items.QUARTZ);
		//if(OreDictionary.doesOreNameExist("stoneAny")) {
		RecipesUtils.addShapedRecipe(blockFoundry, "sbs","s s","sfs", 's', "stoneAny", 'f', Blocks.FURNACE, 'b', "bucket");
		//}
		//else {
			//GameRegistry.addRecipe(new ShapedOreRecipe(blockFoundry, "sbs","s s","sfs", 's', "stone", 'f', Blocks.FURNACE, 'b', "bucket"));
		//}
		RecipesUtils.addShapedRecipe(blockFilter, "g g", "ghg", " g ", 'g', "ingotGold", 'h', Blocks.HOPPER);
		RecipesUtils.addShapelessRecipe(new ItemStack(itemMold, 2), new ItemStack(Blocks.CLAY), new ItemStack(Blocks.SAND));
		RecipesUtils.addShapelessRecipe(new ItemStack(itemMold, 2), new ItemStack(Blocks.CLAY), new ItemStack(Blocks.SAND, 1, BlockSand.EnumType.RED_SAND.getMetadata()));*/
		
		//TODO: recipe event
		/*GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.IRON_AXE, itemMold));
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.IRON_SHOVEL, itemMold));
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.IRON_PICKAXE, itemMold));
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.IRON_HOE, itemMold));
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.IRON_SWORD, itemMold));
		
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.IRON_HELMET, itemMold));
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.IRON_CHESTPLATE, itemMold));
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.IRON_LEGGINGS, itemMold));
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.IRON_BOOTS, itemMold));

		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.SHEARS, itemMold));
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.BUCKET, itemMold));
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, new ItemStack(Blocks.RAIL, 16), itemMold));*/
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		byte serverMessageID = 2;
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("ExpandedIndustry");
		networkWrapper.registerMessage(PacketHandlerServer.class, CtoSMessage.class, serverMessageID, Side.SERVER);
		ticketList = new HashMap<ChunkPos, Integer>();
		ForgeChunkManager.setForcedChunkLoadingCallback(this, new LoadingCallback() {
			@Override
			public void ticketsLoaded(List<Ticket> tickets, World world) {
				
			}
		});
		CastingItemStackHandler.initLists();
	}
	
	public static void forceChunkLoad(World w, ChunkPos pos) {
		if(!ticketList.containsKey(pos)) {
			if(chunkLoaderTicket == null) {
				chunkLoaderTicket = ForgeChunkManager.requestTicket(instance, w, Type.NORMAL);
			}
			ticketList.put(pos, 1);
			ForgeChunkManager.forceChunk(chunkLoaderTicket, pos);
		}
		else {
			ticketList.put(pos, ticketList.get(pos)+1);
		}
	}
	
	public static void releaseChunkLoad(World w, ChunkPos pos) {
		if(!ticketList.containsKey(pos) || chunkLoaderTicket == null) {
			return;
		}
		else {
			int num = ticketList.get(pos)-1;
			if(num > 0)
				ticketList.put(pos, num);
			else
				ForgeChunkManager.unforceChunk(chunkLoaderTicket, pos);
		}
	}
}
