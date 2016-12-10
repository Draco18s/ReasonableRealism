package com.draco18s.industry.recipes;

import java.util.List;

import com.draco18s.hardlib.util.RecipesUtils;
import com.draco18s.industry.ExpandedIndustryBase;
import com.google.common.collect.Lists;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeToolMold implements IRecipe {
	protected Item output;
	protected ItemStack input;
	protected ItemStack mold;

	public RecipeToolMold(Item output, Item tool, Item mold) {
		this.output = output;
		this.input = new ItemStack(tool, 1, 0);
		this.mold = new ItemStack(mold, 1, 0);
	}

	/**
	 * Avoid using this constructor. Only use it if the desired recipe has an output size > 1.
	 */
	public RecipeToolMold(Item output, ItemStack itemStack, Item mold) {
		this.output = output;
		this.input = itemStack;
		this.mold = new ItemStack(mold, 1, 0);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		List<ItemStack> list = Lists.newArrayList();
		list.add(input);
		list.add(mold);
		for (int i = 0; i < inv.getHeight(); ++i) {
			for (int j = 0; j < inv.getWidth(); ++j) {
				ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

				if (itemstack != null) {
					boolean flag = false;

					for (ItemStack itemstack1 : list) {
						if (!itemstack.hasTagCompound() && itemstack.getItem() == itemstack1.getItem() && (itemstack1.getMetadata() == OreDictionary.WILDCARD_VALUE || itemstack.getMetadata() == itemstack1.getMetadata())) {
							flag = true;
							list.remove(itemstack1);
							break;
						}
						else if(itemstack.getItem() != ExpandedIndustryBase.itemMold) {
							IRecipe tr = RecipesUtils.getSimilarRecipeWithGivenInput(RecipesUtils.getRecipeWithOutput(itemstack),new ItemStack(Items.IRON_INGOT));
							if(tr != null) {
								ItemStack test = tr.getRecipeOutput();
								if (!test.hasTagCompound() && test.getItem() == itemstack1.getItem() && (itemstack1.getMetadata() == OreDictionary.WILDCARD_VALUE || test.getMetadata() == itemstack1.getMetadata())) {
									flag = true;
									list.remove(itemstack1);
									break;
								}
							}
						}
					}

					if (!flag) {
						return false;
					}
				}
			}
		}

		return list.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack out = getRecipeOutput();
		for (int i = 0; i < inv.getHeight(); ++i) {
			for (int j = 0; j < inv.getWidth(); ++j) {
				ItemStack itemstack = inv.getStackInRowAndColumn(j, i);
				itemstack = getActualTool(itemstack, input);
				if (itemstack != null && (itemstack.getItem() == input.getItem() && (input.getMetadata() == OreDictionary.WILDCARD_VALUE || itemstack.getMetadata() == input.getMetadata()))) {
					NBTTagCompound nbt = new NBTTagCompound();
					NBTTagCompound itemTag = new NBTTagCompound();
					input.writeToNBT(itemTag);
					nbt.setTag("expindustry:item_mold", itemTag);
					out.setTagCompound(nbt);
				}
			}
		}
		return out;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(output, 1, 0);
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];
		for (int i = 0; i < aitemstack.length; ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
			if(aitemstack[i] == null && getActualTool(itemstack, input) != null) {
				aitemstack[i] = itemstack.copy();
			}
		}
		return aitemstack;
	}
	
	private static ItemStack getActualTool(ItemStack inGrid, ItemStack goal) {
		IRecipe tr = RecipesUtils.getSimilarRecipeWithGivenInput(RecipesUtils.getRecipeWithOutput(inGrid),new ItemStack(Items.IRON_INGOT));
		if(tr != null) {
			ItemStack test = tr.getRecipeOutput();
			if (!test.hasTagCompound() && test.getItem() == goal.getItem() && (goal.getMetadata() == OreDictionary.WILDCARD_VALUE || test.getMetadata() == goal.getMetadata())) {
				return test;
			}
		}
		return null;
	}
}
