package com.draco18s.flowers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import com.draco18s.flowers.block.BlockOreFlower1;
import com.draco18s.flowers.block.BlockOreFlower2;
import com.draco18s.flowers.block.BlockOreFlower3;
import com.draco18s.flowers.block.BlockOreFlowerDesert1;
import com.draco18s.flowers.block.BlockOreFlowerDesert2;
import com.draco18s.flowers.block.BlockOreFlowerDesert3;
import com.draco18s.flowers.item.ItemOreFlower1;
import com.draco18s.flowers.item.ItemOreManipulator;
import com.draco18s.flowers.item.ItemStickyBlob;
import com.draco18s.flowers.util.ChunkOreCounter;
import com.draco18s.flowers.util.FlowerAchievements;
import com.draco18s.flowers.util.OreDataHooks;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlower1;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlower2;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlower3;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlowerDesert1;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlowerDesert2;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlowerDesert3;
import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid="oreflowers", name="OreFlowers", version="{@version:flowers}", dependencies = "required-after:hardlib;required-after:customoregen")
public class OreFlowersBase {
	@Instance("oreflowers")
	public static OreFlowersBase instance;
	
	public static Block oreFlowers1;
	public static Block oreFlowersDesert1;
	public static Block oreFlowers2;
	public static Block oreFlowersDesert2;
	public static Block oreFlowers3;
	public static Block oreFlowersDesert3;
	
	public static Item gooBlob;
	public static Item ironWand;
	public static Item goldWand;
	public static Item diamondWand;
	public static Item redstoneWand;
	public static Item allDataWand;
	
	//@SidedProxy(clientSide="com.draco18s.flowers.client.ClientProxy", serverSide="com.draco18s.flowers.CommonProxy")
	//public static CommonProxy proxy;
	
	public static Logger logger;

	public static ChunkOreCounter oreCounter;
	protected static OreDataHooks dataHooks; 

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		oreCounter = new ChunkOreCounter();
		HardLibAPI.oreFlowers = new FlowerDataHandler();
		HardLibAPI.oreData = dataHooks = new OreDataHooks();
		HardLibAPI.hardOres = new OreBlockInfo();

		oreFlowers1 = new BlockOreFlower1();
		EasyRegistry.registerBlockWithCustomItemAndMapper((BlockOreFlower1)oreFlowers1, new ItemOreFlower1(oreFlowers1, 8, EnumOreFlower1.class), "oreflowers1");
		oreFlowersDesert1 = new BlockOreFlowerDesert1();
		EasyRegistry.registerBlockWithCustomItemAndMapper((BlockOreFlowerDesert1)oreFlowersDesert1, new ItemOreFlower1(oreFlowersDesert1, 8, EnumOreFlowerDesert1.class), "oreflowersdesert1");
		oreFlowers2 = new BlockOreFlower2();
		EasyRegistry.registerBlockWithCustomItemAndMapper((BlockOreFlower2)oreFlowers2, new ItemOreFlower1(oreFlowers2, 16, EnumOreFlower2.class), "oreflowers2");
		oreFlowersDesert2 = new BlockOreFlowerDesert2();
		EasyRegistry.registerBlockWithCustomItemAndMapper((BlockOreFlowerDesert2)oreFlowersDesert2, new ItemOreFlower1(oreFlowersDesert2, 16, EnumOreFlowerDesert2.class), "oreflowersdesert2");
		oreFlowers3 = new BlockOreFlower3();
		EasyRegistry.registerBlockWithCustomItemAndMapper((BlockOreFlower3)oreFlowers3, new ItemOreFlower1(oreFlowers3, 24, EnumOreFlower3.class), "oreflowers3");
		oreFlowersDesert3 = new BlockOreFlowerDesert3();
		EasyRegistry.registerBlockWithCustomItemAndMapper((BlockOreFlowerDesert3)oreFlowersDesert3, new ItemOreFlower1(oreFlowersDesert3, 24, EnumOreFlowerDesert3.class), "oreflowersdesert3");
		
		gooBlob = new ItemStickyBlob();
		EasyRegistry.registerItem(gooBlob, "sticky_goo");
		
