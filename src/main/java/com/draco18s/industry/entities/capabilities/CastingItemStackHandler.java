package com.draco18s.industry.entities.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

public class CastingItemStackHandler extends ItemStackHandler {
	private static List<ItemStack> allSticks;
	private static List<ItemStack> allIngots;
	
	public CastingItemStackHandler(int size) {
		super(size);
	}
	
	public static void initLists() {	
		allSticks = OreDictionary.getOres("stickWood");
		String[] allNames = OreDictionary.getOreNames();
		allIngots = new ArrayList<ItemStack>();
		for(String name : allNames) {
			if(name.contains("ingot")) {
				allIngots.addAll(OreDictionary.getOres(name));
			}
		}
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(slot == 0 && isStick(stack)) {
			return super.insertItem(slot, stack, simulate);
		}
		if(slot == 1 && isIngot(stack)) {
			return super.insertItem(slot, stack, simulate);
		}
		return stack;
	}

	public static boolean isStick(ItemStack stack) {
		for (ItemStack entry : allSticks) {
			if (compareItemStacks(stack, entry)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isIngot(ItemStack stack) {
		for (ItemStack entry : allIngots) {
			if (compareItemStacks(stack, entry)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
		return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
    }
}
