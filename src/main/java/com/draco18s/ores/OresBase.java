package com.draco18s.ores;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.CogHelper;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.capability.CapabilityMechanicalPower;
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
import com.draco18s.ores.enchantments.EnchantmentVeinCracker;
import com.draco18s.ores.entities.EntityOreMinecart;
import com.draco18s.ores.entities.TileEntityAxel;
import com.draco18s.ores.entities.TileEntityBasicSluice;
import com.draco18s.ores.entities.TileEntityMillstone;
import com.draco18s.ores.entities.TileEntityPackager;
import com.draco18s.ores.entities.TileEntitySifter;
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
import com.draco18s.ores.util.OresAchievements;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
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
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid="harderores", name="HarderOres", version="{@version:ore}", dependencies = "required-after:hardlib;required-after:oreflowers")//@[{@version:lib},)  [{@version:flowers},)
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
	
	public static Block dummyOreIron;
	public static Block dummyOreGold;
	public static Block dummyOreDiamond;
	public static Block dummyOreTin;
	public static Block dummyOreCopper;
	public static Block dummyOreLead;
	public static Block dummyOreUranium;
	
	public static Block millstone;
	public static Block axel;
	public static Block windvane;
	public static Block sifter;
	public static Block sluice;
	public static Block pressurePackager;
	
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

	public static boolean sluiceAllowDirt;

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
		
		oreTin = new BlockHardOreBase(EnumOreType.TIN, 1, new Color(0xc3c3c3));
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
		GameRegistry.registerTileEntity(TileEntityBasicSluice.class, "basic_sluice");
		pressurePackager = new BlockPackager();
		EasyRegistry.registerBlockWithItem(pressurePackager, "packager");
		GameRegistry.registerTileEntity(TileEntityPackager.class, "packager");
		
		rawOre = new ItemRawOre();
		EasyRegistry.registerItemWithVariants(rawOre, "orechunks", EnumOreType.IRON);
		smallDust = new ItemDustSmall();
		EasyRegistry.registerItemWithVariants(smallDust, "tinydust", EnumOreType.IRON);
		largeDust = new ItemDustLarge();
		EasyRegistry.registerItemWithVariants(largeDust, "largedust", EnumOreType.IRON);
		nuggets = new ItemNugget();
		EasyRegistry.registerItemWithVariants(nuggets, "nuggets", EnumOreType.IRON);
		
		toolMaterialDiamondStud = EnumHelper.addToolMaterial("DIAMOND_STUD", 3, 750, 7.0F, 2.0F, 5);
		toolMaterialDiamondStud.setRepairItem(new ItemStack(OresBase.rawOre, 1, EnumOreType.DIAMOND.meta));

		EntityRegistry.registerModEntity(EntityOreMinecart.class, "oreMinecart", 0, this, 80, 3, true);
		
		diaStudPick = new ItemDiamondStudPickaxe(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudPick, "diamondstud_pickaxe");
		diaStudShovel = new ItemDiamondStudShovel(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudShovel, "diamondstud_shovel");
		diaStudHoe = new ItemDiamondStudHoe(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudHoe, "diamondstud_hoe");
		diaStudAxe = new ItemDiamondStudHoe(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudAxe, "diamondstud_axe");
		oreMinecart = new ItemEntityOreCart(oreCartEnum);
		EasyRegistry.registerItem(oreMinecart, "orecart");

		EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND};
		enchPulverize = new EnchantmentPulverize(slots);
		enchPulverize.setRegistryName("pulverize");
		enchPulverize.setName(enchPulverize.getRegistryName().toString());
		GameRegistry.register(enchPulverize);
		
		enchCracker = new EnchantmentVeinCracker(slots);
		enchCracker.setRegistryName("cracker");
		enchCracker.setName(enchCracker.getRegistryName().toString());
		GameRegistry.register(enchCracker);

		slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.OFFHAND};
		enchProspector = new EnchantmentProspector(slots);
		enchProspector.setRegistryName("prospector");
		enchProspector.setName(enchProspector.getRegistryName().toString());
		GameRegistry.register(enchProspector);
		
		proxy.registerEventHandlers();
		proxy.registerRenderers();
		
        //These have to be unique
		
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("harderores");
		//networkWrapper.registerMessage(PacketHandlerServer.class, ToServerMessage.class, serverMessageID, Side.SERVER);
		proxy.registerNetwork();
        
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
		
		OreDictionary.registerOre("dustTinyIron", new ItemStack(smallDust, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("dustTinyGold", new ItemStack(smallDust, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("dustTinyFlour", new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta));
		OreDictionary.registerOre("dustTinySugar", new ItemStack(smallDust, 1, EnumOreType.SUGAR.meta));
		OreDictionary.registerOre("dustTinyTin", new ItemStack(smallDust, 1, EnumOreType.TIN.meta));
		OreDictionary.registerOre("dustTinyCopper", new ItemStack(smallDust, 1, EnumOreType.COPPER.meta));
		OreDictionary.registerOre("dustTinyLead", new ItemStack(smallDust, 1, EnumOreType.LEAD.meta));
		
		OreDictionary.registerOre("dustIron", new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("dustGold", new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("dustFlour", new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));

		OreDictionary.registerOre("nuggetIron", new ItemStack(nuggets, 1, EnumOreType.IRON.meta));
		
		OreDictionary.registerOre("oreIron", dummyOreIron);
		OreDictionary.registerOre("oreGold", dummyOreGold);
		OreDictionary.registerOre("oreDiamond", dummyOreDiamond);
		
		/*Smelting*/
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.LIMONITE.meta), new ItemStack(rawOre, 1, EnumOreType.IRON.meta), 0.05f);
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.IRON.meta), new ItemStack(nuggets, 1, EnumOreType.IRON.meta), 0.08f);
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_NUGGET, 1), 0.11f);
		GameRegistry.addSmelting(new ItemStack(smallDust, 1, EnumOreType.IRON.meta), new ItemStack(nuggets, 1, EnumOreType.IRON.meta), 0.08f);
		GameRegistry.addSmelting(new ItemStack(smallDust, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_NUGGET, 1), 0.11f);
		GameRegistry.addSmelting(new ItemStack(largeDust, 1, EnumOreType.IRON.meta), new ItemStack(Items.IRON_INGOT, 1), 0.7f);
		GameRegistry.addSmelting(new ItemStack(largeDust, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_INGOT, 1), 1.0f);
		GameRegistry.addSmelting(dummyOreIron, new ItemStack(Items.IRON_INGOT), 0.7f);
		GameRegistry.addSmelting(dummyOreGold, new ItemStack(Items.GOLD_INGOT), 1.0f);
		GameRegistry.addSmelting(dummyOreDiamond, new ItemStack(Items.DIAMOND), 1.0f);
		
		/*Crafting*/
		RecipesUtils.craftNineOf(new ItemStack(rawOre, 1, EnumOreType.DIAMOND.meta), new ItemStack(Items.DIAMOND,1));
		RecipesUtils.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.IRON.meta), new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		RecipesUtils.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.GOLD.meta), new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		RecipesUtils.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta), new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));
		RecipesUtils.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.SUGAR.meta), new ItemStack(Items.SUGAR));
		RecipesUtils.craftNineOf(new ItemStack(nuggets, 1, EnumOreType.IRON.meta), new ItemStack(Items.IRON_INGOT, 1));
		GameRegistry.addRecipe(new ItemStack(rawOre, 9, EnumOreType.IRON.meta), "x", 'x', new ItemStack(dummyOreIron));
		GameRegistry.addRecipe(new ItemStack(rawOre, 9, EnumOreType.GOLD.meta), "x", 'x', new ItemStack(dummyOreGold));
		GameRegistry.addRecipe(new ItemStack(rawOre, 9, EnumOreType.DIAMOND.meta), "x", 'x', new ItemStack(dummyOreDiamond));
		GameRegistry.addRecipe(new ItemStack(nuggets, 9, EnumOreType.IRON.meta), "x",'x',new ItemStack(Items.IRON_INGOT, 1));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(millstone,9), 	true, "SSS","SWS","SSS", 'S', "stone", 'W', "logWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(sifter), 		true, "PBP","PbP", 'b', Items.BUCKET, 'P', "plankWood", 'B', Blocks.IRON_BARS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(windvane, 2), 	true, "SW", "SW", "SW", 'S', "stickWood", 'W', Blocks.WOOL));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(axel, 2), 		true, "WWW", 'W', "logWood"));
		
		ItemStack diamondNugget = new ItemStack(rawOre,1,EnumOreType.DIAMOND.meta);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudPick), true, "dId", " s ", " s ", 's', "stickWood", 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudAxe), true, "dI ", "Is ", " s ", 's', "stickWood", 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudShovel), true, " d ", " I ", " s ", 's', "stickWood", 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudHoe), true, "dI ", " s ", " s ", 's', "stickWood", 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(sluice), true, "sss","ppp",'s',"stickWood",'p',"slabWood"));
		
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(new ItemStack(Items.BUCKET));
		list.add(new ItemStack(Items.MINECART));
		GameRegistry.addRecipe(new ShapelessRecipes(new ItemStack(oreMinecart), list));
		stoneTools = config.getBoolean("useDioriteStoneTools", "GENERAL", true, "If true, cobblestone cannot be used to create stone tools,\ninstead diorite is used. This prolongs the life of wood tools so it isn't \"make a wood pickaxe to\nmine 3 stone and upgrade.\"");
		if(stoneTools) {
			RecipesUtils.RemoveRecipe(Items.STONE_AXE, 1, 0, "Hard Ores");
			RecipesUtils.RemoveRecipe(Items.STONE_PICKAXE, 1, 0, "Hard Ores");
			RecipesUtils.RemoveRecipe(Items.STONE_SHOVEL, 1, 0, "Hard Ores");
			RecipesUtils.RemoveRecipe(Items.STONE_HOE, 1, 0, "Hard Ores");
			
			ItemStack toolmat = new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.DIORITE.getMetadata());
			GameRegistry.addRecipe(new ItemStack(Items.STONE_PICKAXE), new Object[] {"III", " s ", " s ", 's', Items.STICK, 'I', toolmat});
	        GameRegistry.addRecipe(new ItemStack(Items.STONE_AXE), new Object[] {"II ", "Is ", " s ", 's', Items.STICK, 'I', toolmat});
	        GameRegistry.addRecipe(new ItemStack(Items.STONE_SHOVEL), new Object[] {" I ", " s ", " s ", 's', Items.STICK, 'I', toolmat});
	        GameRegistry.addRecipe(new ItemStack(Items.STONE_HOE), new Object[] {"II ", " s ", " s ", 's', Items.STICK, 'I', toolmat});
	    }
		/*Sluicing*/
		sluiceAllowDirt = config.getBoolean("sluiceAllowsDirt","SLUICE", false, "Set to true to allow dirt to be used in the sluice.");
		int cycle = config.getInt("sluiceCycleTime", "SLUICE", 2, 1, 20, "Time it takes for the sluice to make 1 operation.  This value is multiplied by 75 ticks.");
		//TileEntitySluice.cycleLength = cycle * 15;
		TileEntityBasicSluice.cycleLength = cycle * 15;
		
		HardLibAPI.oreMachines.addSluiceRecipe(Blocks.GRAVEL);
		HardLibAPI.oreMachines.addSluiceRecipe(Blocks.GRAVEL);
		HardLibAPI.oreMachines.addSluiceRecipe(Blocks.GRAVEL);
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
		
		config.addCustomCategoryComment("MILLING", "Enable (hard mode) these to remove vanilla recipes for items and instead require the millstone. In general,\neasy means the millstone doubles resources, while hard is near-vanilla.");
		boolean hardOption = config.getBoolean("RequireMillingFlour", "MILLING", false, "");

		String oreIn = "dustFlour";
		if(hardOption) {
			RecipesUtils.RemoveRecipe(Items.BREAD, 1, 0, "Hard Ores");
			RecipesUtils.RemoveRecipe(Items.COOKIE, 8, 0, "Hard Ores");
			RecipesUtils.RemoveRecipe(Items.CAKE, 1, 0, "Hard Ores");
			
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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.COOKIE, 8), "wcw", 'w', oreIn, 'c', new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage())));
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
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.SUGAR.meta), new ItemStack(Items.SUGAR, 1, 1));
		
		/*Packing*/
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.IRON.meta), new ItemStack(dummyOreIron));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.GOLD.meta), new ItemStack(dummyOreGold));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.DIAMOND.meta), new ItemStack(dummyOreDiamond));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.TIN.meta), new ItemStack(dummyOreTin));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.COPPER.meta), new ItemStack(dummyOreCopper));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.LEAD.meta), new ItemStack(dummyOreLead));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.URANIUM.meta), new ItemStack(dummyOreUranium));
		
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.REDSTONE, 9), new ItemStack(Blocks.REDSTONE_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.WHEAT, 9), new ItemStack(Blocks.HAY_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.SNOWBALL, 9), new ItemStack(Blocks.SNOW));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.SLIME_BALL, 9), new ItemStack(Blocks.SLIME_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.IRON_INGOT, 9), new ItemStack(Blocks.IRON_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.GOLD_INGOT, 9), new ItemStack(Blocks.GOLD_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.DIAMOND, 9), new ItemStack(Blocks.DIAMOND_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.EMERALD, 9), new ItemStack(Blocks.EMERALD_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.CLAY_BALL, 9), new ItemStack(Blocks.CLAY));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.BRICK, 4), new ItemStack(Blocks.BRICK_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.STRING, 4), new ItemStack(Blocks.WOOL));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.QUARTZ, 9), new ItemStack(Blocks.QUARTZ_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Blocks.SAND, 9), new ItemStack(Blocks.SANDSTONE));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Blocks.SAND, 9, BlockSand.EnumType.RED_SAND.getMetadata()), new ItemStack(Blocks.RED_SANDSTONE, 1));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.COAL, 9), new ItemStack(Blocks.COAL_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Blocks.ICE, 9), new ItemStack(Blocks.PACKED_ICE));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Blocks.SNOW, 9), new ItemStack(Blocks.ICE));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.DYE, 9, 4), new ItemStack(Blocks.LAPIS_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.MELON, 9), new ItemStack(Blocks.MELON_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.MAGMA_CREAM, 4), new ItemStack(Blocks.field_189877_df));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.NETHERBRICK, 4), new ItemStack(Blocks.NETHER_BRICK));
		//Conflicts, not "storage."  Cannot be uncrafted
		//HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.PRISMARINE_SHARD, 4), new ItemStack(Blocks.PRISMARINE));
		//HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.PRISMARINE_SHARD, 9), new ItemStack(Blocks.PRISMARINE, 1, 1));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.NETHER_WART, 9), new ItemStack(Blocks.field_189878_dg));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.DYE, 9, EnumDyeColor.WHITE.getDyeDamage()), new ItemStack(Blocks.field_189880_di));
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new OreGuiHandler());
		
		config.save();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		OresAchievements.addCoreAchievements();
		if(stoneTools) {
			OresAchievements.addStoneTools();
		}
		List<ItemStack> oreDictReq;
		int addedOres = 0;
		oreDictReq = OreDictionary.getOres("oreTin");
		if(oreDictReq.size() > 0) {
			addExtraOre("Tin", EnumOreType.TIN, oreTin, 3, true);
			addedOres++;
		}
		oreDictReq = OreDictionary.getOres("oreCopper");
		if(oreDictReq.size() > 0) {
			addExtraOre("Copper", EnumOreType.COPPER, oreCopper, 3, true);
			addedOres++;
		}
		oreDictReq = OreDictionary.getOres("oreLead");
		if(oreDictReq.size() > 0) {
			addExtraOre("Lead", EnumOreType.LEAD, oreLead, 2, true);
			addedOres++;
		}
		oreDictReq = OreDictionary.getOres("oreUranium");
		if(oreDictReq.size() > 0) {
			addExtraOre("Uranium", EnumOreType.URANIUM, oreUranium, 1, false);
			addedOres++;
		}
		for(;addedOres>0;addedOres -= 2) {
			HardLibAPI.oreMachines.addSluiceRecipe(Blocks.GRAVEL);
		}
		
		OreDictionary.registerOre("oreTin", dummyOreTin);
		OreDictionary.registerOre("oreCopper", dummyOreCopper);
		OreDictionary.registerOre("oreLead", dummyOreLead);
		OreDictionary.registerOre("oreUranium", dummyOreUranium);
	}
	
	private void addExtraOre(String oreName, EnumOreType oreType, Block oreBlock, int sluiceWeight, boolean canFindDefault) {
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
				GameRegistry.addRecipe(new ShapelessOreRecipe(dustStack.get(0),
						oreIn, oreIn, oreIn,
						oreIn, oreIn, oreIn,
						oreIn, oreIn, oreIn));
				ItemStack instk = tinyDusktStack.get(0).copy();
				if(nuggetStack.size() > 0) {
					GameRegistry.addSmelting(instk, nuggetStack.get(0), 0.1f);
					GameRegistry.addSmelting(rawOreIn, nuggetStack.get(0), 0.1f);
				}
				instk.stackSize = 2;
				HardLibAPI.oreMachines.addMillRecipe(rawOreIn, instk);
				HardLibAPI.oreMachines.addSiftRecipe(oreIn, 8, dustStack.get(0));
			}
			oreType.set = true;
		}
	}
}