		IBlockState flower1State = oreFlowers1.getDefaultState();
		IBlockState flowerDesert1State = oreFlowersDesert1.getDefaultState();
		OreFlowerData data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._1POORJOE),
				8, 10, 0);
		BlockWrapper wrap = new BlockWrapper(Blocks.IRON_ORE, 16);
		ironWand = new ItemOreManipulator(wrap);
		EasyRegistry.registerItem(ironWand, "ironwand");
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flowerDesert1State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._1RED_SORREL),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._2HORSETAIL),
				8, 10, 0);
		wrap = new BlockWrapper(Blocks.GOLD_ORE, 16);
		goldWand = new ItemOreManipulator(wrap);
		EasyRegistry.registerItem(goldWand, "goldwand");
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._3VALLOZIA),
				8, 10, 0);
		wrap = new BlockWrapper(Blocks.DIAMOND_ORE, 16);
		diamondWand = new ItemOreManipulator(wrap);
		EasyRegistry.registerItem(diamondWand, "diamondwand");
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flowerDesert1State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._3CHANDELIER_TREE).withProperty(Props.FLOWER_STALK, true),
				8, 15, 0, 1);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._4FLAME_LILY).withProperty(Props.FLOWER_STALK, true),
				2, 12, 0, 2);
		wrap = new BlockWrapper(Blocks.REDSTONE_ORE, 16);
		redstoneWand = new ItemOreManipulator(wrap);
		EasyRegistry.registerItem(redstoneWand, "redstonewand");
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.closeDictator, data);
		data = new OreFlowerData(flowerDesert1State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._4AVELOZ).withProperty(Props.FLOWER_STALK, true),
				2, 12, 0, 1);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.closeDictator, data);

		allDataWand = new ItemOreManipulator(null);
		EasyRegistry.registerItem(allDataWand, "datawand");
		
		FlowerEventHandler handler = new FlowerEventHandler();
		MinecraftForge.ORE_GEN_BUS.register(handler);
		MinecraftForge.EVENT_BUS.register(handler);
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		GameRegistry.addShapedRecipe(new ItemStack(gooBlob), "xx","xx",'x',new ItemStack(oreFlowersDesert1,1,EnumOreFlowerDesert1._4AVELOZ.getOrdinal()));
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		IBlockState flower1State = oreFlowers1.getDefaultState();
		IBlockState flowerDesert1State = oreFlowersDesert1.getDefaultState();
		IBlockState flower2State = oreFlowers2.getDefaultState();
		IBlockState flowerDesert2State = oreFlowersDesert2.getDefaultState();
		IBlockState flower3State = oreFlowers3.getDefaultState();
		IBlockState flowerDesert3State = oreFlowersDesert3.getDefaultState();
		FlowerAchievements.addCoreAchievements();
		addArbitraryOre("oreIron",		 8, 11, 7, OreFlowerDictator.defaultDictator,	flower1State, flowerDesert1State, Props.FLOWER_TYPE,  EnumOreFlower1._1POORJOE,			Props.DESERT_FLOWER_TYPE,  EnumOreFlowerDesert1._1RED_SORREL);
		addArbitraryOre("oreGold",		 9, 11, 6, OreFlowerDictator.defaultDictator,	flower1State, flowerDesert1State, Props.FLOWER_TYPE,  EnumOreFlower1._2HORSETAIL,		Props.DESERT_FLOWER_TYPE,  null/*EnumOreFlowerDesert1._2GOLD*/);
		addArbitraryOre("oreDiamond",	 8, 11, 5, OreFlowerDictator.commonDictator,	flower1State, flowerDesert1State, Props.FLOWER_TYPE,  EnumOreFlower1._3VALLOZIA,		Props.DESERT_FLOWER_TYPE,  EnumOreFlowerDesert1._3CHANDELIER_TREE);
		addArbitraryOre("oreRedstone",	 8, 15, 5, OreFlowerDictator.closeRareDictator,	flower1State, flowerDesert1State, Props.FLOWER_TYPE,  EnumOreFlower1._4FLAME_LILY,		Props.DESERT_FLOWER_TYPE,  EnumOreFlowerDesert1._4AVELOZ);
		addArbitraryOre("oreTin",		 8,  5, 6, OreFlowerDictator.rareDictator,		flower1State, flowerDesert1State, Props.FLOWER_TYPE,  EnumOreFlower1._5TANSY, 			Props.DESERT_FLOWER_TYPE,  null/*EnumOreFlowerDesert1._5TIN*/);
		addArbitraryOre("oreCopper",	 4, 10, 6, OreFlowerDictator.defaultDictator,	flower1State, flowerDesert1State, Props.FLOWER_TYPE,  EnumOreFlower1._6HAUMAN, 			Props.DESERT_FLOWER_TYPE,  EnumOreFlowerDesert1._6COPPER_FLOWER);
		addArbitraryOre("oreLead", 		 4,  7, 5, OreFlowerDictator.defaultDictator,	flower1State, flowerDesert1State, Props.FLOWER_TYPE,  EnumOreFlower1._7LEADPLANT, 		Props.DESERT_FLOWER_TYPE,  EnumOreFlowerDesert1._7SHEEPS_FESCUE);
		addArbitraryOre("oreUranium", 	 3,  7, 4, OreFlowerDictator.defaultDictator,	flower1State, flowerDesert1State, Props.FLOWER_TYPE,  EnumOreFlower1._8RED_AMARANTH,	Props.DESERT_FLOWER_TYPE,  EnumOreFlowerDesert1._8PRIMROSE);

		addArbitraryOre("oreSilver",	11,  5, 5, OreFlowerDictator.defaultDictator,	flower2State, flowerDesert2State, Props.FLOWER_TYPE2, EnumOreFlower2._1MUSTARD, 		Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._1RAPESEED);
		addArbitraryOre("oreNickel",	 6,  8, 5, OreFlowerDictator.defaultDictator,	flower2State, flowerDesert2State, Props.FLOWER_TYPE2, EnumOreFlower2._2SHRUB_VIOLET,	Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._2MILKWORT);
		addArbitraryOre("oreAluminum",	 5, 10, 5, OreFlowerDictator.defaultDictator,	flower2State, flowerDesert2State, Props.FLOWER_TYPE2, EnumOreFlower2._3AFFINE, 			Props.DESERT_FLOWER_TYPE2, null/*EnumOreFlowerDesert2._3ALUMINUM*/);
		addArbitraryOre("orePlatinum",	 8, 12, 5, OreFlowerDictator.rareDictator,		flower2State, flowerDesert2State, Props.FLOWER_TYPE2, null/*EnumOreFlower2._4PLATINUM*/,Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._4MADWORT);
		addArbitraryOre("oreZinc",		 8, 10, 6, OreFlowerDictator.rareDictator,		flower2State, flowerDesert2State, Props.FLOWER_TYPE2, EnumOreFlower2._5CLOVER, 			Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._5ZILLA);
		addArbitraryOre("oreFluorite",	 7, 10, 4, OreFlowerDictator.defaultDictator,	flower2State, flowerDesert2State, Props.FLOWER_TYPE2, EnumOreFlower2._6CAMELLIA, 		Props.DESERT_FLOWER_TYPE2, null/*EnumOreFlowerDesert2._6FLUORITE*/);
		addArbitraryOre("oreCadmium",	 6, 10, 6, OreFlowerDictator.defaultDictator,	flower2State, flowerDesert2State, Props.FLOWER_TYPE2, EnumOreFlower2._7MALVA, 			Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._7MARIGOLD);
		addArbitraryOre("oreThorium",	 5,  8, 4, OreFlowerDictator.defaultDictator,	flower2State, flowerDesert2State, Props.FLOWER_TYPE2, EnumOreFlower2._8MELASTOMA, 		Props.DESERT_FLOWER_TYPE2, null/*EnumOreFlowerDesert2._8THORIUM*/);
		
		addArbitraryOre("oreOsmium",	 5,  9, 5, OreFlowerDictator.defaultDictator,	flower3State, flowerDesert3State, Props.FLOWER_TYPE3, EnumOreFlower3._1ARROWHEAD, 		Props.DESERT_FLOWER_TYPE3, EnumOreFlowerDesert3._1PAINTBRUSH);
	}
	
	private <T extends Comparable<T>, V extends T,U extends Comparable<U>, W extends U>
	void addArbitraryOre(String orename, int numFlowers, int clusterSize, int threshold, OreFlowerDictator dictator, IBlockState flower, IBlockState desertFlower, @Nullable IProperty<T> flowerProp, @Nullable V flowerValue, @Nullable IProperty<U> desertProp, @Nullable W desertValue) {
		List<ItemStack> oreDictReq = OreDictionary.getOres(orename);
		OreFlowerData data;
		BlockWrapper wrap;
		if(oreDictReq.size() > 0) {
			for(ItemStack stack : oreDictReq) {
				if(stack.getItem() instanceof ItemBlock) {
					Block block = Block.getBlockFromItem(stack.getItem());
					if(block.getRegistryName().toString().contains("dummy")) continue;
					if(block instanceof IBlockMultiBreak) {
						wrap = new BlockWrapper(block, Props.ORE_DENSITY);
					}
					else {
						wrap = new BlockWrapper(block, 9);
					}
					
					if(HardLibAPI.oreFlowers.getDataForOre(wrap) == null) {
						if(flowerProp != null && flowerValue != null) {
							data = new OreFlowerData(flower.withProperty(flowerProp, flowerValue),
									numFlowers, clusterSize, threshold);
							HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
						}
						if(desertProp != null && desertValue != null) {
							data = new OreFlowerData(desertFlower.withProperty(desertProp, desertValue),
									numFlowers, clusterSize, threshold);
							HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
						}
					}
				}
			}
		}
	}
}
