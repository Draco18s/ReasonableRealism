package com.draco18s.harderores.block.ore;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.block.state.BlockProperties;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class HardOreBlock extends Block {
	public final int metaChange;

	public HardOreBlock(int metaDecrement, Properties properties) {
		super(properties);
		metaChange = metaDecrement;
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockProperties.ORE_DENSITY, 16));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockProperties.ORE_DENSITY);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Deprecated
	public boolean isSolid(BlockState state) {
		return true;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		for(int i = 1; i <= 16; i++) {
			ItemStack it = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.getRegistryName().getNamespace(),this.getRegistryName().getPath()+"_"+i)));
			//ItemStack it = new ItemStack(this);
			it.getOrCreateTag().putInt("harderores:density", Math.max(0, i));
			if(!items.contains(it))
				items.add(it);
		}
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		int density = state.get(BlockProperties.ORE_DENSITY);
		ItemStack it = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.getRegistryName().getNamespace(),this.getRegistryName().getPath()+"_"+density)));
		it.getOrCreateTag().putInt("harderores:density", Math.max(0, density));
		return it;
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(BlockProperties.ORE_DENSITY, context.getItem().getOrCreateTag().getInt("harderores:density"));
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		//super.harvestBlock(worldIn, player, pos, state, te, stack);
		player.addStat(Stats.BLOCK_MINED.get(this));
		player.addExhaustion(0.005F);
		if (worldIn instanceof ServerWorld) {
			getDrops(state, (ServerWorld)worldIn, pos, te, player, stack).forEach((p_220057_2_) -> {
				tspawnAsEntity(worldIn, pos, p_220057_2_);
			});
		}
		if (/*this.canSilkHarvest(worldIn, pos, state, player)*/ worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			worldIn.removeBlock(pos, false);
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid)
	{
		if(player != null && player.abilities.isCreativeMode) {
			world.removeBlock(pos, false);
			return true;
		}
		if(willHarvest) {
			this.onBlockHarvested(world, pos, state, player);
			int m = state.get(BlockProperties.ORE_DENSITY);
			m -= metaChange;
			if(m < 1)
				return world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);

			world.setBlockState(pos, state.with(BlockProperties.ORE_DENSITY, m), 3);
			ItemStack itemstack1 = player.getHeldItemMainhand();
			ItemStack itemstack2 = itemstack1 == null ? null : itemstack1.copy();
			this.harvestBlock(world, player, pos, state, null, itemstack2);
			m -= metaChange;
			return false;
		}
		return true;
	}

	@Override
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
	}
}
