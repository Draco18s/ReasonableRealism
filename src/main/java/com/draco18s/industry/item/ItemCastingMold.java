package com.draco18s.industry.item;

import java.util.List;

import com.draco18s.hardlib.api.interfaces.IItemWithMeshDefinition;
import com.draco18s.hardlib.api.recipes.RecipeToolMold;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCastingMold extends Item implements IItemWithMeshDefinition {
	public ItemCastingMold() {
		//setHasSubtypes(true);
		setMaxDamage(32);
		setNoRepair();
		setCreativeTab(CreativeTabs.MATERIALS);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		ItemStack base = new ItemStack(itemIn);
		subItems.add(base);
		boolean skip = false;
		for(RecipeToolMold.RecipeSubItem stack : RecipeToolMold.getAllmolditems()) {
			subItems.add(RecipeToolMold.addImprint(base.copy(),stack.input, stack.resourceDomain));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			NBTTagCompound itemTags = nbt.getCompoundTag("expindustry:item_mold");
			ItemStack result = ItemStack.loadItemStackFromNBT(itemTags);
			Object[] list = result.getDisplayName().split("[ -]");
			if(list.length == 1) {
				tooltip.add(I18n.format("tooltip.expindustry:imprint1.text", list));
			}
			else if(list.length == 2) {
				tooltip.add(I18n.format("tooltip.expindustry:imprint2.text", list));
			}
			else if(list.length == 3) {
				tooltip.add(I18n.format("tooltip.expindustry:imprint3.text", list));
			}
			else if(list.length == 4) {
				tooltip.add(I18n.format("tooltip.expindustry:imprint4.text", list));
			}
		}
	}
	
	@Override
	public int getMaxDamage(ItemStack stack) {
		if(stack.hasTagCompound())
			return super.getMaxDamage(stack);
		return 0;
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		if(stack.hasTagCompound())
			return 1;
		return super.getItemStackLimit(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemMeshDefinition getMeshDefinition() {
		return new ItemMeshDefinition()
		{
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				if(stack.hasTagCompound()) {
					NBTTagCompound nbt = stack.getTagCompound();
					if(nbt.hasKey("expindustry:item_mold")) {
						NBTTagCompound itemTags = nbt.getCompoundTag("expindustry:item_mold");
						ItemStack result = ItemStack.loadItemStackFromNBT(itemTags);
						String imprintName = result.getUnlocalizedName();
						String domain = "expindustry";
						if(nbt.hasKey("expindustry:resourceDomain")) {
							domain = nbt.getString("expindustry:resourceDomain");
						}
						if(imprintName.contains(":")) {
							imprintName = imprintName.substring(imprintName.indexOf(':')+1);
						}
						imprintName = imprintName.replaceAll("tile.", "");
						imprintName = imprintName.replaceAll("item.", "");
						imprintName = imprintName.replaceAll("[Ii]ron", "");
						ResourceLocation loc = new ResourceLocation(domain, "mold_"+imprintName);
						ModelResourceLocation fullModelLocation = new ModelResourceLocation(loc, "inventory");
						return fullModelLocation;
					}
				}
				return new ModelResourceLocation(new ResourceLocation("expindustry", "mold_clean"), "inventory");
			}
		};
	}
}
