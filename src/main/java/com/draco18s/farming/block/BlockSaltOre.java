package com.draco18s.farming.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.farming.FarmingBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;

public class BlockSaltOre extends Block {
	public BlockSaltOre() {
		super(Material.ROCK);
		setHardness(1.0f);
		setHarvestLevel("pickaxe", 0);
		setResistance(2.5f);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		if (Loader.isModLoaded("harderores")) {
			return FarmingBase.rawSalt;
		}
		return FarmingBase.saltPile;
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		if (Loader.isModLoaded("harderores")) {
			return 1 + random.nextInt(2) + random.nextInt(fortune + 1);
		}
		return 1 + random.nextInt(3) + random.nextInt(fortune * 2 + 1);
	}
}
