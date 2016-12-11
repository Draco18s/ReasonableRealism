package com.draco18s.ores.util;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.draco18s.flowers.util.FlowerAchievements;
import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.ores.OresBase;

import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class OresAchievements {
	public static Achievement craftMill;
	public static Achievement constructMill;
	public static Achievement grindOre;
	public static Achievement craftSifter;
	public static Achievement mineLimonite;
	public static Achievement acquireIronChunk;
	public static Achievement acquireNuggets;
	public static Achievement fakeIronBar;
	public static Achievement craftDiamondStud;
	public static Achievement craftSluice;
	public static Achievement mineDiorite;
	
	private static Field parentAch;
	private static Field dispRow;
	private static Field dispCol;
	
	private static void initAvhievements() {
		parentAch = ReflectionHelper.findField(Achievement.class, "field_75992_c","parentAchievement");
		dispCol = ReflectionHelper.findField(Achievement.class, "field_75993_a","displayColumn");
		dispRow = ReflectionHelper.findField(Achievement.class, "field_75991_b","displayRow");
		
		try {
			dispRow.set(AchievementList.MINE_WOOD, -1);
			dispCol.set(AchievementList.ON_A_RAIL, 0);
			dispRow.set(AchievementList.ON_A_RAIL, 6);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addCoreAchievements() {
		initAvhievements();
		craftMill = new Achievement("craftMill", "craftMill", 9, 3, new ItemStack(OresBase.axel), AchievementList.BUILD_BETTER_PICKAXE).registerStat();
		constructMill = new Achievement("constructMill", "constructMill", 7, 5, OresBase.windvane, craftMill).registerStat();
		grindOre = new Achievement("grindOre", "grindOre", 9, 6, new ItemStack(OresBase.smallDust, 1, EnumOreType.GOLD.meta), constructMill).registerStat();
		craftSifter = new Achievement("craftSifter", "craftSifter", 9, 8, new ItemStack(OresBase.sifter), grindOre).registerStat();
		
		mineLimonite = new Achievement("mineLimonite", "mineLimonite", 3, 0, new ItemStack(OresBase.rawOre, 1, EnumOreType.LIMONITE.meta), AchievementList.BUILD_WORK_BENCH).registerStat();
		acquireIronChunk = new Achievement("acquireIronChunk", "acquireIronChunk", 2, 1, new ItemStack(OresBase.rawOre, 1, EnumOreType.IRON.meta), mineLimonite).registerStat();
		acquireNuggets = new Achievement("acquireNuggets", "acquireNuggets", 1, 2, new ItemStack(OresBase.nuggets,1,EnumOreType.IRON.meta), acquireIronChunk).registerStat();
		//hope this works: it doesn't :(
		//ArrayList list = (ArrayList)AchievementList.ACHIEVEMENTS;
		//list.remove(AchievementList.ACQUIRE_IRON);
		fakeIronBar = new Achievement("fakeIronBar", "fakeIronBar", 1, 4, Items.IRON_INGOT, AchievementList.BUILD_FURNACE).registerStat();
		
		//patch to effectively swap AQUIRE_IRON and FAKE_IRON
		//so that the gui shows the desired prereq
		try {
			parentAch.set(AchievementList.ACQUIRE_IRON, acquireNuggets);
		}
		catch (Exception e) {
			
		}
		
		//list.add(AchievementList.ACQUIRE_IRON);
		craftDiamondStud = new Achievement("craftDiamondStud", "craftDiamondStud", -2, 3, new ItemStack(OresBase.diaStudAxe), AchievementList.DIAMONDS).registerStat();

		craftSluice = new Achievement("craftSluice", "craftSluice", -4, -3, OresBase.sluice, FlowerAchievements.prospecting).registerStat();		
	}

	public static void addStoneTools() {
		ItemStack diorite = new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.DIORITE.getMetadata());
		mineDiorite = new Achievement("mineDiorite", "mineDiorite", 6, 2, diorite, AchievementList.BUILD_PICKAXE).registerStat();

		try {
			parentAch.set(AchievementList.BUILD_BETTER_PICKAXE, mineDiorite);
			dispCol.set(AchievementList.BUILD_BETTER_PICKAXE, 8);
		}
		catch (Exception e) {

		}
	}
}
