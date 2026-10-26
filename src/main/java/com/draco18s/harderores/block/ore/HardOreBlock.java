package com.draco18s.harderores.block.ore;

import java.awt.Color;

import javax.annotation.Nullable;

import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

public class HardOreBlock extends Block implements IBlockMultiBreak {
	private final int metaChange;
	private final Color particleColor;

	public HardOreBlock(int metaDecrement, Color particleColorIn, Properties properties) {
		super(properties);
		metaChange = metaDecrement;
		particleColor = particleColorIn;
		registerDefaultState(this.stateDefinition.any().setValue(BlockProperties.ORE_DENSITY, 16));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockProperties.ORE_DENSITY);
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
		return setNbtOnStack(super.getCloneItemStack(level, pos, state, includeData, player), BlockProperties.ORE_DENSITY, state);
	}

	/** Sets the state the item places (its block_state component, which was the BlockStateTag NBT). */
	public static <V extends Comparable<V>> ItemStack setNbtOnStack(ItemStack stack, Property<V> prop, BlockState state) {
		stack.update(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY, props -> props.with(prop, state));
		return stack;
	}

	public static <V extends Comparable<V>> ItemStack setNbtOnStack(ItemStack stack, Property<V> prop, V val) {
		stack.update(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY, props -> props.with(prop, val));
		return stack;
	}

	/** The density an ore item places at, 16 if it doesn't say. */
	public static int getDensity(ItemStack stack) {
		Integer density = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).get(BlockProperties.ORE_DENSITY);
		return density == null ? 16 : density;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
		// Silk touch takes the whole block, density and all (the loot table copies the density onto the item).
		if((player.isCreative() && !player.isCrouching()) || HardLib.getEnchantmentLevel(level.registryAccess(), Enchantments.SILK_TOUCH, player.getMainHandItem()) > 0) {
			return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
		}
		int val = Math.max(state.getValue(BlockProperties.ORE_DENSITY) - metaChange, 0);
        if(val > 0) {
        	level.setBlock(pos, state.setValue(BlockProperties.ORE_DENSITY, val), level.isClientSide() ? 11 : 3);
        	return true;
        }
		return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
	}

	/** Creative tool: right-click with an empty hand to lower the density by 4 (by 1 while sneaking), wrapping 1 to 16. */
	@Override
	@Deprecated
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if(!player.getAbilities().instabuild) {
			return InteractionResult.PASS;
		}
		if(!world.isClientSide()) {
			int m = state.getValue(BlockProperties.ORE_DENSITY) - (player.isShiftKeyDown() ? 1 : 4);
			if(m < 1) m += 16;
			world.setBlock(pos, state.setValue(BlockProperties.ORE_DENSITY, m), 3);
		}
		return InteractionResult.SUCCESS;
	}

	/** Like Block.playerDestroy, but the drops come out of an open face: the block is usually still there. */
	@Override
	public void playerDestroy(ServerLevel world, ServerPlayer player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack tool) {
		player.awardStat(Stats.BLOCK_MINED.get(this));
		player.causeFoodExhaustion(0.005F);
		if(world instanceof ServerLevel level) {
			getDrops(state, level, pos, be, player, tool).forEach(stack -> spawnOnOpenFace(level, pos, stack));
			state.spawnAfterBreak(level, pos, tool, true);
		}
	}

	public static final Direction[] DROP_SEARCH_DIRECTIONS = {Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.DOWN};

	/** Drops at the block if it's gone, else just outside its first open face (straight down if it has none). */
	public static void spawnOnOpenFace(Level world, BlockPos pos, ItemStack stack) {
		if(!(world instanceof ServerLevel level) || stack.isEmpty() || !level.getGameRules().get(GameRules.BLOCK_DROPS) || world.restoringBlockSnapshots) return;
		RandomSource rand = world.getRandom();
		double d0 = rand.nextFloat() * 0.5F + 0.25D;
		double d1 = rand.nextFloat() * 0.5F + 0.25D;
		double d2 = rand.nextFloat() * 0.5F + 0.25D;
		if(!world.getBlockState(pos).canOcclude()) {
			ItemEntity entityitem = new ItemEntity(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
			entityitem.setDefaultPickUpDelay();
			world.addFreshEntity(entityitem);
			return;
		}
		for(Direction dir : DROP_SEARCH_DIRECTIONS) {
			if(!world.getBlockState(pos.relative(dir)).canOcclude() || dir == Direction.DOWN) {
				ItemEntity entityitem = new ItemEntity(world, pos.getX() + d0 + dir.getStepX(), pos.getY() + (dir.getStepY() != 0 ? 0.05D : d1) + dir.getStepY(), pos.getZ() + d2 + dir.getStepZ(), stack);
				entityitem.setDeltaMovement(rand.nextDouble() * 0.1D * dir.getStepX(), 0.1D, rand.nextDouble() * 0.1D * dir.getStepZ());
				entityitem.setDefaultPickUpDelay();
				world.addFreshEntity(entityitem);
				return;
			}
		}
	}

	@Override
	public int getDensityChangeOnBreak(LevelReader worldIn, BlockPos pos, BlockState state) {
		return metaChange;
	}

	@Override
	public Color getProspectorParticleColor(LevelReader worldIn, BlockPos pos, BlockState state) {
		return particleColor;
	}
}
