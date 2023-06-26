package com.draco18s.harderores.block;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.MillstoneBlockEntity;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.api.internal.block.ModEntityBlock;
import com.draco18s.hardlib.util.InventoryUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

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

	public InteractionResult use(BlockState p_48804_, Level p_48805_, BlockPos p_48806_, Player p_48807_, InteractionHand p_48808_, BlockHitResult p_48809_) {
		return InteractionResult.CONSUME;
	}
	
	//updatePostPlacement
	public BlockState updateShape(BlockState thisCurState, Direction dir, BlockState state2, LevelAccessor world, BlockPos thisPos, BlockPos pos2) {
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
	
	public boolean checkPlacement(LevelAccessor iworld, BlockPos pos) {
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
	
	//onReplaced
	@Deprecated
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockentity = world.getBlockEntity(pos);
			InventoryUtils.dropItemHandlerContents(blockentity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null), world, pos);
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}
}
