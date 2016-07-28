package com.draco18s.ores;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.RecipesUtil;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.hardlib.capability.CapabilityMechanicalPower;
import com.draco18s.ores.block.BlockAxel;
import com.draco18s.ores.block.BlockMillstone;
import com.draco18s.ores.block.BlockSifter;
import com.draco18s.ores.block.BlockWindvane;
import com.draco18s.ores.block.ore.BlockHardDiamond;
import com.draco18s.ores.block.ore.BlockHardGold;
import com.draco18s.ores.block.ore.BlockHardIron;
import com.draco18s.ores.enchantments.EnchantmentProspector;
import com.draco18s.ores.enchantments.EnchantmentPulverize;
import com.draco18s.ores.enchantments.EnchantmentVeinCracker;
import com.draco18s.ores.entities.TileEntityAxel;
import com.draco18s.ores.entities.TileEntityMillstone;
import com.draco18s.ores.entities.TileEntitySifter;
import com.draco18s.ores.item.ItemDustLarge;
import com.draco18s.ores.item.ItemDustSmall;
import com.draco18s.ores.item.ItemOreBlock;
import com.draco18s.ores.item.ItemRawOre;
import com.draco18s.ores.networking.PacketHandlerClient;
import com.draco18s.ores.networking.ToClientMessage;
import com.draco18s.ores.recipes.OreProcessingRecipes;

@Mod(modid="HarderOres", name="HarderOres", version="{@version:ore}"/*, dependencies = "required-after:HardLib"*/)
public class OresBase {
	@Instance
	public static OresBase instance;
	
	@SidedProxy(clientSide="com.draco18s.ores.client.ClientProxy", serverSide="com.draco18s.ores.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;
	
	public static Block oreIron;
	public static Block oreGold;
	public static Block oreDiamond;
	
	public static Block millstone;
	public static Block axel;
	public static Block windvane;
	public static Block sifter;
	
	public static Item rawOre;
	public static Item smallDust;
	public static Item largeDust;
	
	public static Enchantment enchPulverize;
	public static Enchantment enchCracker;
	public static Enchantment enchProspector;
	
	public static Configuration config;

	public static SimpleNetworkWrapper networkWrapper;

	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		CapabilityMechanicalPower.register();
		
		oreIron = new BlockHardIron();
		proxy.registerBlockWithCustomItem(oreIron, new ItemOreBlock(oreIron), "hardiron");
		oreGold = new BlockHardGold();
		proxy.registerBlockWithCustomItem(oreGold, new ItemOreBlock(oreGold), "hardgold");
		oreDiamond = new BlockHardDiamond();
		proxy.registerBlockWithCustomItem(oreDiamond, new ItemOreBlock(oreDiamond), "harddiamond");
		millstone = new BlockMillstone();
		proxy.registerBlockWithItem(millstone, "millstone");
		GameRegistry.registerTileEntity(TileEntityMillstone.class, "millstone");
		axel = new BlockAxel();
		proxy.registerBlockWithItem(axel, "axel");
		GameRegistry.registerTileEntity(TileEntityAxel.class, "axel");
		windvane = new BlockWindvane();
		proxy.registerBlockWithItem(windvane, "windvane");
		sifter = new BlockSifter();
		proxy.registerBlockWithItem(sifter, "sifter");
		GameRegistry.registerTileEntity(TileEntitySifter.class, "sifter");
		
		rawOre = new ItemRawOre();
		proxy.RegisterItemWithVariants(rawOre, "orechunks", EnumOreType.IRON);
		smallDust = new ItemDustSmall();
		proxy.RegisterItemWithVariants(smallDust, "tinydust", EnumOreType.IRON);
		largeDust = new ItemDustLarge();
		proxy.RegisterItemWithVariants(largeDust, "largedust", EnumOreType.IRON);

		EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND};
		enchPulverize = new EnchantmentPulverize(slots);
		enchPulverize.setRegistryName("pulverize");
		GameRegistry.register(enchPulverize);
		
		enchCracker = new EnchantmentVeinCracker(slots);
		enchCracker.setRegistryName("cracker");
		GameRegistry.register(enchCracker);

		slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.OFFHAND};
		enchProspector = new EnchantmentProspector(slots);
		enchProspector.setRegistryName("prospector");
		GameRegistry.register(enchProspector);
		
		proxy.registerEventHandlers();
		proxy.registerRenderers();
		
        //These have to be unique
        //byte serverMessageID = 1;
        byte clientMessageID = 2;
		
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("harderores");
		//networkWrapper.registerMessage(PacketHandlerServer.class, ToServerMessage.class, serverMessageID, Side.SERVER);
		networkWrapper.registerMessage(PacketHandlerClient.class, ToClientMessage.class, clientMessageID, Side.CLIENT);
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		OreDictionary.registerOre("rawOreChunkLimonite", new ItemStack(rawOre, 1, EnumOreType.LIMONITE.meta));
		OreDictionary.registerOre("rawOreChunkIron", new ItemStack(rawOre, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("rawOreChunkGold", new ItemStack(rawOre, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("rawOreChunkDiamond", new ItemStack(rawOre, 1, EnumOreType.DIAMOND.meta));
		
		OreDictionary.registerOre("dustTinyIron", new ItemStack(smallDust, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("dustTinyGold", new ItemStack(smallDust, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("dustTinyFlour", new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta));
		OreDictionary.registerOre("dustTinySugar", new ItemStack(smallDust, 1, EnumOreType.SUGAR.meta));
		
		HardLibAPI.oreMachines = new OreProcessingRecipes();
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(rawOre,1,EnumOreType.IRON.meta), new ItemStack(smallDust,2,EnumOreType.IRON.meta));
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(rawOre,1,EnumOreType.GOLD.meta), new ItemStack(smallDust,2,EnumOreType.GOLD.meta));
		
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.IRON.meta), new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.GOLD.meta), new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.FLOUR.meta), new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));

		config.addCustomCategoryComment("MILLING", "Enable (hard mode) these to remove vanilla recipes for items and instead require the millstone. In general,\neasy means the millstone doubles resources, while hard is near-vanilla.");
		boolean hardOption = config.getBoolean("RequireMillingFlour", "MILLING", false, "");

		String oreIn = "dustFlour";
		if(hardOption) {
			RecipesUtil.RemoveRecipe(Items.BREAD, 1, 0, "Hard Ores");
			RecipesUtil.RemoveRecipe(Items.COOKIE, 8, 0, "Hard Ores");
			RecipesUtil.RemoveRecipe(Items.CAKE, 1, 0, "Hard Ores");
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.BREAD, 3), "www", 'w', oreIn)); //works out to 1:1 vanilla
			//hard: wheat is ground to "4/9th flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT), new ItemStack(smallDust, 4, EnumOreType.FLOUR.meta));
			//hard: seeds are ground to "1/9ths flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta));
		}
		else {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.BREAD), "www", 'w', oreIn));
			//easy: wheat is ground to "2 flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT), new ItemStack(smallDust, 18, EnumOreType.FLOUR.meta));
			//easy: seeds are ground to "2/9ths flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(smallDust, 2, EnumOreType.FLOUR.meta));
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.COOKIE, 8), "wcw", 'w', oreIn, 'c', new ItemStack(Items.DYE, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.CAKE), "mmm", "ses", "www", 'w', oreIn, 's', Items.SUGAR, 'e', Items.EGG, 'm', Items.MILK_BUCKET));
		
		hardOption = config.getBoolean("RequireMillingSugar", "MILLING", false, "If enabled, sugarcane cannot be crafted into sugar");
		int sugarMulti = config.getInt("MillingMultiplierSugar", "MILLING", 6, 1, 12, "Sugar is a easy-to-get resource and rare-to-use, so it may be desirable to reduce the production.\nOutput of milling sugar (in tiny piles) is this value in hard-milling and 2x this value in\neasy-milling.\nVanilla Equivalence is 9.");

		if(hardOption) {
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.REEDS), new ItemStack(smallDust, sugarMulti, EnumOreType.SUGAR.meta));
		}
		else {
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.REEDS), new ItemStack(smallDust, 2*sugarMulti, EnumOreType.SUGAR.meta));
		}
		hardOption = config.getBoolean("RequireMillingBonemeal", "MILLING", false, "");
		if(hardOption) {
			RecipesUtil.RemoveRecipe(Items.DYE, 3, 15, "Hard Ores");
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.BONE), new ItemStack(Items.DYE, 2, 15));
		}
		else {
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.BONE), new ItemStack(Items.DYE, 4, 15));
		}

		ItemStack bonemeal = new ItemStack(Items.DYE, 1, 15);
		HardLibAPI.oreMachines.addSiftRecipe(bonemeal, bonemeal, false);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		config.save();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
