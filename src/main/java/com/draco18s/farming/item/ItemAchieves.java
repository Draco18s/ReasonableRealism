package com.draco18s.farming.item;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.farming.util.EnumFarmAchieves;
import com.draco18s.hardlib.blockproperties.ores.EnumOreType;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAchieves extends Item {
	public ItemAchieves() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, EnumFarmAchieves.KILL_WEEDS.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumFarmAchieves.CROP_ROTATION.meta));
		subItems.add(new ItemStack(itemIn, 1, EnumFarmAchieves.THERMOMETER.meta));
	}
}
