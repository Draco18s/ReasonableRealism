package com.draco18s.ores.item;

import java.util.List;

import com.draco18s.hardlib.blockproperties.EnumOreType;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemOreFlower1 extends ItemBlock {

	public ItemOreFlower1(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
        return damage;
    }
	
	@Override
    public String getUnlocalizedName(ItemStack stack) {
		EnumOreType type = EnumOreType.values()[stack.getMetadata()];
        return "item." + type.getVariantName();
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
		EnumOreType type = EnumOreType.values()[stack.getMetadata()];
		I18n.format(type.getVariantName() + ".indicator");
	}
}
