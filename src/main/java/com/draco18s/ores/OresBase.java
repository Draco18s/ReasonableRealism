package com.draco18s.ores;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.RecipesUtil;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.hardlib.capability.CapabilityMechanicalPower;
import com.draco18s.ores.block.BlockAxel;
import com.draco18s.ores.block.BlockMillstone;
import com.draco18s.ores.block.BlockSifter;
import com.draco18s.ores.block.BlockSluice;
import com.draco18s.ores.block.BlockWindvane;
import com.draco18s.ores.block.ore.BlockHardDiamond;
import com.draco18s.ores.block.ore.BlockHardGold;
import com.draco18s.ores.block.ore.BlockHardIron;
import com.draco18s.ores.enchantments.EnchantmentProspector;
import com.draco18s.ores.enchantments.EnchantmentPulverize;
import com.draco18s.ores.enchantments.EnchantmentVeinCracker;
import com.draco18s.ores.entities.EntityOreMinecart;
import com.draco18s.ores.entities.TileEntityAxel;
import com.draco18s.ores.entities.TileEntityMillstone;
import com.draco18s.ores.entities.TileEntitySifter;
import com.draco18s.ores.entities.TileEntitySluice;
import com.draco18s.ores.flowers.FlowerIntegration;
import com.draco18s.ores.item.ItemDiamondStudHoe;
import com.draco18s.ores.item.ItemDiamondStudPickaxe;
import com.draco18s.ores.item.ItemDiamondStudShovel;
import com.draco18s.ores.item.ItemDustLarge;
import com.draco18s.ores.item.ItemDustSmall;
import com.draco18s.ores.item.ItemEntityOreCart;
import com.draco18s.ores.item.ItemNugget;
import com.draco18s.ores.item.ItemOreBlock;
import com.draco18s.ores.item.ItemRawOre;
import com.draco18s.ores.networking.ClientOreParticleHandler;
import com.draco18s.ores.networking.ServerOreCartHandler;
import com.draco18s.ores.networking.ToClientMessageOreParticles;
import com.draco18s.ores.networking.ToServerMessageOreCart;
import com.draco18s.ores.recipes.OreProcessingRecipes;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.client.model.ModelFluid;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid="harderores", name="HarderOres", version="{@version:ore}", dependencies = "required-after:hardlib;required-after:oreflowers")//@[{@version:lib},)  [{@version:flowers},)
public class OresBase {
	@Instance("harderores")
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
	public static Block sluice;
	
	public static Item rawOre;
	public static Item smallDust;
	public static Item largeDust;
	public static Item nuggets;
	
	public static Item diaStudPick;
	public static Item diaStudShovel;
	public static Item diaStudHoe;
	public static Item diaStudAxe;
	
	public static Item oreMinecart;
	
	public static Enchantment enchPulverize;
	public static Enchantment enchCracker;
	public static Enchantment enchProspector;
	
	public static ToolMaterial toolMaterialDiamondStud;
	public static EntityMinecart.Type oreCartEnum;
	
	public static Configuration config;

	public static SimpleNetworkWrapper networkWrapper;


	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		CapabilityMechanicalPower.register();
		HardLibAPI.oreMachines = new OreProcessingRecipes();
		
		oreIron = new BlockHardIron();
		EasyRegistry.registerBlockWithCustomItem(oreIron, new ItemOreBlock(oreIron), "hardiron");
		oreGold = new BlockHardGold();
		EasyRegistry.registerBlockWithCustomItem(oreGold, new ItemOreBlock(oreGold), "hardgold");
		oreDiamond = new BlockHardDiamond();
		EasyRegistry.registerBlockWithCustomItem(oreDiamond, new ItemOreBlock(oreDiamond), "harddiamond");
		millstone = new BlockMillstone();
		EasyRegistry.registerBlockWithItem(millstone, "millstone");
		GameRegistry.registerTileEntity(TileEntityMillstone.class, "millstone");
		axel = new BlockAxel();
		EasyRegistry.registerBlockWithItem(axel, "axel");
		GameRegistry.registerTileEntity(TileEntityAxel.class, "axel");
		windvane = new BlockWindvane();
		EasyRegistry.registerBlockWithItem(windvane, "windvane");
		sifter = new BlockSifter();
		EasyRegistry.registerBlockWithItem(sifter, "sifter");
		GameRegistry.registerTileEntity(TileEntitySifter.class, "sifter");
		sluice = new BlockSluice();
		EasyRegistry.registerBlockWithItem(sluice, "basic_sluice");
		GameRegistry.registerTileEntity(TileEntitySluice.class, "basic_sluice");
		
		rawOre = new ItemRawOre();
		EasyRegistry.registerItemWithVariants(rawOre, "orechunks", EnumOreType.IRON);
		smallDust = new ItemDustSmall();
		EasyRegistry.registerItemWithVariants(smallDust, "tinydust", EnumOreType.IRON);
		largeDust = new ItemDustLarge();
		EasyRegistry.registerItemWithVariants(largeDust, "largedust", EnumOreType.IRON);
		nuggets = new ItemNugget();
		EasyRegistry.registerItemWithVariants(nuggets, "nuggets", EnumOreType.IRON);
		
