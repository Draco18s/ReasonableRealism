package com.draco18s.harderfarming.block;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

import com.draco18s.harderfarming.HarderFarming;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.item.ToolItem;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class CropWeedsBlock extends CropsBlock {

	public CropWeedsBlock() {
		super(Properties.create(Material.PLANTS).tickRandomly().hardnessAndResistance(2, 0).doesNotBlockMovement().sound(SoundType.CROP));
		this.setDefaultState(this.stateContainer.getBaseState().with(getAgeProperty(), 0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(getAgeProperty());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		int i = state.get(this.getAgeProperty()) < 7 ? state.get(this.getAgeProperty()) : 7;
		return super.getShape(state.with(getAgeProperty(), i), worldIn, pos, context);
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return BlockStateProperties.AGE_0_15;
	}

	@Override
	public int getMaxAge() {
		return 15;
	}

	@Override
	protected IItemProvider getSeedsItem() {
		return HarderFarming.ModItems.weed_seeds;
	}

	@Override
	public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
		super.tick(state, worldIn, pos, random);
		if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		if (worldIn.getLightSubtracted(pos, 0) >= 9 && random.nextBoolean()) {
			int i = this.getAge(state);
			if (i < this.getMaxAge() && i != 7) {
				worldIn.setBlockState(pos, this.withAge(i + 1), 2);
			} else {
				weedSpread(worldIn, pos, random);
				worldIn.setBlockState(pos, state.with(getAgeProperty(), 7));
				if(i == 7) {
					worldIn.setBlockState(pos, this.withAge(i + 1), 2);
				}
			}
		}
	}

	public static void weedSpread(World world, BlockPos pos, Random rand) {
		Stream<BlockPos> list = BlockPos.getAllInBox(pos.add(-5,0,-5), pos.add(5,0,5));
		ArrayList<BlockPos> validPlacements = new ArrayList<BlockPos>();
		list.forEach(p -> {
			BlockPos dwn = p.down();
			if (world.getBlockState(dwn.down()).getBlock() == Blocks.FARMLAND) {
				if (canPlantWeeds(world, dwn, rand)) {
					validPlacements.add(dwn);
				}
			} else if (world.getBlockState(dwn).getBlock() == Blocks.FARMLAND) {
				if (canPlantWeeds(world, dwn.up(), rand)) {
					validPlacements.add(dwn.up());
				}
			} else if (world.getBlockState(dwn.up()).getBlock() == Blocks.FARMLAND) {
				if (canPlantWeeds(world, dwn.up(2), rand)) {
					validPlacements.add(dwn.up(2));
				}
			}
			else if(world.getBlockState(dwn.down()).getBlock() == Blocks.GRASS_BLOCK && rand.nextInt(10) == 0) {
				if(world.getBlockState(dwn).canBeReplacedByLeaves(world, pos))
					validPlacements.add(dwn);
			}
			else if(world.getBlockState(dwn).getBlock() == Blocks.GRASS_BLOCK && rand.nextInt(10) == 0) {
				if(world.getBlockState(dwn.up()).canBeReplacedByLeaves(world, pos))
					validPlacements.add(dwn.up());
			}
			else if(world.getBlockState(dwn.up()).getBlock() == Blocks.GRASS_BLOCK && rand.nextInt(10) == 0) {
				if(world.getBlockState(dwn.up(2)).canBeReplacedByLeaves(world, pos))
					validPlacements.add(dwn.up(2));
			}
		});
		for(int i = 0; i < 1 && validPlacements.size() > 0; i++) {
			int r = rand.nextInt(validPlacements.size());
			BlockPos p = validPlacements.get(r);
			if(world.getBlockState(p).getBlock() instanceof CarpetBlock) {
				if(rand.nextInt(4) != 0)
					continue;
				world.destroyBlock(p, true);
			}
			if(world.getBlockState(p.down()).getBlock() == Blocks.FARMLAND)
				world.setBlockState(p, HarderFarming.ModBlocks.crop_weeds.getDefaultState(), 3);
			else
				world.setBlockState(p, Blocks.GRASS.getDefaultState(), 3);
			validPlacements.remove(r);
		}
	}

	private static boolean canPlantWeeds(World world, BlockPos pos, Random rand) {
		BlockState growing = world.getBlockState(pos);
		if (growing.getMaterial() == Material.AIR) {
			return true;
		}
		if (growing.getBlock() instanceof CarpetBlock) {
			return true;
		}
		return false;
	}

	@Override
	@Deprecated
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		float hardness = state.getBlockHardness(worldIn, pos);

		ItemStack s = player.getHeldItemMainhand();

		IItemTier mat;
		if(s != null && s.getItem() instanceof TieredItem) {
			mat = ((TieredItem)s.getItem()).getTier();
		}
		else {
			return 1f / 90;
		}
		hardness += mat.getEfficiency();
		int i = EnchantmentHelper.getEfficiencyModifier(player);

		if (i > 0 && s != null)
		{
			float f1 = (float)(i * i + 1);

			boolean canHarvest = (worldIn instanceof IWorldReader) ? ForgeHooks.canToolHarvestBlock((IWorldReader) worldIn, pos, s) : false;

			if (!canHarvest && hardness <= 1.0F)
			{
				hardness += f1 * 0.08F;
			}
			else
			{
				hardness += f1;
			}
		}
		return hardness / this.blockHardness / 45;
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
		ItemStack stack = player.getHeldItemMainhand();
		if(stack != null) {
			Item i = stack.getItem();
			if(i instanceof ToolItem) {
				//stack.damageItem(2, player);
				stack.damageItem(2, player, (_player) -> {
					_player.sendBreakAnimation(Hand.MAIN_HAND);
				});
			}
			else if(i instanceof HoeItem) {
				//stack.damageItem(1, player);
				stack.damageItem(1, player, (_player) -> {
					_player.sendBreakAnimation(Hand.MAIN_HAND);
				});
			}
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}
}
