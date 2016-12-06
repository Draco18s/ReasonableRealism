package com.draco18s.ores.flowers;

import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlower1;
import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlower2;
import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlowerDesert1;
import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlowerDesert2;
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
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._1RED_SORREL),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		
		wrap = new BlockWrapper(OresBase.oreGold, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._2HORSETAIL),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		/*data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._2GOLD),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);*/
		
		wrap = new BlockWrapper(OresBase.oreDiamond, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._3VALLOZIA),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._3CHANDELIER_TREE).withProperty(Props.FLOWER_STALK, true),
				8, 15, 0, 1);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(flower2State.getBlock(), 1, EnumOreFlowerDesert1._4AVELOZ.ordinal()), new ItemStack(OreFlowersBase.gooBlob));
		
		
		wrap = new BlockWrapper(OresBase.oreTin, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._5TANSY),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		/*data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._5TIN),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, dictator, data);*/
		wrap = new BlockWrapper(OresBase.oreCopper, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._6HAUMAN),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._6COPPER_FLOWER),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(OresBase.oreLead, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._7LEADPLANT),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._7SHEEPS_FESCUE),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(OresBase.oreUranium, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE, EnumOreFlower1._8RED_AMARANTH),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE, EnumOreFlowerDesert1._8PRIMROSE),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		
		/* Ore Flower 2 */

		flower1State = HardLibAPI.oreFlowers.getDefaultFlower(Props.FLOWER_TYPE2);
		flower2State = HardLibAPI.oreFlowers.getDefaultFlower(Props.DESERT_FLOWER_TYPE2);
		wrap = new BlockWrapper(OresBase.oreSilver, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE2, EnumOreFlower2._1MUSTARD),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._1RAPESEED),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(OresBase.oreNickel, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE2, EnumOreFlower2._2SHRUB_VIOLET),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._2MILKWORT),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		wrap = new BlockWrapper(OresBase.oreAluminum, Props.ORE_DENSITY);
		data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE2, EnumOreFlower2._3AFFINE),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
		/*data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._3ALUMINUM),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);*/
		wrap = new BlockWrapper(OresBase.orePlatinum, Props.ORE_DENSITY);
		/*data = new OreFlowerData(flower1State.withProperty(Props.FLOWER_TYPE2, EnumOreFlower2._4PLATINUM),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);*/
		data = new OreFlowerData(flower2State.withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._4MADWORT),
				8, 10, 0);
		HardLibAPI.oreFlowers.addOreFlowerData(wrap, OreFlowerDictator.defaultDictator, data);
	}
}
