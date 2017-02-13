package com.draco18s.industry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.recipes.RecipeToolMold;
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
import net.minecraft.block.BlockSand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
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
import net.minecraftforge.oredict.ShapedOreRecipe;

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

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
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
		
		HardLibAPI.itemMold = itemMold = new ItemCastingMold();
		//EasyRegistry.registerItem(itemMold, "casting_mold");
		EasyRegistry.registerItemWithCustomMeshDefinition((ItemCastingMold)itemMold, "casting_mold");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IndustryGuiHandler());
		FilterDimension.mainRegistry();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		OreDictionary.registerOre("bucket", Items.BUCKET);
		RecipeSorter.register("sand_mold", RecipeToolMold.class, Category.SHAPELESS, "");
		
		GameRegistry.addRecipe(new ShapedOreRecipe(blockWoodHopper, "p p", "p p", " p ", 'p', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(blockDistributor, " h ", " i ", "ppp", 'p', "plankWood", 'i', "ingotIron", 'h', Blocks.HOPPER));
		GameRegistry.addRecipe(new ShapedOreRecipe(blockCartLoader, "i i", "rhd", " i ", 'i', "ingotIron", 'h', Blocks.HOPPER, 'r', Blocks.REDSTONE_BLOCK, 'd', Items.REPEATER));
		GameRegistry.addRecipe(new ShapedOreRecipe(blockRailBridge, "R", "P", 'R', Blocks.RAIL, 'P', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(blockRailBridgePowered, "R", "P", 'R', Blocks.GOLDEN_RAIL, 'P', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTypeRail,6), "i i", "ipi", "iqi", 'p', Blocks.STONE_PRESSURE_PLATE, 'i', "ingotIron", 'q', Items.QUARTZ));
		if(OreDictionary.doesOreNameExist("stoneAny")) {
			GameRegistry.addRecipe(new ShapedOreRecipe(blockFoundry, "sbs","s s","sfs", 's', "stoneAny", 'f', Blocks.FURNACE, 'b', "bucket"));
		}
		else {
			GameRegistry.addRecipe(new ShapedOreRecipe(blockFoundry, "sbs","s s","sfs", 's', "stone", 'f', Blocks.FURNACE, 'b', "bucket"));
		}
		
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(new ItemStack(Blocks.CLAY));
		list.add(new ItemStack(Blocks.SAND));
		GameRegistry.addRecipe(new ShapelessRecipes(new ItemStack(itemMold, 2), list));
		list = new ArrayList<ItemStack>();
		list.add(new ItemStack(Blocks.CLAY));
		list.add(new ItemStack(Blocks.SAND, 1, BlockSand.EnumType.RED_SAND.getMetadata()));
		GameRegistry.addRecipe(new ShapelessRecipes(new ItemStack(itemMold, 2), list));
		
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, Items.IRON_AXE, itemMold));
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
		GameRegistry.addRecipe(new RecipeToolMold(itemMold, new ItemStack(Blocks.RAIL, 16), itemMold));
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
