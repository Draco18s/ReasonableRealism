package com.draco18s.hardlib.api.recipes;

import java.util.ArrayList;
import java.util.List;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.interfaces.IItemWithMeshDefinition;
import com.draco18s.hardlib.util.RecipesUtils;
import com.draco18s.industry.ExpandedIndustryBase;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
	public static List<RecipeSubItem> allMoldItems = new ArrayList<RecipeSubItem>();
	protected Item output;
	protected ItemStack input;
	protected ItemStack mold;
	protected String resourceDomain;

	/**
	 * See {@link #RecipeToolMold(Item, ItemStack, Item, String)
	 */
	public RecipeToolMold(Item output, Item tool, Item mold) {
		this(output, new ItemStack(tool, 1, 0), mold);
	}

	/**
	 * See {@link #RecipeToolMold(Item, ItemStack, Item, String)
	 */
	public RecipeToolMold(Item output, ItemStack itemStack, Item mold) {
		this(output, itemStack, mold, null);
	}
	
	/**
	 * See {@link #RecipeToolMold(Item, ItemStack, Item, String)
	 */
	public RecipeToolMold(Item output, Item tool, Item mold, String resourceDomain) {
		this(output, new ItemStack(tool, 1, 0), mold, resourceDomain);
	}

	/**
	 * Creates a recipe that adds the mold NBT data.  There is a convenient referene to
	 * {@link com.draco18s.industry.item.ItemCastingMold ItemCastingMold} int the
	 * {@link com.draco18s.hardlib.api.HardLibAPI.itemMold HardLibAPI}<br>
	 * If output implements {@link com.draco18s.hardlib.api.interfaces.IItemWithMeshDefinition IItemWithMeshDefinition}
	 * then this recipe <b>must</b> be created during preInit, or the item model will fail to load properly.
	 * @param output - Should be an ItemCastingMold, but may be any item.  NBT data will be applied to it that
	 * will allow it to function in the Foundry.  If the item class implements IItemWithMeshDefinition, then a
	 * variant model will be registered with the ModelBakery.  If the output item is ItemCastingMold, then the result
	 * will be added to ItemCastingMold's list of subitems.
	 * @param itemStack - the ItemStack to make a mold of
	 * @param mold - the unmolded mold item, probably also an ItemCastingMold
	 * @param resourceDomain - where to look for a json file describing the item mesh, if using ItemCastingMold
	 */
	public RecipeToolMold(Item output, ItemStack itemStack, Item mold, String resourceDomain) {
		this.output = output;
		this.input = itemStack;
		this.mold = new ItemStack(mold, 1, 0);
		this.resourceDomain = resourceDomain;
		
		if(output instanceof IItemWithMeshDefinition) {
			ItemStack tempResult = new ItemStack(output);
			addImprint(itemStack, tempResult, resourceDomain);
			EasyRegistry.registerSpecificItemVariantsWithBakery(cast(output), tempResult);
			if(output == HardLibAPI.itemMold) {
				allMoldItems.add(new RecipeSubItem(this.input, resourceDomain));
			}
		}
	}

	private static <T extends Item & IItemWithMeshDefinition>T cast(Item item) {
		if(!(item instanceof IItemWithMeshDefinition)) {
			throw new RuntimeException(item.getClass().getSimpleName() + " does not implement IItemWithMeshDefinition!");
		}
		return (T)item;
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
					/*NBTTagCompound nbt = new NBTTagCompound();
					NBTTagCompound itemTag = new NBTTagCompound();
					input.writeToNBT(itemTag);
					nbt.setTag("expindustry:item_mold", itemTag);
					out.setTagCompound(nbt);
					if(resourceDomain != null) {
						nbt.setString("expindustry:resourceDomain", resourceDomain);
					}*/
					out = addImprint(input,out,resourceDomain);
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
	
	public static ItemStack addImprint(ItemStack imprintStack, ItemStack moldToImprint, String resourceDomain) {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound itemTag = new NBTTagCompound();
		imprintStack.writeToNBT(itemTag);
		nbt.setTag("expindustry:item_mold", itemTag);
		if(resourceDomain != null) {
			nbt.setString("expindustry:resourceDomain", resourceDomain);
		}
		moldToImprint.setTagCompound(nbt);
		return moldToImprint;
	}
	
	public static class RecipeSubItem {
		public final ItemStack input;
		public final String resourceDomain;
		
		public RecipeSubItem(ItemStack i, String domain) {
			input = i;
			resourceDomain = domain;
		}
	}
}
