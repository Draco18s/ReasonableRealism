package com.draco18s.farming.block;

import java.util.ArrayList;
import java.util.List;

import com.draco18s.farming.HarderFarming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Weeds: show up on bare farmland next to crops, grow tall, and spread to nearby farmland (or as grass onto
 * grass blocks). They are slow to clear by hand and wear down the tool used. Carpet over farmland keeps them
 * out entirely: they neither spawn nor spread under it, and can't push it off.
 */
public class CropWeedsBlock extends CropBlock {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_15;

	public CropWeedsBlock() {
		super(Properties.of(Material.PLANT).randomTicks().strength(2, 0).noCollission().sound(SoundType.CROP));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}

	@Override
	public int getMaxAge() {
		return 15;
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		// Shapes beyond the crop's 8 stages stay at full height.
		return Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, Math.min(state.getValue(AGE), 7)).getShape(world, pos, context);
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return HarderFarming.ModItems.weed_seeds;
	}

	/** Grows to 7, spreads (pausing at 7 for one tick), then keeps growing to 15 and spreads every tick after. */
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (!world.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		if (world.getRawBrightness(pos, 0) >= 9 && random.nextBoolean()) {
			int i = this.getAge(state);
			if (i < this.getMaxAge() && i != 7) {
				world.setBlock(pos, this.getStateForAge(i + 1), 2);
			} else {
				weedSpread(world, pos, random);
				world.setBlock(pos, this.getStateForAge(i == 7 ? 8 : 7), 2);
			}
		}
	}

	public static void weedSpread(Level world, BlockPos pos, RandomSource rand) {
		List<BlockPos> validPlacements = new ArrayList<BlockPos>();
		for(BlockPos p : BlockPos.betweenClosed(pos.offset(-5,0,-5), pos.offset(5,0,5))) {
			BlockPos dwn = p.below();
			if (world.getBlockState(dwn.below()).is(Blocks.FARMLAND)) {
				if (canPlantWeeds(world, dwn)) validPlacements.add(dwn.immutable());
			} else if (world.getBlockState(dwn).is(Blocks.FARMLAND)) {
				if (canPlantWeeds(world, p)) validPlacements.add(p.immutable());
			} else if (world.getBlockState(p).is(Blocks.FARMLAND)) {
				if (canPlantWeeds(world, p.above())) validPlacements.add(p.above());
			}
			else if(world.getBlockState(dwn.below()).is(Blocks.GRASS_BLOCK) && rand.nextInt(10) == 0) {
				if(world.getBlockState(dwn).isAir()) validPlacements.add(dwn.immutable());
			}
			else if(world.getBlockState(dwn).is(Blocks.GRASS_BLOCK) && rand.nextInt(10) == 0) {
				if(world.getBlockState(p).isAir()) validPlacements.add(p.immutable());
			}
			else if(world.getBlockState(p).is(Blocks.GRASS_BLOCK) && rand.nextInt(10) == 0) {
				if(world.getBlockState(p.above()).isAir()) validPlacements.add(p.above());
			}
		}
		if(validPlacements.isEmpty()) return;
		BlockPos p = validPlacements.get(rand.nextInt(validPlacements.size()));
		if(world.getBlockState(p.below()).is(Blocks.FARMLAND))
			world.setBlock(p, HarderFarming.ModBlocks.crop_weeds.defaultBlockState(), 3);
		else
			world.setBlock(p, Blocks.GRASS.defaultBlockState(), 3);
	}

	/** Only open farmland: a carpet (or anything else) over it keeps weeds out. */
	private static boolean canPlantWeeds(Level world, BlockPos pos) {
		return world.getBlockState(pos).isAir();
	}

	/** Tough to pull: fast only with a good tool, and a slow grind by hand. */
	@Override
	@Deprecated
	public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
		float hardness = state.getDestroySpeed(world, pos);
		ItemStack s = player.getMainHandItem();
		if(!(s.getItem() instanceof TieredItem tiered)) {
			return 1f / 90;
		}
		hardness += tiered.getTier().getSpeed();
		int i = EnchantmentHelper.getBlockEfficiency(player);
		if (i > 0) {
			hardness += i * i + 1;
		}
		return hardness / state.getDestroySpeed(world, pos) / 45;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		ItemStack stack = player.getMainHandItem();
		if(stack.getItem() instanceof TieredItem) {
			stack.hurtAndBreak(stack.getItem() instanceof HoeItem ? 1 : 2, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
		}
		return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
	}
}
