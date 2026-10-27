package com.draco18s.farming.block;

import java.util.ArrayList;
import java.util.List;

import com.draco18s.farming.HarderFarming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Farmland overgrown with weeds. The weeds live in the farmland (no block entity) and are drawn in the space above
 * it, among whatever crop grows there. A crop over weeds loses its growth ticks to them (see
 * EventHandler.handleCrops), and a hoe pulls them without touching the crop (EventHandler.onHoeWeeds).
 * Otherwise it's farmland: it dries out, and trampling or a solid block above turns it (weeds and all) to dirt.
 */
public class WeedyFarmlandBlock extends FarmlandBlock {
	/** How grown the weeds are: they spread once fully grown. */
	public static final IntegerProperty WEEDS = BlockStateProperties.AGE_7;
	public static final int MAX_WEEDS = 7;

	public WeedyFarmlandBlock(BlockBehaviour.Properties props) {
		super(Blocks.DIRT, props);
		this.registerDefaultState(this.defaultBlockState().setValue(WEEDS, 0));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WEEDS);
	}

	/** Moisture as farmland; then the weeds grow on their own ticks, as they did as a block of their own. */
	@Override
	protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		super.randomTick(state, world, pos, random);
		if (world.getBlockState(pos) != state) return; // dried out to dirt
		if (world.getRawBrightness(pos.above(), 0) >= 9 && random.nextBoolean()) {
			growWeeds(world, pos, state, random);
		}
	}

	/** The weeds grow a stage, or spread once fully grown. */
	public static void growWeeds(Level world, BlockPos pos, BlockState state, RandomSource random) {
		int weeds = state.getValue(WEEDS);
		if (weeds < MAX_WEEDS) {
			world.setBlock(pos, state.setValue(WEEDS, weeds + 1), 2);
		}
		else {
			weedSpread(world, pos, random);
		}
	}

	/** Back to plain farmland, keeping the moisture. */
	public static void clearWeeds(Level world, BlockPos pos, BlockState state) {
		world.setBlock(pos, Blocks.FARMLAND.defaultBlockState().setValue(MOISTURE, state.getValue(MOISTURE)), 3);
	}

	/** Weeds in plain farmland: the same moisture, and new weeds. */
	public static void plantWeeds(Level world, BlockPos farmland) {
		BlockState state = world.getBlockState(farmland);
		world.setBlock(farmland, HarderFarming.ModBlocks.weedy_farmland.defaultBlockState().setValue(MOISTURE, state.getValue(MOISTURE)), 3);
	}

	/** True for the farmland (or what's growing on it) of a weedy patch. */
	public static boolean isWeedy(BlockGetter world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() instanceof WeedyFarmlandBlock || world.getBlockState(pos.below()).getBlock() instanceof WeedyFarmlandBlock;
	}

	/**
	 * Seeds a random patch within 5 blocks (and one up or down): farmland, bare or under a crop, or (one time in
	 * ten) grass onto a grass block.
	 */
	public static void weedSpread(Level world, BlockPos pos, RandomSource rand) {
		List<BlockPos> farmland = new ArrayList<BlockPos>();
		List<BlockPos> grass = new ArrayList<BlockPos>();
		for(BlockPos p : BlockPos.betweenClosed(pos.offset(-5,-1,-5), pos.offset(5,1,5))) {
			BlockState s = world.getBlockState(p);
			if (s.is(Blocks.FARMLAND)) {
				if (canPlantWeeds(world, p)) farmland.add(p.immutable());
			}
			else if (s.is(Blocks.GRASS_BLOCK) && world.getBlockState(p.above()).isAir() && rand.nextInt(10) == 0) {
				grass.add(p.above());
			}
		}
		int n = farmland.size() + grass.size();
		if(n == 0) return;
		int i = rand.nextInt(n);
		if(i < farmland.size())
			plantWeeds(world, farmland.get(i));
		else
			world.setBlock(grass.get(i - farmland.size()), Blocks.SHORT_GRASS.defaultBlockState(), 3);
	}

	/**
	 * Open farmland or farmland under a plant (something you can walk through that stays put). A carpet, or
	 * anything else with a collision box, keeps weeds out.
	 */
	private static boolean canPlantWeeds(Level world, BlockPos farmland) {
		BlockPos up = farmland.above();
		BlockState above = world.getBlockState(up);
		return above.isAir() || (above.getCollisionShape(world, up).isEmpty() && !above.canBeReplaced() && above.getFluidState().isEmpty());
	}
}
