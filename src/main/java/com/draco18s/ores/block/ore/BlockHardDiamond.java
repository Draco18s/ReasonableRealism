package com.draco18s.ores.block.ore;

import java.awt.Color;
import java.util.Random;

import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.hardlib.blockproperties.Props;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockHardDiamond extends BlockHardOreBase {
	Color color = new Color(0x5decf5);

	public BlockHardDiamond() {
		super(EnumOreType.DIAMOND, 3);
		setHardness(12.0f);
		setHarvestLevel("pickaxe", 2);
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		float f = 0;
		if(fortune > 0) {
			f = random.nextInt(fortune+(state.getValue(Props.ORE_DENSITY))/5+1);
		}
		return 1 + Math.round(f/1.0f);
	}
	
	@Override
	public Color getProspectorParticleColor(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
		return color;
	}
}
