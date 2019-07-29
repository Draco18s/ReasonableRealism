package com.draco18s.flowers;

import java.util.List;

import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;
import com.draco18s.hardlib.api.interfaces.IHardOres;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class OreBlockInfo implements IHardOres {
	@Override
	public boolean isHardOre(BlockState b) {
		return b.getBlock() instanceof IBlockMultiBreak && b.getProperties().contains(BlockProperties.ORE_DENSITY);
	}

	@Override
	public List<ItemStack> mineHardOreOnce(World world, BlockPos pos, ItemStack stack) {
		return this.mineHardOreOnce(world, pos, stack, Blocks.AIR.getDefaultState());
	}

	@Override
	public List<ItemStack> mineHardOreOnce(World world, BlockPos pos, ItemStack stack, BlockState replacement) {
		BlockState state = world.getBlockState(pos);
		if(isHardOre(state)) {
			int val = state.get(BlockProperties.ORE_DENSITY);
			int change = ((IBlockMultiBreak)state.getBlock()).getDensityChangeOnBreak(world, pos, state);
			if(val > change) {
				world.setBlockState(pos, state.with(BlockProperties.ORE_DENSITY, val-change));	
			}
			else {
				world.setBlockState(pos, replacement);
			}
			LootContext.Builder builder = (new LootContext.Builder((ServerWorld)world)).withRandom(world.rand).withParameter(LootParameters.POSITION, pos).withParameter(LootParameters.TOOL, stack);
			List<ItemStack> allDrops = state.getDrops(builder);
			allDrops.subList(1, allDrops.size()).clear();
			return allDrops;
		}
		return null;
	}

	@Override
	public List<ItemStack> getHardOreDropsOnce(World world, BlockPos pos, ItemStack stack) {
		BlockState state = world.getBlockState(pos);
		if(isHardOre(state)) {
			LootContext.Builder builder = (new LootContext.Builder((ServerWorld)world)).withRandom(world.rand).withParameter(LootParameters.POSITION, pos).withParameter(LootParameters.TOOL, stack);
			List<ItemStack> allDrops = state.getDrops(builder);
			allDrops.subList(1, allDrops.size()).clear();
			return allDrops;
		}
		return null;
	}
}
