package com.draco18s.ores.block.ore;

import java.util.Random;

import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.ores.OresBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BlockLimonite extends Block {

	public BlockLimonite() {
		super(Material.GROUND,MapColor.ADOBE);
		setHardness(3.0f);
		setHarvestLevel("shovel", 0);
		setResistance(1.0f);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}
	
	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		int r = 1;
		if(fortune > 0) r += random.nextInt(1);
		r += random.nextInt(1);
		return r;
    }
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return OresBase.rawOre;
    }

	@Override
	public int damageDropped(IBlockState state) {
        return EnumOreType.LIMONITE.meta;
    }
}
