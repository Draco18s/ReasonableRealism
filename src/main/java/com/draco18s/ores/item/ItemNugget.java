package com.draco18s.ores.item;

import java.util.List;

import org.apache.logging.log4j.Level;

import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.ores.OresBase;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemNugget extends Item {

	public ItemNugget() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	@Override
	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		EnumOreType v = EnumOreType.values()[stack.getItemDamage()];
		/*switch(v) {
			case LIMONITE:
				return "item.harderores:"+v.name+"_nugget";
			case IRON:
				return "item.harderores:"+v.name+"_nugget";
			default:
				return "item.harderores:unknown_nugget";
		}*/
		return "item.harderores:"+v.name+"_nugget";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			//OresBase.logger.log(Level.DEBUG, "Adding 1 nugget");
			subItems.add(new ItemStack(this, 1, EnumOreType.IRON.meta));
		}
	}
}
