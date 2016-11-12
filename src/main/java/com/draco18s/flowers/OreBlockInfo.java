package com.draco18s.flowers;

import java.util.ArrayList;
import java.util.List;

import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.interfaces.IBlockMultiBreak;
import com.draco18s.hardlib.interfaces.IHardOres;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreBlockInfo implements IHardOres {
	@Override
	public boolean isHardOre(IBlockState b) {
		return b.getBlock() instanceof IBlockMultiBreak && b.getProperties().containsKey(Props.ORE_DENSITY);
	}

	@Override
	public List<ItemStack> mineHardOreOnce(World world, BlockPos pos, int fortune) {
		return this.mineHardOreOnce(world, pos, fortune, Blocks.AIR.getDefaultState());
	}

	@Override
	public List<ItemStack> mineHardOreOnce(World world, BlockPos pos, int fortune, IBlockState replacement) {
		IBlockState state = world.getBlockState(pos);
		if(isHardOre(state)) {
			int val = state.getValue(Props.ORE_DENSITY);
			int change = ((IBlockMultiBreak)state.getBlock()).getDensityChangeOnBreak(world, pos, state);
			if(val > change) {
				world.setBlockState(pos, state.withProperty(Props.ORE_DENSITY, val-change));	
			}
			else {
				world.setBlockState(pos, replacement);
			}
			List<ItemStack> allDrops = state.getBlock().getDrops(world, pos, state, fortune);
			allDrops.subList(1, allDrops.size()).clear();
			return allDrops;
		}
		return null;
	}

	@Override
	public List<ItemStack> getHardOreDropsOnce(World world, BlockPos pos, int fortune) {
		IBlockState state = world.getBlockState(pos);
		if(isHardOre(state)) {
			List<ItemStack> allDrops = state.getBlock().getDrops(world, pos, state, fortune);
			allDrops.subList(1, allDrops.size()).clear();
			return allDrops;
		}
		return null;
	}
}
