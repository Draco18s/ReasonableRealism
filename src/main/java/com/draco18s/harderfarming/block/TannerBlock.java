package com.draco18s.harderfarming.block;

import javax.annotation.Nullable;

import com.draco18s.harderfarming.entity.TannerTileEntity;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.farming.LeatherStatus;
import com.draco18s.hardlib.util.InventoryUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.CapabilityItemHandler;

public class TannerBlock extends Block {
	public static final VoxelShape TANNER_AABB_SOUTH = makeCuboidShape(0.0D*16, 0.0D*16, 0.2D*16, 1.0D*16, 1.0D*16, 0.8D*16);
	public static final VoxelShape TANNER_AABB_WEST = makeCuboidShape(0.2D*16, 0.0D*16, 0.0D*16, 0.8D*16, 1.0D*16, 1.0D*16);

	public TannerBlock() {
		super(Properties.create(Material.WOOD).hardnessAndResistance(2, 0).sound(SoundType.WOOD).harvestTool(ToolType.AXE).harvestLevel(0));
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockProperties.LEFT_LEATHER_STATE, LeatherStatus.NONE).with(BlockProperties.RIGHT_LEATHER_STATE, LeatherStatus.NONE).with(BlockProperties.SALT_LEVEL, 0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
		builder.add(BlockProperties.LEFT_LEATHER_STATE);
		builder.add(BlockProperties.RIGHT_LEATHER_STATE);
		builder.add(BlockProperties.SALT_LEVEL);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction face = context.getPlacementHorizontalFacing();
		if(face == Direction.NORTH || face == Direction.EAST) face = face.getOpposite();
		return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, face);
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if(state.get(BlockStateProperties.HORIZONTAL_FACING) == Direction.SOUTH) {
			return TANNER_AABB_SOUTH;
		}
		return TANNER_AABB_WEST;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TannerTileEntity();
	}

	@Override
	@Deprecated
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TannerTileEntity te = (TannerTileEntity)world.getTileEntity(pos);
		ItemStack heldItem = player.getHeldItem(hand);
		te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, hit.getFace()).ifPresent(inven -> {
			if(inven.getStackInSlot(0).isEmpty() && !heldItem.isEmpty()) {
				ItemStack remain = inven.insertItem(0, heldItem.copy(), false);
				if(hit.getFace() == Direction.UP && (remain.isEmpty() || remain.getCount() < heldItem.getCount())) {
					//TODO: advancements
					//player.addStat(FarmingAchievements.saltedHide, 1);
				}
				if(remain.isEmpty()) {
					heldItem.split(64);
				}
				else {
					heldItem.setCount(remain.getCount());
				}
			}
			else {
				if((heldItem.isEmpty() || heldItem.getItem() == Items.LEATHER) && hit.getFace() != Direction.UP) {
					ItemStack item = inven.extractItem(0, 1, false);
					player.inventory.addItemStackToInventory(item);

					if(!item.isEmpty() && item.getItem() == Items.LEATHER) {
						//TODO: advancements
						//player.addStat(FarmingAchievements.getLeather, 1);
						//player.addStat(AchievementList.KILL_COW, 1);
					}
				}
			}
		});
		te.markDirty();
		te.setBlockToUpdate();
		//world.markAndNotifyBlock(pos, chunk, blockstate, newState, flags);
		//world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		return true;
	}
	
	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileEntity = world.getTileEntity(pos);
			InventoryUtils.dropItemHandlerContents(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null), world, pos);
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}
}
