package com.draco18s.ores.item;

import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.ores.OresBase;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

public class ItemDiamondStudShovel extends ItemSpade {

	public ItemDiamondStudShovel(ToolMaterial material) {
		super(material);
	}

	//no longer needeed
	/*@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairItem) {
		if(repairItem.getItem() == OresBase.rawOre && repairItem.getMetadata() == EnumOreType.DIAMOND.meta) {
			return true;
		}
		return false;
	}*/
}