		toolMaterialDiamondStud = EnumHelper.addToolMaterial("DIAMOND_STUD", 3, 750, 7.0F, 2.0F, 5);
		toolMaterialDiamondStud.customCraftingMaterial = rawOre;

		EntityRegistry.registerModEntity(EntityOreMinecart.class, "oreMinecart", 0, this, 80, 3, true);
		
		diaStudPick = new ItemDiamondStudPickaxe(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudPick, "diamondstud_pickaxe");
		diaStudShovel = new ItemDiamondStudShovel(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudShovel, "diamondstud_shovel");
		diaStudHoe = new ItemDiamondStudHoe(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudHoe, "diamondstud_hoe");
		oreMinecart = new ItemEntityOreCart(oreCartEnum);
		EasyRegistry.registerItem(oreMinecart, "orecart");

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
        byte serverMessageID = 1;
        byte clientMessageID = 2;
		
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("harderores");
		//networkWrapper.registerMessage(PacketHandlerServer.class, ToServerMessage.class, serverMessageID, Side.SERVER);
		networkWrapper.registerMessage(ClientOreParticleHandler.class, ToClientMessageOreParticles.class, clientMessageID, Side.CLIENT);
		networkWrapper.registerMessage(ServerOreCartHandler.class, ToServerMessageOreCart.class, serverMessageID, Side.SERVER);
		
		FlowerIntegration.registerFlowerGen();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		/*Ore Dict*/
		OreDictionary.registerOre("rawOreChunkLimonite", new ItemStack(rawOre, 1, EnumOreType.LIMONITE.meta));
		OreDictionary.registerOre("rawOreChunkIron", new ItemStack(rawOre, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("rawOreChunkGold", new ItemStack(rawOre, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("rawOreChunkDiamond", new ItemStack(rawOre, 1, EnumOreType.DIAMOND.meta));
		
		OreDictionary.registerOre("dustTinyIron", new ItemStack(smallDust, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("dustTinyGold", new ItemStack(smallDust, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("dustTinyFlour", new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta));
		OreDictionary.registerOre("dustTinySugar", new ItemStack(smallDust, 1, EnumOreType.SUGAR.meta));

		OreDictionary.registerOre("nuggetIron", new ItemStack(nuggets, 1, EnumOreType.IRON.meta));
		
		/*Milling*/
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(rawOre,1,EnumOreType.IRON.meta), new ItemStack(smallDust,2,EnumOreType.IRON.meta));
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(rawOre,1,EnumOreType.GOLD.meta), new ItemStack(smallDust,2,EnumOreType.GOLD.meta));
		
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.IRON.meta), new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.GOLD.meta), new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.FLOUR.meta), new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));
		
		/*Smelting*/
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.LIMONITE.meta), new ItemStack(rawOre, 1, EnumOreType.IRON.meta), 0.05f);
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.IRON.meta), new ItemStack(nuggets, 1, EnumOreType.IRON.meta), 0.08f);
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_NUGGET, 1), 0.11f);
		GameRegistry.addSmelting(new ItemStack(smallDust, 1, EnumOreType.IRON.meta), new ItemStack(nuggets, 1, EnumOreType.IRON.meta), 0.08f);
		GameRegistry.addSmelting(new ItemStack(smallDust, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_NUGGET, 1), 0.11f);
		GameRegistry.addSmelting(new ItemStack(largeDust, 1, EnumOreType.IRON.meta), new ItemStack(Items.IRON_INGOT, 1), 0.7f);
		GameRegistry.addSmelting(new ItemStack(largeDust, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_INGOT, 1), 1.0f);

		/*Crafting*/
		RecipesUtil.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.IRON.meta), new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		RecipesUtil.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.GOLD.meta), new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		RecipesUtil.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta), new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));
		RecipesUtil.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.DIAMOND.meta), new ItemStack(Items.DIAMOND,1));
		RecipesUtil.craftNineOf(new ItemStack(nuggets, 1, EnumOreType.IRON.meta), new ItemStack(Items.IRON_INGOT, 1));
		GameRegistry.addRecipe(new ItemStack(nuggets, 9, EnumOreType.IRON.meta), "x",'x',new ItemStack(Items.IRON_INGOT, 1));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(millstone,9), 	true, "SSS","SWS","SSS", 'S', "stone", 'W', "logWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(sifter), 		true, "PBP","PbP", 'b', Items.BUCKET, 'P', "plankWood", 'B', Blocks.IRON_BARS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(windvane, 2), 	true, "SW", "SW", "SW", 'S', Items.STICK, 'W', Blocks.WOOL));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(axel, 2), 		true, "WWW", 'W', "logWood"));
		
		ItemStack diamondNugget = new ItemStack(rawOre,1,EnumOreType.DIAMOND.meta);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudPick), true, "dId", " s ", " s ", 's', Items.STICK, 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudAxe), true, "dI ", "Is ", " s ", 's', Items.STICK, 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudShovel), true, " d ", " I ", " s ", 's', Items.STICK, 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudHoe), true, "dI ", " s ", " s ", 's', Items.STICK, 'I', "ingotIron", 'd', diamondNugget));
		
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(new ItemStack(Items.BUCKET));
		list.add(new ItemStack(Items.MINECART));
		GameRegistry.addRecipe(new ShapelessRecipes(new ItemStack(oreMinecart), list));
		
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
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new OreGuiHandler());
		
		config.save();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
