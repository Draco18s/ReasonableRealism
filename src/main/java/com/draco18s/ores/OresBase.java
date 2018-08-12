package com.draco18s.ores;

import java.awt.Color;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.CogHelper;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.advancement.MillstoneTrigger;
import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.api.capability.CapabilityMechanicalPower;
import com.draco18s.hardlib.util.AdvancementUtils;
import com.draco18s.hardlib.util.RecipesUtils;
import com.draco18s.ores.block.BlockAxel;
import com.draco18s.ores.block.BlockDummyOre;
import com.draco18s.ores.block.BlockMillstone;
import com.draco18s.ores.block.BlockPackager;
import com.draco18s.ores.block.BlockSifter;
import com.draco18s.ores.block.BlockSluice;
import com.draco18s.ores.block.BlockWindvane;
import com.draco18s.ores.block.ore.BlockHardDiamond;
import com.draco18s.ores.block.ore.BlockHardOreBase;
import com.draco18s.ores.block.ore.BlockLimonite;
import com.draco18s.ores.enchantments.EnchantmentProspector;
import com.draco18s.ores.enchantments.EnchantmentPulverize;
import com.draco18s.ores.enchantments.EnchantmentShatter;
import com.draco18s.ores.enchantments.EnchantmentVeinCracker;
import com.draco18s.ores.entities.EntityOreMinecart;
import com.draco18s.ores.entities.TileEntityAxel;
import com.draco18s.ores.entities.TileEntityBasicSluice;
import com.draco18s.ores.entities.TileEntityMillstone;
import com.draco18s.ores.entities.TileEntityPackager;
import com.draco18s.ores.entities.TileEntitySifter;
import com.draco18s.ores.integration.FlowerIntegration;
import com.draco18s.ores.item.ItemDiamondStudAxe;
import com.draco18s.ores.item.ItemDiamondStudHoe;
import com.draco18s.ores.item.ItemDiamondStudPickaxe;
import com.draco18s.ores.item.ItemDiamondStudShovel;
import com.draco18s.ores.item.ItemDustLarge;
import com.draco18s.ores.item.ItemDustSmall;
import com.draco18s.ores.item.ItemEntityOreCart;
import com.draco18s.ores.item.ItemOreBlock;
import com.draco18s.ores.item.ItemRawOre;
import com.draco18s.ores.networking.ClientOreParticleHandler;
import com.draco18s.ores.networking.ServerOreCartHandler;
import com.draco18s.ores.networking.ToClientMessageOreParticles;
import com.draco18s.ores.networking.ToServerMessageOreCart;
import com.draco18s.ores.recipes.OreProcessingRecipes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid="harderores", name="HarderOres", version="{@version:ore}", dependencies = "required-after:hardlib;required-after:oreflowers")
public class OresBase {
	@Instance("harderores")
	public static OresBase instance;
	
