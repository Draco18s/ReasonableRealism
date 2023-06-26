package com.draco18s.harderores.block.ore;

import java.awt.Color;

import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;

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
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return setNbtOnStack(super.getCloneItemStack(state, target, level, pos, player), BlockProperties.ORE_DENSITY, state);
	}

	public static ItemStack setNbtOnStack(ItemStack stack, Property<?> prop, BlockState state) {
		CompoundTag compoundtag = new CompoundTag();
		compoundtag.putString(prop.getName(), String.valueOf(state.getValue(prop)));
		stack.addTagElement("BlockStateTag", compoundtag);
		return stack;
	}

	public static <V extends Comparable<V>> ItemStack setNbtOnStack(ItemStack stack, Property<V> prop, V val) {
		CompoundTag compoundtag = new CompoundTag();
		compoundtag.putString(prop.getName(), String.valueOf(val));
		stack.addTagElement("BlockStateTag", compoundtag);
		return stack;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if(player.isCreative() && !player.isCrouching()) {
			return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
		}
		int val = Math.max(state.getValue(BlockProperties.ORE_DENSITY) - metaChange, 0);
        if(val > 0) {
        	level.setBlock(pos, state.setValue(BlockProperties.ORE_DENSITY, val), level.isClientSide ? 11 : 3);
        	return true;
        }
		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	/*@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(player != null && player.abilities.isCreativeMode && player.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
			if(!world.isRemote) {
				int m = state.get(BlockProperties.ORE_DENSITY);
				m = m - (player.isSneaking()?1:4);
				if(m < 1)
					m += 16;
				world.setBlockState(pos, state.with(BlockProperties.ORE_DENSITY, m), 3);
			}
			return true;
		}
		return false;
	}

	public static final Direction[] DROP_SEARCH_DIRECTIONS = {Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.DOWN};

	public static void tspawnAsEntity(World worldIn, BlockPos pos, ItemStack stack) {
		if (!worldIn.isRemote && worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && !worldIn.restoringBlockSnapshots) { // do not drop items while restoring blockstates, prevents item dupe
			//if (captureDrops.get()) {
			//	capturedDrops.get().add(stack);
			//	return;
			//}
			//float f = 0.5F;
			double d0 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
			double d1 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
			double d2 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
			if(!worldIn.getBlockState(pos).isSolid()) {
				ItemEntity entityitem = new ItemEntity(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
				entityitem.setDefaultPickupDelay();
				worldIn.addEntity(entityitem);
				return;
			}
			else {
				for(Direction dir : DROP_SEARCH_DIRECTIONS) {
					if(!worldIn.getBlockState(pos.offset(dir)).isSolid() || dir == Direction.DOWN) {
						ItemEntity entityitem = new ItemEntity(worldIn, (double)pos.getX() + d0+dir.getXOffset(), (double)pos.getY() + (dir.getYOffset() != 0 ? 0.05D : d1)+dir.getYOffset(), (double)pos.getZ() + d2+dir.getZOffset(), stack);
						double signX = Math.signum(dir.getXOffset());
						double signZ = Math.signum(dir.getZOffset());
						entityitem.setMotion((worldIn.rand.nextDouble() * 0.1D) * signX, 0.1D, (worldIn.rand.nextDouble() * 0.1D) * signZ);
						entityitem.setDefaultPickupDelay();
						worldIn.addEntity(entityitem);
						return;
					}
				}
			}
		}
	}*/

	@Override
	public int getDensityChangeOnBreak(LevelReader worldIn, BlockPos pos, BlockState state) {
		return metaChange;
	}

	@Override
	public Color getProspectorParticleColor(LevelReader worldIn, BlockPos pos, BlockState state) {
		return particleColor;
	}
}
