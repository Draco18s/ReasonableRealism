package com.draco18s.flowers.util;

import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class FlowerAchievements {
	public static Advancement oreFlowers;
	public static Advancement prospecting;
	
	/*public static void addCoreAchievements() {
		oreFlowers = new Advancement("oreFlowers", "oreFlowers", -2, -1, new ItemStack(OreFlowersBase.oreFlowers1, 1, EnumOreType.REDSTONE.meta), AdvancementList.OPEN_INVENTORY).registerStat();
		prospecting = new Advancement("prospecting", "prospecting", -4, -1, new ItemStack(Items.DYE, 1, 15), oreFlowers).registerStat();
	}*/
}
