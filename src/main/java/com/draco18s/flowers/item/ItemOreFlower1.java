package com.draco18s.flowers.item;

import java.util.List;

import com.draco18s.hardlib.blockproperties.flowers.EnumOreFlower1;
import com.draco18s.hardlib.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemOreFlower1 extends ItemBlock {
	private final Class<? extends IMetaLookup> prop;
	private final int metaOffset;

	public ItemOreFlower1(Block block, int metaoffset, Class<? extends IMetaLookup> prop) {
		super(block);
		setHasSubtypes(true);
		this.prop = prop;
		this.metaOffset = metaoffset;
	}

	@Override
	public int getMetadata(int damage) {
        return damage;
    }
	
	@Override
    public String getUnlocalizedName(ItemStack stack) {
		//EnumOreType type = EnumOreType.values()[stack.getMetadata()+metaOffset];
        //return "item."+prop.getByOrdinal(stack.getMetadata()).name();
		return "item.oreflowers:"+((IMetaLookup)prop.getEnumConstants()[0].getByOrdinal(stack.getMetadata())).getVariantName();
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		EnumOreType type = EnumOreType.values()[stack.getMetadata()+metaOffset];
		tooltip.add(I18n.format("indicator."+type.getVariantName()));
	}
}
