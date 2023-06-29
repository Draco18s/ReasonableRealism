package com.draco18s.harderores.block;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.MillstoneBlockEntity;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.advancement.MillstoneTrigger;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.api.internal.block.ModEntityBlock;
import com.draco18s.hardlib.util.InventoryUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class MillstoneBlock extends ModEntityBlock {

	public MillstoneBlock() {
		super(Properties.of(Material.STONE).strength(2).sound(SoundType.STONE).requiresCorrectToolForDrops());
		registerDefaultState(this.stateDefinition.any().setValue(BlockProperties.MILL_ORIENTATION, MillstoneOrientation.NONE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockProperties.MILL_ORIENTATION);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return new MillstoneBlockEntity(p_153215_, p_153216_);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> betType) {
		return level.isClientSide ? null : createTickerHelper(betType, HarderOres.ModBlockEntities.machine_millstone, MillstoneBlockEntity::tick);
	}

	@Deprecated
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		MillstoneOrientation mill = state.getValue(BlockProperties.MILL_ORIENTATION);
		ItemStack heldItem = player.getItemInHand(hand);
		if(!mill.canAcceptInput || heldItem.isEmpty()) return InteractionResult.PASS;
		BlockEntity be = world.getBlockEntity(pos);
		LazyOptional<IItemHandler> handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
		return handler.map(inventory -> {
			ItemStack stack = heldItem.copy();
			stack.setCount(1);
			stack = inventory.insertItem(0, stack, true);
			if(stack.isEmpty()) {
				inventory.insertItem(0, heldItem.split(1), false);
				
				MillstoneOrientation millpos = world.getBlockState(pos).getValue(BlockProperties.MILL_ORIENTATION);
				MillstoneBlockEntity center = (MillstoneBlockEntity)world.getBlockEntity(be.getBlockPos().offset(millpos.offset.getX(), 0, millpos.offset.getZ()));

				if(center != null && center.getPower() > 0) {
					if(player instanceof ServerPlayer sPlayer) {
						LootContext.Builder bld = new LootContext.Builder(sPlayer.getLevel())
								.withParameter(MillstoneTrigger.POWER, center.getPower());
						LootContext ctx = bld.create(MillstoneTrigger.requiredParams);
						HardLibAPI.Advancements.MILL_BUILT.trigger(sPlayer, ctx);
					}
				}
				
				return InteractionResult.CONSUME;
			}
			return InteractionResult.PASS;
		}).orElse(InteractionResult.PASS);
	}

	/*public static final VoxelShape CORNER_SE = box(0,0,0,10,16,10);
	public static final VoxelShape CORNER_NE = box(0,0,6,10,16,16);
	public static final VoxelShape CORNER_NW = box(6,0,6,16,16,16);
	public static final VoxelShape CORNER_SW = box(6,0,0,16,16,10);
	public static final VoxelShape POST = box(2,2,2,14,14,14);
	
	@Override
	@Deprecated
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
		return POST;
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		switch(state.getValue(BlockProperties.MILL_ORIENTATION)) {
			case SOUTH_EAST:
				return CORNER_SE;
			case NORTH_EAST:
				return CORNER_NE;
			case NORTH_WEST:
				return CORNER_NW;
			case SOUTH_WEST:
				return CORNER_SW;
			default:
				break;
		}
		return super.getShape(state, world, pos, ctx);
	}*/

	@Deprecated
	@Override
	public BlockState updateShape(BlockState thisCurState, Direction dir, BlockState state2, LevelAccessor world, BlockPos thisPos, BlockPos pos2) {
		if(world.getBlockState(thisPos).getBlock() != this) return world.getBlockState(thisPos);
		if(!checkPlacement(world, thisPos)) {
			Iterable<BlockPos> list = BlockPos.betweenClosed(thisPos.offset(-1,0,-1), thisPos.offset(1,0,1));//.map(BlockPos::toImmutable).collect(Collectors.toList());
			for(BlockPos p : list) {
				if(world.getBlockState(p).getBlock() == this) {
					if(checkPlacement(world, p))
						break;
				}
			}
		}
		return world.getBlockState(thisPos);
	}
	
	protected boolean checkPlacement(LevelAccessor iworld, BlockPos pos) {
		if(iworld instanceof Level world) {
			//World world = (World)iworld;
			BlockState state;// = this.getDefaultState();

			Iterable<BlockPos> list = BlockPos.betweenClosed(pos.offset(-1,0,-1), pos.offset(1,0,1));//map(BlockPos::toImmutable).collect(Collectors.toList());
			int count = 0;
			for(BlockPos p : list) {
				state = world.getBlockState(p);
				if(state.getBlock() == this && state.getValue(BlockProperties.MILL_ORIENTATION) == MillstoneOrientation.NONE) {
					count++;
				}
			}
			if(count == 9) {
				for(BlockPos p : list) {
					BlockPos off = pos.subtract(p);
					for(MillstoneOrientation orient : MillstoneOrientation.values()) {
						if(orient.offset.getX() == off.getX() && orient.offset.getZ() == off.getZ()) {
							world.setBlockAndUpdate(p, defaultBlockState().setValue(BlockProperties.MILL_ORIENTATION, orient));
						}
					}
				}
			}
		}
		return false;
	}
	
	@Deprecated
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighbor, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state,world,pos,neighbor,fromPos,isMoving);
		if(neighbor == this && state.getValue(BlockProperties.MILL_ORIENTATION) != MillstoneOrientation.NONE && world.getBlockState(fromPos).isAir()) {
			BlockPos offset = state.getValue(BlockProperties.MILL_ORIENTATION).offset;
			BlockPos centerPos = pos.offset(offset.getX(),offset.getY(),offset.getZ());
			if(!checkPlacement(world, centerPos)) {
				Iterable<BlockPos> list = BlockPos.betweenClosed(centerPos.offset(-1,0,-1), centerPos.offset(1,0,1));//.map(BlockPos::toImmutable).collect(Collectors.toList());
				for(BlockPos p : list) {
					if(world.getBlockState(p).getBlock() == this) {
						world.setBlock(p, defaultBlockState(), 1|2|16);
					}
				}
			}
		}
	}
	
	@Deprecated
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockentity = world.getBlockEntity(pos);
			InventoryUtils.dropItemHandlerContents(blockentity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null), world, pos);
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}
}
