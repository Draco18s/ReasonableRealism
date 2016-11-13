package com.draco18s.farming.util;

import java.lang.reflect.Field;

import com.draco18s.farming.FarmingBase;
import com.draco18s.farming.FarmingEventHandler;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class FarmingAchievements {
	public static Achievement collectWinterWheat;
	public static Achievement craftThermometer;
	public static Achievement collectRawhide;
	public static Achievement craftTanner;
	public static Achievement getLeather;
	public static Achievement saltedHide;
	public static Achievement killWeeds;
	public static Achievement cropRotation;
	public static Achievement weedSuppressor;
	
	private static Field parentAch;
	private static Field dispRow;
	private static Field dispCol;
	
	private static void initAvhievements() {
		parentAch = ReflectionHelper.findField(Achievement.class, "field_75992_c","parentAchievement");
		dispCol = ReflectionHelper.findField(Achievement.class, "field_75993_a","displayColumn");
		dispRow = ReflectionHelper.findField(Achievement.class, "field_75991_b","displayRow");
		
		try {
			dispCol.set(AchievementList.FLY_PIG, 11);
			dispCol.set(AchievementList.BREED_COW, 11);
			dispCol.set(AchievementList.KILL_COW, 9);
			dispRow.set(AchievementList.BUILD_HOE, -5);
			dispRow.set(AchievementList.BAKE_CAKE, -7);
			dispRow.set(AchievementList.MAKE_BREAD, -5);
			dispRow.set(AchievementList.KILL_COW, -4);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addCoreAchievements() {
		initAvhievements();
		
		if(FarmingEventHandler.doRawLeather) {
			collectRawhide = new Achievement("collectRawhide", "collectRawhide", 5, -3, FarmingBase.rawLeather, AchievementList.BUILD_SWORD).registerStat();
			craftTanner = new Achievement("craftTanner", "craftTanner", 7, -4, FarmingBase.tanningRack, collectRawhide).registerStat();
			getLeather = new Achievement("getLeather", "getLeather", 9, -4, Items.LEATHER, craftTanner).registerStat();
			saltedHide = new Achievement("saltedHide", "saltedHide", 6, -6, FarmingBase.rawSalt, craftTanner).registerStat();
			
			try {
				parentAch.set(AchievementList.FLY_PIG, getLeather);
				parentAch.set(AchievementList.BREED_COW, getLeather);
				parentAch.set(AchievementList.KILL_COW, craftTanner);
			}
			catch (Exception e) {
				
			}
		}
		killWeeds = new Achievement("killWeeds", "killWeeds", 0, -3, new ItemStack(FarmingBase.itemAchievementIcons, 1, EnumFarmAchieves.KILL_WEEDS.meta), AchievementList.BUILD_HOE).registerStat();
		collectWinterWheat = new Achievement("collectWinterWheat", "collectWinterWheat", 3, -4, FarmingBase.winterWheatSeeds, AchievementList.BUILD_HOE).registerStat();
		craftThermometer = new Achievement("craftThermometer", "craftThermometer", 4, -7, new ItemStack(FarmingBase.itemAchievementIcons, 1, EnumFarmAchieves.THERMOMETER.meta), AchievementList.BUILD_HOE).registerStat();
		cropRotation = new Achievement("cropRotation", "cropRotation", -1, -4, new ItemStack(FarmingBase.itemAchievementIcons, 1, EnumFarmAchieves.CROP_ROTATION.meta), killWeeds).registerStat();
		weedSuppressor = new Achievement("weedSuppressor", "weedSuppressor", -2, -3, new ItemStack(Blocks.CARPET, 1, 12), killWeeds).registerStat();
	}
}
