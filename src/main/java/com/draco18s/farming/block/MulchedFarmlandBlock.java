package com.draco18s.farming.block;

import com.draco18s.farming.HarderFarming;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.farming.Mulch;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.Nullable;

/**
 * Farmland mulched with a carpet or leaf litter, laid at the base of the crop (see EventHandler.onMulch). Mulch
 * keeps weeds out and stops trampling. Carpets come back off with shears; leaves can't be taken back, and rot away
 * once the crop is harvested (or anything else leaves the space above empty).
 * Otherwise it's farmland: it dries out, and a solid block above turns it to dirt (dropping a carpet).
 */
public class MulchedFarmlandBlock extends FarmlandBlock {
	public static final EnumProperty<Mulch> MULCH = BlockProperties.MULCH;

	public MulchedFarmlandBlock(BlockBehaviour.Properties props) {
		super(Blocks.DIRT, props);
		this.registerDefaultState(this.defaultBlockState().setValue(MULCH, Mulch.WHITE));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(MULCH);
	}

	/** Mulch farmland (plain or weedy: the mulch smothers weeds), keeping its moisture. */
	public static void mulch(Level world, BlockPos farmland, Mulch mulch) {
		BlockState state = world.getBlockState(farmland);
		world.setBlock(farmland, HarderFarming.ModBlocks.mulched_farmland.defaultBlockState()
				.setValue(MOISTURE, state.getValue(MOISTURE)).setValue(MULCH, mulch), 3);
	}

	/** Plain farmland again, with the same moisture. */
	public static BlockState unmulched(BlockState state) {
		return Blocks.FARMLAND.defaultBlockState().setValue(MOISTURE, state.getValue(MOISTURE));
	}

	/** The carpet, if it can come back off. */
	public static ItemStack retrieve(BlockState state) {
		Mulch mulch = state.getValue(MULCH);
		return mulch.retrievable() ? new ItemStack(mulch.item()) : ItemStack.EMPTY;
	}

	/** Leaves rot away once nothing grows through them any more. */
	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour,
			BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
		state = super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
		if(directionToNeighbour == Direction.UP && neighbourState.isAir() && state.is(this) && state.getValue(MULCH) == Mulch.LEAVES) {
			return unmulched(state);
		}
		return state;
	}

	/** Mulch takes the fall: no trampling. */
	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
		entity.causeFallDamage(fallDistance * (1.0F - this.getFallDistanceReduction()), 1.0F, entity.damageSources().fall());
	}

	/** Drying out (or being covered) to dirt drops a carpet. */
	@Override
	public void turnToBaseBlock(@Nullable Entity sourceEntity, BlockState state, Level level, BlockPos pos) {
		if(!level.isClientSide()) {
			Block.popResource(level, pos.above(), retrieve(state));
		}
		super.turnToBaseBlock(sourceEntity, state, level, pos);
	}
}
