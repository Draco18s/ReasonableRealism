package com.draco18s.farming.block;


import com.draco18s.farming.HarderFarming;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Weeds as a block of their own, which they were until they moved into the farmland (WeedyFarmlandBlock). Any left
 * in a world move there on their next random tick. They are slow to clear by hand and wear down the tool used.
 */
public class CropWeedsBlock extends CropBlock {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_15;

	public CropWeedsBlock(Properties props) {
		super(props.mapColor(MapColor.PLANT).pushReaction(PushReaction.POPPED).randomTicks().strength(2, 0).noCollision().sound(SoundType.CROP));
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

	/** Moves into the farmland below (as weeds of the same growth), or just grows and spreads off farmland. */
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (!world.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		BlockState below = world.getBlockState(pos.below());
		if (below.is(Blocks.FARMLAND)) {
			world.setBlock(pos.below(), HarderFarming.ModBlocks.weedy_farmland.defaultBlockState()
					.setValue(WeedyFarmlandBlock.MOISTURE, below.getValue(WeedyFarmlandBlock.MOISTURE))
					.setValue(WeedyFarmlandBlock.WEEDS, Math.min(this.getAge(state), WeedyFarmlandBlock.MAX_WEEDS)), 3);
			world.removeBlock(pos, false);
		}
		else if (world.getRawBrightness(pos, 0) >= 9 && random.nextBoolean()) {
			int i = this.getAge(state);
			if (i < this.getMaxAge()) {
				world.setBlock(pos, this.getStateForAge(i + 1), 2);
			} else {
				WeedyFarmlandBlock.weedSpread(world, pos.below(), random);
			}
		}
	}

	/** Tough to pull: fast only with a good tool, and a slow grind by hand. */
	@Override
	@Deprecated
	public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
		float hardness = state.getDestroySpeed(world, pos);
		ItemStack s = player.getMainHandItem();
		Tool tool = s.get(DataComponents.TOOL);
		if(tool == null) {
			return 1f / 90;
		}
		// The tool's best speed (what its tier's speed was).
		hardness += tool.rules().stream().flatMap(rule -> rule.speed().stream()).max(Float::compare).orElse(tool.defaultMiningSpeed());
		// Efficiency adds level^2+1 to this attribute (since 1.21), as it used to add here.
		hardness += (float)player.getAttributeValue(Attributes.MINING_EFFICIENCY);
		return hardness / state.getDestroySpeed(world, pos) / 45;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
		ItemStack stack = player.getMainHandItem();
		if(stack.has(DataComponents.TOOL)) {
			stack.hurtAndBreak(stack.is(ItemTags.HOES) ? 1 : 2, player, EquipmentSlot.MAINHAND);
		}
		return super.onDestroyedByPlayer(state, world, pos, player, toolStack, willHarvest, fluid);
	}
}
