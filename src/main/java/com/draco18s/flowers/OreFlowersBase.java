package com.draco18s.flowers;

import org.apache.logging.log4j.Logger;

import com.draco18s.flowers.block.BlockOreFlower1;
import com.draco18s.flowers.block.BlockOreFlowerDesert;
import com.draco18s.flowers.item.ItemOreFlower1;
import com.draco18s.flowers.states.StateMapperFlowers;
import com.draco18s.flowers.util.OreDataHooks;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.EnumOreFlower1;
import com.draco18s.hardlib.blockproperties.EnumOreFlower2;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.internal.BlockWrapper;
import com.draco18s.hardlib.internal.OreFlowerData;
import com.draco18s.hardlib.internal.OreFlowerDictator;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="oreflowers", name="OreFlowers", version="{@version:flowers}"/*, dependencies = "required-after:HardLib"*/)
public class OreFlowersBase {
	@Instance("OreFlowers")
	public static OreFlowersBase instance;
	
	public static Block oreFlowers1;
	public static Block oreFlowers2;
	
	//@SidedProxy(clientSide="com.draco18s.ores.client.ClientProxy", serverSide="com.draco18s.ores.CommonProxy")
	//public static CommonProxy proxy;
	
	public static Logger logger;

	public static OreDataHooks oreCounter;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		oreCounter = new OreDataHooks();
		HardLibAPI.oreFlowers = new FlowerDataHandler();

		oreFlowers1 = new BlockOreFlower1();
		EasyRegistry.registerBlockWithCustomItemAndMapper(oreFlowers1, new ItemOreFlower1(oreFlowers1,8, EnumOreFlower1.class), new StateMapperFlowers(Props.FLOWER_TYPE), "oreflowers1");
		oreFlowers2 = new BlockOreFlowerDesert();
		EasyRegistry.registerBlockWithCustomItemAndMapper(oreFlowers2, new ItemOreFlower1(oreFlowers2,8, EnumOreFlower2.class), new StateMapperFlowers(Props.DESERT_FLOWER_TYPE), "oreflowers2");
		
		OreFlowerDictator dictator = new OreFlowerDictator(5, 0);
		IBlockState flower1State = oreFlowers1.getDefaultState();
		IBlockState flower2State = oreFlowers2.getDefaultState();
		OreFlowerData data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1.POORJOE), 8, 3, 0);
		BlockWrapper wrap = new BlockWrapper(Blocks.IRON_ORE, 16);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlower2.RED_SORREL), 8, 3, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		
		dictator = new OreFlowerDictator(5, 0);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1.HORSETAIL), 8, 3, 0);
		wrap = new BlockWrapper(Blocks.GOLD_ORE, 16);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		
		dictator = new OreFlowerDictator(5, 0);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1.VALLOZIA), 8, 3, 0);
		wrap = new BlockWrapper(Blocks.DIAMOND_ORE, 16);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlower2.CHANDELIER_TREE).withProperty(Props.FLOWER_STALK, true), 8, 3, 0, 1);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		
		dictator = new OreFlowerDictator(5, 0);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1.FLAME_LILY).withProperty(Props.FLOWER_STALK, true), 8, 3, 0, 3);
		wrap = new BlockWrapper(Blocks.REDSTONE_ORE, 16);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		
		MinecraftForge.ORE_GEN_BUS.register(new FlowerEventHandler());
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
