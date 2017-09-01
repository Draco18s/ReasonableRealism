package com.draco18s.flowers.item;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlower3;
import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.api.internal.IMetaLookup;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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
		return "item.oreflowers:"+((IMetaLookup)prop.getEnumConstants()[0].getByOrdinal(stack.getMetadata())).getVariantName();
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		EnumOreType type = EnumOreType.values()[stack.getMetadata()+metaOffset];
		tooltip.add(I18n.format("indicator."+type.getVariantName()));
		if(stack.getItem() == Item.getItemFromBlock(OreFlowersBase.oreFlowers3) && stack.getMetadata() == 2) {
			tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("tooltip:oreflowers:april.one"));
		}
    }
}
