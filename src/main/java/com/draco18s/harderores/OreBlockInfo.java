package com.draco18s.harderores;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;
import com.draco18s.hardlib.api.interfaces.IHardOres;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

/** HardLibAPI.hardOres: hard ore blocks lose a layer of density each time they are mined. */
public class OreBlockInfo implements IHardOres {
	@Override
	public boolean isHardOre(BlockState b) {
		return b.getBlock() instanceof IBlockMultiBreak && b.hasProperty(BlockProperties.ORE_DENSITY);
	}

	@Override
	@Nullable
	public List<ItemStack> mineHardOreOnce(Level world, ServerPlayer entityPlayer, BlockPos pos, ItemStack stack) {
		return mineHardOreOnce(world, entityPlayer, pos, stack, Blocks.AIR.defaultBlockState());
	}

	@Override
	@Nullable
	public List<ItemStack> mineHardOreOnce(Level world, ServerPlayer entityPlayer, BlockPos pos, ItemStack stack, BlockState replacement) {
		BlockState state = world.getBlockState(pos);
		if(!isHardOre(state) || !(world instanceof ServerLevel level)) return null;
		boolean canHarvest = entityPlayer != null ? state.canHarvestBlock(world, pos, entityPlayer) : !state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state);
		if(!canHarvest) return null;

		int val = state.getValue(BlockProperties.ORE_DENSITY);
		int change = ((IBlockMultiBreak)state.getBlock()).getDensityChangeOnBreak(world, pos, state);
		List<ItemStack> drops = getDrops(level, pos, state, stack, entityPlayer);
		// Silk touch takes the whole block (its drop keeps the density).
		boolean whole = HardLib.getEnchantmentLevel(world.registryAccess(), Enchantments.SILK_TOUCH, stack) > 0 || val <= change;
		world.setBlock(pos, whole ? replacement : state.setValue(BlockProperties.ORE_DENSITY, val - change), 3);
		return drops;
	}

	@Override
	@Nullable
	public List<ItemStack> getHardOreDropsOnce(Level world, BlockPos pos, ItemStack stack) {
		BlockState state = world.getBlockState(pos);
		if(!isHardOre(state) || !(world instanceof ServerLevel level)) return null;
		return getDrops(level, pos, state, stack, null);
	}

	/** One break's worth: the first drop of the block's loot table (silk touch or ore chunks, not both). */
	private static List<ItemStack> getDrops(ServerLevel level, BlockPos pos, BlockState state, ItemStack stack, @Nullable ServerPlayer player) {
		LootParams.Builder builder = new LootParams.Builder(level)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
				.withParameter(LootContextParams.TOOL, stack)
				.withOptionalParameter(LootContextParams.THIS_ENTITY, player)
				.withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));
		List<ItemStack> allDrops = state.getDrops(builder);
		if(allDrops.size() > 1) {
			allDrops.subList(1, allDrops.size()).clear();
		}
		return allDrops;
	}
}
