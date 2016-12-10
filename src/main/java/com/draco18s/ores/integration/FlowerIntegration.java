package com.draco18s.ores.integration;

import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlower1;
import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlower2;
import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlower3;
import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlowerDesert1;
import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlowerDesert2;
import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlowerDesert3;
import com.draco18s.hardlib.internal.BlockWrapper;
import com.draco18s.hardlib.internal.OreFlowerData;
import com.draco18s.hardlib.internal.OreFlowerDictator;
import com.draco18s.ores.OresBase;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class FlowerIntegration {
	public static void registerFlowerGen() {
		IBlockState flower1State = HardLibAPI.oreFlowers.getDefaultFlower(Props.FLOWER_TYPE);
		IBlockState flower2State = HardLibAPI.oreFlowers.getDefaultFlower(Props.DESERT_FLOWER_TYPE);
		
		/* Ore Flower 1 */
		
		BlockWrapper wrap = new BlockWrapper(OresBase.oreIron, Props.ORE_DENSITY);
		OreFlowerData data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._1POORJOE),
				8, 11, 7);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._1RED_SORREL),
				8, 11, 7);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		
		wrap = new BlockWrapper(OresBase.oreGold, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._2HORSETAIL),
				9, 11, 6);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		/*data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._2GOLD),
				9, 11, 6);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);*/
		
		wrap = new BlockWrapper(OresBase.oreDiamond, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._3VALLOZIA),
				8, 15, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.commonDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._3CHANDELIER_TREE).withProperty(Props.FLOWER_STALK, true),
				8, 15, 5, 1);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.commonDictator, data);
		
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(flower2State.getBlock(), 1, EnumOreFlowerDesert1._4AVELOZ.ordinal()), new ItemStack(OreFlowersBase.gooBlob));
		
		wrap = new BlockWrapper(OresBase.oreTin, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._5TANSY),
				8,  5, 6);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		/*data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._5TIN),
				8,  5, 6);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);*/
		wrap = new BlockWrapper(OresBase.oreCopper, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._6HAUMAN),
				4, 10, 6);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._6COPPER_FLOWER),
				4, 10, 6);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(OresBase.oreLead, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._7LEADPLANT),
				4,  7, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._7SHEEPS_FESCUE),
				4,  7, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(OresBase.oreUranium, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._8RED_AMARANTH),
				3,  7, 4);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._8PRIMROSE),
				3,  7, 4);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		
		/* Ore Flower 2 */

		flower1State = HardLibAPI.oreFlowers.getDefaultFlower(Props.FLOWER_TYPE2);
		flower2State = HardLibAPI.oreFlowers.getDefaultFlower(Props.DESERT_FLOWER_TYPE2);
		wrap = new BlockWrapper(OresBase.oreSilver, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE2, EnumOreFlower2._1MUSTARD),
				11, 5, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._1RAPESEED),
				11, 5, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(OresBase.oreNickel, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE2, EnumOreFlower2._2SHRUB_VIOLET),
				6,  8, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._2MILKWORT),
				6,  8, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(OresBase.oreAluminum, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE2, EnumOreFlower2._3AFFINE),
				5, 10, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		/*data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._3ALUMINUM),
				5, 10, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);*/
		wrap = new BlockWrapper(OresBase.orePlatinum, Props.ORE_DENSITY);
		/*data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE2, EnumOreFlower2._4PLATINUM),
				8, 12, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);*/
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._4MADWORT),
				8, 12, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(OresBase.oreZinc, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE2, EnumOreFlower2._5CLOVER),
				8, 10, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._5ZILLA),
				8, 10, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		
		/* Ore Flower 3 */
		
		flower1State = HardLibAPI.oreFlowers.getDefaultFlower(Props.FLOWER_TYPE3);
		flower2State = HardLibAPI.oreFlowers.getDefaultFlower(Props.DESERT_FLOWER_TYPE3);
		wrap = new BlockWrapper(OresBase.oreOsmium, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE3, EnumOreFlower3._1ARROWHEAD),
				5,  9, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE3, EnumOreFlowerDesert3._1PAINTBRUSH),
				5,  9, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(OresBase.oreQuartz, Props.ORE_DENSITY);
		//quartz will not have any flowers
		/*data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE3, EnumOreFlower3._2QUARTZ),
				5,  9, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE3, EnumOreFlowerDesert3._2QUARTZ),
				5,  9, 5);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);*/
	}
}
