package com.draco18s.farming.item;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.farming.util.EnumFarmAchieves;
import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAchieves extends Item {
	public ItemAchieves() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab) || tab == CreativeTabs.SEARCH) {
			subItems.add(new ItemStack(this, 1, EnumFarmAchieves.KILL_WEEDS.meta));//0
			subItems.add(new ItemStack(this, 1, EnumFarmAchieves.CROP_ROTATION.meta));//1
			subItems.add(new ItemStack(this, 1, EnumFarmAchieves.THERMOMETER.meta));//2
		}
	}

	@Override
    @Nullable
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab()
    {
        return null;
    }
}