	@SidedProxy(clientSide="com.draco18s.ores.client.ClientProxy", serverSide="com.draco18s.ores.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;
	
	public static Block oreLimonite;
	public static Block oreIron;
	public static Block oreGold;
	public static Block oreDiamond;
	public static Block oreTin;
	public static Block oreCopper;
	public static Block oreLead;
	public static Block oreUranium;

	public static Block oreSilver;
	public static Block oreNickel;
	public static Block oreAluminum;
	public static Block orePlatinum;
	public static Block oreZinc;
	//public static Block oreFluorite;
	//public static Block oreCadmium;
	//public static Block oreThorium;
	
	public static Block oreOsmium;
	public static Block oreQuartz;
	
	public static Block dummyOreIron;
	public static Block dummyOreGold;
	public static Block dummyOreDiamond;
	public static Block dummyOreTin;
	public static Block dummyOreCopper;
	public static Block dummyOreLead;
	public static Block dummyOreUranium;
	
	public static Block dummyOreSilver;
	public static Block dummyOreNickel;
	public static Block dummyOreAluminum;
	public static Block dummyOrePlatinum;
	public static Block dummyOreZinc;
	//public static Block dummyOreFluorite;
	//public static Block dummyOreCadmium;
	//public static Block dummyOreThorium;
	
	public static Block dummyOreOsmium;
	public static Block dummyOreQuartz;
	
	public static Block millstone;
	public static Block axel;
	public static Block windvane;
	public static Block sifter;
	public static Block sluice;
	public static Block pressurePackager;
	
	public static Item rawOre;
	public static Item smallDust;
	public static Item largeDust;
	//public static Item nuggets;
	
	public static Item diaStudPick;
	public static Item diaStudShovel;
	public static Item diaStudHoe;
	public static Item diaStudAxe;
	
	public static Item oreMinecart;
	
	public static Enchantment enchPulverize;
	public static Enchantment enchCracker;
	public static Enchantment enchShatter;
	public static Enchantment enchProspector;
	
	public static ToolMaterial toolMaterialDiamondStud;
	public static EntityMinecart.Type oreCartEnum;

	public static SimpleNetworkWrapper networkWrapper;
	
	public static Configuration config;

	public static boolean sluiceAllowDirt;
	public static boolean useSounds;
	private boolean stoneTools;


	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		CogHelper.addCogModule("HarderVanillaOres.xml");
		CogHelper.addCogModule("HarderExtraOres.xml");
		CogHelper.addCogModule("HarderLimonite.xml");
		CapabilityMechanicalPower.register();
		HardLibAPI.oreMachines = new OreProcessingRecipes();
		
		oreIron = new BlockHardOreBase(EnumOreType.IRON, 1, new Color(0xd8af93));
		oreIron.setHardness(6.0f).setHarvestLevel("pickaxe", 1);
		EasyRegistry.registerBlockWithCustomItem(oreIron, new ItemOreBlock(oreIron), "ore_hardiron");
		oreGold = new BlockHardOreBase(EnumOreType.GOLD, 1, new Color(0xfacf3b));
		oreGold.setHardness(9.0f).setHarvestLevel("pickaxe", 2);
		EasyRegistry.registerBlockWithCustomItem(oreGold, new ItemOreBlock(oreGold), "ore_hardgold");
		oreDiamond = new BlockHardDiamond();
		EasyRegistry.registerBlockWithCustomItem(oreDiamond, new ItemOreBlock(oreDiamond), "ore_harddiamond");
		oreLimonite = new BlockLimonite();
		EasyRegistry.registerBlockWithItem(oreLimonite, "ore_limonite");
		dummyOreIron = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreIron, "dummy_ore_iron");
		dummyOreGold = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreGold, "dummy_ore_gold");
		dummyOreDiamond = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreDiamond, "dummy_ore_diamond");
		
		oreTin = new BlockHardOreBase(EnumOreType.TIN, 1, new Color(0xdbdbbd));
		oreTin.setHardness(6.0f).setHarvestLevel("pickaxe", 1);
		EasyRegistry.registerBlockWithCustomItem(oreTin, new ItemOreBlock(oreTin), "ore_hardtin");
		dummyOreTin = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreTin, "dummy_ore_tin");
		oreCopper = new BlockHardOreBase(EnumOreType.COPPER, 1, new Color(0xa35c29));
		oreCopper.setHardness(6.0f).setHarvestLevel("pickaxe", 1);
		EasyRegistry.registerBlockWithCustomItem(oreCopper, new ItemOreBlock(oreCopper), "ore_hardcopper");
		dummyOreCopper = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreCopper, "dummy_ore_copper");
		oreLead = new BlockHardOreBase(EnumOreType.LEAD, 1, new Color(0xb9d6d9));
		oreLead.setHardness(6.0f).setHarvestLevel("pickaxe", 2);
		EasyRegistry.registerBlockWithCustomItem(oreLead, new ItemOreBlock(oreLead), "ore_hardlead");
		dummyOreLead = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreLead, "dummy_ore_lead");
		oreUranium = new BlockHardOreBase(EnumOreType.URANIUM, 1, new Color(0x74d513));
		oreUranium.setHardness(6.0f).setHarvestLevel("pickaxe", 2);
		EasyRegistry.registerBlockWithCustomItem(oreUranium, new ItemOreBlock(oreUranium), "ore_harduranium");
		dummyOreUranium = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreUranium, "dummy_ore_uranium");
		
		oreSilver = new BlockHardOreBase(EnumOreType.SILVER, 1, new Color(0xaccdf1));
		oreSilver.setHardness(4.0f).setHarvestLevel("pickaxe", 2);
		EasyRegistry.registerBlockWithCustomItem(oreSilver, new ItemOreBlock(oreSilver), "ore_hardsilver");
		dummyOreSilver = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreSilver, "dummy_ore_silver");
		oreNickel = new BlockHardOreBase(EnumOreType.NICKEL, 1, new Color(0xe6e4b6));
		oreNickel.setHardness(4.0f).setHarvestLevel("pickaxe", 2);
		EasyRegistry.registerBlockWithCustomItem(oreNickel, new ItemOreBlock(oreNickel), "ore_hardnickel");
		dummyOreNickel = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreNickel, "dummy_ore_nickel");
		oreAluminum = new BlockHardOreBase(EnumOreType.ALUMINUM, 1, new Color(0x524032));
		oreAluminum.setHardness(3.0f).setHarvestLevel("shovel", 1);
		EasyRegistry.registerBlockWithCustomItem(oreAluminum, new ItemOreBlock(oreAluminum), "ore_hardbauxite");
		dummyOreAluminum = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreAluminum, "dummy_ore_bauxite");
		orePlatinum = new BlockHardOreBase(EnumOreType.PLATINUM, 1, new Color(0x0ecaf0));
		orePlatinum.setHardness(5.0f).setHarvestLevel("pickaxe", 2);
		EasyRegistry.registerBlockWithCustomItem(orePlatinum, new ItemOreBlock(orePlatinum), "ore_hardplatinum");
		dummyOrePlatinum = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOrePlatinum, "dummy_ore_platinum");
		oreZinc = new BlockHardOreBase(EnumOreType.ZINC, 1, new Color(0xc3c3c3));
		oreZinc.setHardness(5.0f).setHarvestLevel("pickaxe", 1);
		EasyRegistry.registerBlockWithCustomItem(oreZinc, new ItemOreBlock(oreZinc), "ore_hardzinc");
		dummyOreZinc = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreZinc, "dummy_ore_zinc");
		oreQuartz = new BlockHardOreBase(EnumOreType.QUARTZ, 1, new Color(0xd6e8f2));
		oreQuartz.setHardness(2.0f).setHarvestLevel("pickaxe", 1);
		EasyRegistry.registerBlockWithCustomItem(oreQuartz, new ItemOreBlock(oreQuartz), "ore_hardquartz");
		dummyOreQuartz = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreQuartz, "dummy_ore_quartz");

		oreOsmium = new BlockHardOreBase(EnumOreType.OSMIUM, 1, new Color(0x466ec8));
		oreOsmium.setHardness(6.0f).setHarvestLevel("pickaxe", 1);
		EasyRegistry.registerBlockWithCustomItem(oreOsmium, new ItemOreBlock(oreOsmium), "ore_hardosmium");
		dummyOreOsmium = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreOsmium, "dummy_ore_osmium");
		
		millstone = new BlockMillstone();
		EasyRegistry.registerBlockWithItem(millstone, "millstone");
		GameRegistry.registerTileEntity(TileEntityMillstone.class, "harderores:millstone");
		axel = new BlockAxel();
		EasyRegistry.registerBlockWithItem(axel, "axel");
		GameRegistry.registerTileEntity(TileEntityAxel.class, "harderores:axel");
		windvane = new BlockWindvane();
		EasyRegistry.registerBlockWithItem(windvane, "windvane");
		sifter = new BlockSifter();
		EasyRegistry.registerBlockWithItem(sifter, "sifter");
		GameRegistry.registerTileEntity(TileEntitySifter.class, "harderores:sifter");
		sluice = new BlockSluice();
		EasyRegistry.registerBlockWithItem(sluice, "basic_sluice");
		GameRegistry.registerTileEntity(TileEntityBasicSluice.class, "harderores:basic_sluice");
		pressurePackager = new BlockPackager();
		EasyRegistry.registerBlockWithItem(pressurePackager, "packager");
		GameRegistry.registerTileEntity(TileEntityPackager.class, "harderores:packager");
		
		rawOre = new ItemRawOre();
		EasyRegistry.registerItemWithVariants(rawOre, "orechunks", EnumOreType.IRON);
		smallDust = new ItemDustSmall();
		EasyRegistry.registerItemWithVariants(smallDust, "tinydust", EnumOreType.IRON);
		largeDust = new ItemDustLarge();
		EasyRegistry.registerItemWithVariants(largeDust, "largedust", EnumOreType.IRON);
		//nuggets = new ItemNugget();
		//EasyRegistry.registerItemWithVariants(nuggets, "nuggets", EnumOreType.IRON);
		
		toolMaterialDiamondStud = EnumHelper.addToolMaterial("DIAMOND_STUD", 3, 750, 7.0F, 2.0F, 5);
		toolMaterialDiamondStud.setRepairItem(new ItemStack(OresBase.rawOre, 1, EnumOreType.DIAMOND.meta));

		EntityRegistry.registerModEntity(new ResourceLocation("harderores:oreMinecart"), EntityOreMinecart.class, "harderores:oreMinecart", 0, this, 80, 3, true);
		//EntityRegistry.registerModEntity(EntityOreMinecart.class, "harderores:oreMinecart", 0, this, 80, 3, true);

		diaStudPick = new ItemDiamondStudPickaxe(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudPick, "diamondstud_pickaxe");
		diaStudShovel = new ItemDiamondStudShovel(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudShovel, "diamondstud_shovel");
		diaStudHoe = new ItemDiamondStudHoe(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudHoe, "diamondstud_hoe");
		diaStudAxe = new ItemDiamondStudAxe(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudAxe, "diamondstud_axe");
		oreMinecart = new ItemEntityOreCart(oreCartEnum);
		EasyRegistry.registerItem(oreMinecart, "orecart");

		EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND};
		enchPulverize = new EnchantmentPulverize(slots);
		enchPulverize.setRegistryName("pulverize");
		enchPulverize.setName(enchPulverize.getRegistryName().toString());
		EasyRegistry.registerOther(enchPulverize);
		//GameRegistry.register(enchPulverize);
		
		enchCracker = new EnchantmentVeinCracker(slots);
		enchCracker.setRegistryName("cracker");
		enchCracker.setName(enchCracker.getRegistryName().toString());
		EasyRegistry.registerOther(enchCracker);
		//GameRegistry.register(enchCracker);
		
		enchShatter = new EnchantmentShatter(slots);
		enchShatter.setRegistryName("shatter");
		enchShatter.setName(enchShatter.getRegistryName().toString());
		EasyRegistry.registerOther(enchShatter);
		//GameRegistry.register(enchShatter);

		slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.OFFHAND};
		enchProspector = new EnchantmentProspector(slots);
		enchProspector.setRegistryName("prospector");
		enchProspector.setName(enchProspector.getRegistryName().toString());
		EasyRegistry.registerOther(enchProspector);
		//GameRegistry.register(enchProspector);
		
		proxy.registerEventHandlers();
		proxy.registerRenderers();
		
		//These have to be unique
		byte serverMessageID = 1;
		byte clientMessageID = 2;
		
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("harderores");

		//networkWrapper.registerMessage(PacketHandlerServer.class, ToServerMessage.class, serverMessageID, Side.SERVER);
		OresBase.networkWrapper.registerMessage(ServerOreCartHandler.class, ToServerMessageOreCart.class, serverMessageID, Side.SERVER);
		OresBase.networkWrapper.registerMessage(ClientOreParticleHandler.class, ToClientMessageOreParticles.class, clientMessageID, Side.CLIENT);
		
		FlowerIntegration.registerFlowerGen();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		/*Ore Dict*/
		OreDictionary.registerOre("rawOreChunkLimonite", new ItemStack(rawOre, 1, EnumOreType.LIMONITE.meta));
		OreDictionary.registerOre("rawOreChunkIron", new ItemStack(rawOre, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("rawOreChunkGold", new ItemStack(rawOre, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("rawOreChunkDiamond", new ItemStack(rawOre, 1, EnumOreType.DIAMOND.meta));
		OreDictionary.registerOre("rawOreChunkTin", new ItemStack(rawOre, 1, EnumOreType.TIN.meta));
		OreDictionary.registerOre("rawOreChunkCopper", new ItemStack(rawOre, 1, EnumOreType.COPPER.meta));
		OreDictionary.registerOre("rawOreChunkLead", new ItemStack(rawOre, 1, EnumOreType.LEAD.meta));
		OreDictionary.registerOre("rawOreChunkUranium", new ItemStack(rawOre, 1, EnumOreType.URANIUM.meta));
		OreDictionary.registerOre("rawOreChunkSilver", new ItemStack(rawOre, 1, EnumOreType.SILVER.meta));
		OreDictionary.registerOre("rawOreChunkNickel", new ItemStack(rawOre, 1, EnumOreType.NICKEL.meta));
		OreDictionary.registerOre("rawOreChunkAluminum", new ItemStack(rawOre, 1, EnumOreType.ALUMINUM.meta));
		OreDictionary.registerOre("rawOreChunkPlatinum", new ItemStack(rawOre, 1, EnumOreType.PLATINUM.meta));
		OreDictionary.registerOre("rawOreChunkZinc", new ItemStack(rawOre, 1, EnumOreType.ZINC.meta));
		OreDictionary.registerOre("rawOreChunkOsmium", new ItemStack(rawOre, 1, EnumOreType.OSMIUM.meta));
		OreDictionary.registerOre("gemBlueQuartz", new ItemStack(rawOre, 1, EnumOreType.QUARTZ.meta));
		
		OreDictionary.registerOre("dustTinyIron", new ItemStack(smallDust, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("dustTinyGold", new ItemStack(smallDust, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("dustTinyFlour", new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta));
		OreDictionary.registerOre("dustTinySugar", new ItemStack(smallDust, 1, EnumOreType.SUGAR.meta));
		OreDictionary.registerOre("dustTinyTin", new ItemStack(smallDust, 1, EnumOreType.TIN.meta));
		OreDictionary.registerOre("dustTinyCopper", new ItemStack(smallDust, 1, EnumOreType.COPPER.meta));
		OreDictionary.registerOre("dustTinyLead", new ItemStack(smallDust, 1, EnumOreType.LEAD.meta));
		OreDictionary.registerOre("dustTinySilver", new ItemStack(smallDust, 1, EnumOreType.SILVER.meta));
		OreDictionary.registerOre("dustTinyNickel", new ItemStack(smallDust, 1, EnumOreType.NICKEL.meta));
		OreDictionary.registerOre("dustTinyAluminum", new ItemStack(smallDust, 1, EnumOreType.ALUMINUM.meta));
		OreDictionary.registerOre("dustTinyPlatinum", new ItemStack(smallDust, 1, EnumOreType.PLATINUM.meta));
		OreDictionary.registerOre("dustTinyZinc", new ItemStack(smallDust, 1, EnumOreType.ZINC.meta));
		OreDictionary.registerOre("dustTinyOsmium", new ItemStack(smallDust, 1, EnumOreType.OSMIUM.meta));
		
		OreDictionary.registerOre("dustIron", new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("dustGold", new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("dustFlour", new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));

		//OreDictionary.registerOre("nuggetIron", new ItemStack(nuggets, 1, EnumOreType.IRON.meta));
		
		OreDictionary.registerOre("oreIronHard", oreIron);
		OreDictionary.registerOre("oreGoldHard", oreGold);
		OreDictionary.registerOre("oreDiamondHard", oreDiamond);
		
		OreDictionary.registerOre("oreIron", dummyOreIron);
		OreDictionary.registerOre("oreGold", dummyOreGold);
		OreDictionary.registerOre("oreDiamond", dummyOreDiamond);
		
		/*Smelting*/
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.LIMONITE.meta), new ItemStack(rawOre, 1, EnumOreType.IRON.meta), 0.05f);
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.IRON.meta), new ItemStack(Items.IRON_NUGGET), 0.08f);
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_NUGGET, 1), 0.11f);
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.QUARTZ.meta), new ItemStack(Blocks.GLASS_PANE, 1), 0.11f);
		GameRegistry.addSmelting(new ItemStack(smallDust, 1, EnumOreType.IRON.meta), new ItemStack(Items.IRON_NUGGET), 0.08f);
		GameRegistry.addSmelting(new ItemStack(smallDust, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_NUGGET, 1), 0.11f);
		GameRegistry.addSmelting(new ItemStack(largeDust, 1, EnumOreType.IRON.meta), new ItemStack(Items.IRON_INGOT, 1), 0.7f);
		GameRegistry.addSmelting(new ItemStack(largeDust, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_INGOT, 1), 1.0f);
		GameRegistry.addSmelting(dummyOreIron, new ItemStack(Items.IRON_INGOT), 0.7f);
		GameRegistry.addSmelting(dummyOreGold, new ItemStack(Items.GOLD_INGOT), 1.0f);
		GameRegistry.addSmelting(dummyOreDiamond, new ItemStack(Items.DIAMOND), 1.0f);
		GameRegistry.addSmelting(dummyOreQuartz, new ItemStack(Blocks.GLASS, 3), 1.0f);
		
		/*Crafting*/
		/*RecipesUtils.craftNineOf(new ItemStack(rawOre, 1, EnumOreType.DIAMOND.meta), new ItemStack(Items.DIAMOND,1));
		RecipesUtils.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.IRON.meta), new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		RecipesUtils.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.GOLD.meta), new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		RecipesUtils.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta), new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));
		RecipesUtils.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.SUGAR.meta), new ItemStack(Items.SUGAR));*/
		
		useSounds = config.getBoolean("Use Sounds", "GENERAL", true, "If true, then the millstone will make noise.");
		/*Sluicing*/
		sluiceAllowDirt = config.getBoolean("sluiceAllowsDirt","SLUICE", false, "Set to true to allow dirt to be used in the sluice.");
		int cycle = config.getInt("sluiceCycleTime", "SLUICE", 2, 1, 20, "Time it takes for the sluice to make 1 operation.  This value is multiplied by 75 ticks.");
		//TileEntitySluice.cycleLength = cycle * 15;
		TileEntityBasicSluice.cycleLength = cycle * 15;
		
		if(config.get("SLUICE", "canFindIron", true).getBoolean()) {
			HardLibAPI.oreMachines.addSluiceRecipe(oreIron);
			HardLibAPI.oreMachines.addSluiceRecipe(oreIron);
			HardLibAPI.oreMachines.addSluiceRecipe(oreIron);
		}
		if(config.get("SLUICE", "canFindGold", true).getBoolean()) {
			HardLibAPI.oreMachines.addSluiceRecipe(oreGold);
			HardLibAPI.oreMachines.addSluiceRecipe(oreGold);
			HardLibAPI.oreMachines.addSluiceRecipe(oreGold);
		}
		if(config.get("SLUICE", "canFindDiamond", false).getBoolean()) {
			HardLibAPI.oreMachines.addSluiceRecipe(oreDiamond);
		}
		if(config.get("SLUICE", "canFindRedstone", true).getBoolean()) {
			HardLibAPI.oreMachines.addSluiceRecipe(Blocks.REDSTONE_ORE);
			HardLibAPI.oreMachines.addSluiceRecipe(Blocks.REDSTONE_ORE);
		}
		
		/*Milling*/
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(rawOre,1,EnumOreType.IRON.meta), new ItemStack(smallDust,2,EnumOreType.IRON.meta));
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(rawOre,1,EnumOreType.GOLD.meta), new ItemStack(smallDust,2,EnumOreType.GOLD.meta));
		//Mod ores handled by addExtraOre()
		
		config.addCustomCategoryComment("MILLING", "Enable (hard mode) these to remove vanilla recipes for items and instead require the millstone. In general,\neasy means the millstone doubles resources, while hard is near-vanilla.");
		boolean hardOption = config.getBoolean("RequireMillingFlour", "MILLING", false, "");

		String oreIn = "dustFlour";
		if(hardOption) {
			//hard: wheat is ground to "4/9th flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT), new ItemStack(smallDust, 4, EnumOreType.FLOUR.meta));
			//hard: seeds are ground to "1/9ths flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta));
		}
		else {
			//easy: wheat is ground to "2 flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT), new ItemStack(smallDust, 18, EnumOreType.FLOUR.meta));
			//easy: seeds are ground to "2/9ths flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(smallDust, 2, EnumOreType.FLOUR.meta));
		}
		
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
			RecipesUtils.RemoveRecipe(Items.DYE, 3, EnumDyeColor.WHITE.getDyeDamage(), "Hard Ores");
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.BONE), new ItemStack(Items.DYE, 2, EnumDyeColor.WHITE.getDyeDamage()));
		}
		else {
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.BONE), new ItemStack(Items.DYE, 4, EnumDyeColor.WHITE.getDyeDamage()));
		}

		/*Sifting*/
		ItemStack bonemeal = new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage());
		HardLibAPI.oreMachines.addSiftRecipe(bonemeal, bonemeal, false);
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.IRON.meta), new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.GOLD.meta), new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.FLOUR.meta), new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.SUGAR.meta), new ItemStack(Items.SUGAR, 1));
		//Mod ores handled by addExtraOre()
		
		/*Packing*/
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.IRON.meta), new ItemStack(dummyOreIron));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.GOLD.meta), new ItemStack(dummyOreGold));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.DIAMOND.meta), new ItemStack(dummyOreDiamond));
		//Mod ores handled by addExtraOre()
		addPressurePackRecipes();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new OreGuiHandler());
		
		config.save();
	}

	private void addPressurePackRecipes() {
		List<IRecipe> allStacks = RecipesUtils.getAllStorageRecipes();
		logger.log(Level.WARN, "allStacks size: " + allStacks.size());
		
		for(IRecipe recip : allStacks) {
			if(!HardLibAPI.oreMachines.getSiftResult(recip.getRecipeOutput(), false).isEmpty()) continue;
			
			Ingredient ingred = null;
			for(Ingredient s : recip.getIngredients()) {
				if(s != Ingredient.EMPTY) {
					ingred = s;
				}
			}
			ItemStack[] stacks = ingred.getMatchingStacks();

			if(!HardLibAPI.oreMachines.getPressurePackResult(stacks[0], false).isEmpty()) continue;
			if(recip.getRecipeOutput().getItem() == Items.LEATHER) continue;
			
			logger.log(Level.WARN, "    " + recip.getRecipeOutput().getDisplayName());
			logger.log(Level.WARN, "    " + stacks.length + ", [" + stacks[0].getDisplayName() + "]");
			
			HardLibAPI.oreMachines.addPressurePackRecipe(recip.getRecipeOutput(), stacks[0]);
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		List<ItemStack> oreDictReq;
		int addedOres = 9;
		oreDictReq = OreDictionary.getOres("oreTin");
		if(oreDictReq.size() > 0) {
			addExtraOre("Tin", EnumOreType.TIN, oreTin, dummyOreTin, 3, true);
			addedOres += 3;
		}
		oreDictReq = OreDictionary.getOres("oreCopper");
		if(oreDictReq.size() > 0) {
			addExtraOre("Copper", EnumOreType.COPPER, oreCopper, dummyOreCopper, 3, true);
			addedOres += 3;
		}
		oreDictReq = OreDictionary.getOres("oreLead");
		if(oreDictReq.size() > 0) {
			addExtraOre("Lead", EnumOreType.LEAD, oreLead, dummyOreLead, 2, true);
			addedOres += 2;
		}
		oreDictReq = OreDictionary.getOres("oreUranium");
		if(oreDictReq.size() > 0) {
			addExtraOre("Uranium", EnumOreType.URANIUM, oreUranium, dummyOreUranium, 1, false);
			addedOres += 1;
		}
		oreDictReq = OreDictionary.getOres("oreSilver");
		if(oreDictReq.size() > 0) {
			addExtraOre("Silver", EnumOreType.SILVER, oreSilver, dummyOreSilver, 3, true);
			addedOres += 3;
		}
		oreDictReq = OreDictionary.getOres("oreNickel");
		if(oreDictReq.size() > 0) {
			addExtraOre("Nickel", EnumOreType.NICKEL, oreNickel, dummyOreNickel, 3, true);
			addedOres += 3;
		}
		oreDictReq = OreDictionary.getOres("oreAluminum");
		if(oreDictReq.size() > 0) {
			addExtraOre("Aluminum", EnumOreType.ALUMINUM, oreAluminum, dummyOreAluminum, 1, true);
			addedOres += 1;
		}
		oreDictReq = OreDictionary.getOres("orePlatinum");
		if(oreDictReq.size() > 0) {
			addExtraOre("Platinum", EnumOreType.PLATINUM, orePlatinum, dummyOrePlatinum, 2, true);
			addedOres += 2;
		}
		oreDictReq = OreDictionary.getOres("oreOsmium");
		if(oreDictReq.size() > 0) {
			addExtraOre("Osmium", EnumOreType.OSMIUM, oreOsmium, dummyOreOsmium, 2, false);
			addedOres += 2;
		}
		oreDictReq = OreDictionary.getOres("oreZinc");
		if(oreDictReq.size() > 0) {
			addExtraOre("Zinc", EnumOreType.ZINC, oreZinc, dummyOreZinc, 2, true);
			addedOres += 2;
			addExtraOre("Quartz", EnumOreType.QUARTZ, oreQuartz, dummyOreQuartz, 1, true);
			addedOres += 1;
		}
		//one quarter of the entries should be gravel
		for(;addedOres>0;addedOres -= 3) {
			HardLibAPI.oreMachines.addSluiceRecipe(Blocks.GRAVEL);
		}
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		AdvancementUtils.reparentAdvancement(new ResourceLocation("minecraft:story/upgrade_tools"), new ResourceLocation("harderores:alternate_stone"), "Harder Ores", true);
		
		
		boolean hardOption = OresBase.config.getBoolean("RequireMillingFlour", "MILLING", false, "");
		if(hardOption) {

		}
		boolean stoneTools = OresBase.config.getBoolean("useDioriteStoneTools", "GENERAL", true, "If true, cobblestone cannot be used to create stone tools,\ninstead diorite is used. This prolongs the life of wood tools so it isn't \"make a wood pickaxe to\nmine 3 stone and upgrade.\"");
		if(stoneTools) {
			AdvancementUtils.removeAdvancement(new ResourceLocation("minecraft:recipes/tools/stone_axe"), "Harder Ores");
			AdvancementUtils.removeAdvancement(new ResourceLocation("minecraft:recipes/tools/stone_pickaxe"), "Harder Ores");
			AdvancementUtils.removeAdvancement(new ResourceLocation("minecraft:recipes/tools/stone_shovel"), "Harder Ores");
			AdvancementUtils.removeAdvancement(new ResourceLocation("minecraft:recipes/tools/stone_hoe"), "Harder Ores");
		}
	}
	
	private void addExtraOre(String oreName, EnumOreType oreType, Block oreBlock, Block dummyOre, int sluiceWeight, boolean canFindDefault) {
		if(!oreType.set) {
			if(config.get("SLUICE", "canFind"+oreName, canFindDefault).getBoolean()) {
				for(int i = sluiceWeight; i > 0; i--) {
					HardLibAPI.oreMachines.addSluiceRecipe(oreBlock);
				}
			}
			String oreIn = "dustTiny"+oreName;
			ItemStack rawOreIn = new ItemStack(rawOre, 1, oreType.meta);
			List<ItemStack> dustStack = OreDictionary.getOres("dust"+oreName);
			List<ItemStack> tinyDusktStack = OreDictionary.getOres(oreIn);
			List<ItemStack> nuggetStack = OreDictionary.getOres("nugget"+oreName);
			if(dustStack.size() > 0 && tinyDusktStack.size() > 0) {
				ItemStack instk = tinyDusktStack.get(0).copy();
				if(nuggetStack.size() > 0) {
					GameRegistry.addSmelting(instk, nuggetStack.get(0), 0.1f);
					GameRegistry.addSmelting(rawOreIn, nuggetStack.get(0), 0.1f);
				}
				instk.setCount(2);
				HardLibAPI.oreMachines.addMillRecipe(rawOreIn, instk);
				HardLibAPI.oreMachines.addSiftRecipe(oreIn, 8, dustStack.get(0));
			}
			List<ItemStack> ingotStack = OreDictionary.getOres("ingot"+oreName);
			if(ingotStack.size() > 0) {
				GameRegistry.addSmelting(dummyOre, ingotStack.get(0), 0.7f);
			}
			HardLibAPI.oreMachines.addPressurePackRecipe(rawOreIn, new ItemStack(dummyOre));
			OreDictionary.registerOre("ore"+oreName, dummyOre);
			OreDictionary.registerOre("ore"+oreName+"Hard", oreBlock);
			oreType.set = true;
		}
	}
}
