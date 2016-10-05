package com.draco18s.ores.flowers;

import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.EnumOreFlower1;
import com.draco18s.hardlib.blockproperties.EnumOreFlowerDesert1;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.internal.BlockWrapper;
import com.draco18s.hardlib.internal.OreFlowerData;
import com.draco18s.hardlib.internal.OreFlowerDictator;
import com.draco18s.ores.OresBase;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

public class FlowerIntegration {
	public static void registerFlowerGen() {
		OreFlowerDictator dictator = new OreFlowerDictator(5, 0);
		IBlockState flower1State = HardLibAPI.oreFlowers.getDefaultFlower(Props.FLOWER_TYPE);
		IBlockState flower2State = HardLibAPI.oreFlowers.getDefaultFlower(Props.DESERT_FLOWER_TYPE);
		
		BlockWrapper wrap = new BlockWrapper(OresBase.oreIron, Props.ORE_DENSITY);
		OreFlowerData data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._1POORJOE),
				8, 3, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._1RED_SORREL),
				8, 3, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		
		wrap = new BlockWrapper(OresBase.oreGold, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._2HORSETAIL),
				8, 3, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		/*data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._2GOLD),
				8, 3, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);*/
		
		wrap = new BlockWrapper(OresBase.oreDiamond, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._3VALLOZIA),
				8, 3, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._3CHANDELIER_TREE),
				8, 3, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);
		
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(flower2State.getBlock(), 1, EnumOreFlowerDesert1._4AVELOZ.ordinal()), new ItemStack(OreFlowersBase.gooBlob));
	}
}
