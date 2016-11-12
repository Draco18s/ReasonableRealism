package com.draco18s.flowers.util;

import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.hardlib.blockproperties.ores.EnumOreType;
import com.draco18s.ores.OresBase;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;

public class FlowerAchievements {
	public static Achievement oreFlowers;
	public static Achievement prospecting;
	
	public static void addCoreAchievements() {
		oreFlowers = new Achievement("oreFlowers", "oreFlowers", -2, -1, new ItemStack(OreFlowersBase.oreFlowers1, 1, EnumOreType.REDSTONE.meta), AchievementList.OPEN_INVENTORY).registerStat();
		prospecting = new Achievement("prospecting", "prospecting", -4, -1, new ItemStack(Items.DYE, 1, 15), oreFlowers).registerStat();
	}
}
