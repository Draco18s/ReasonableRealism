package com.draco18s.harderores.block;

import java.util.List;
import java.util.stream.Collectors;

import com.draco18s.harderores.entity.MillstoneTileEntity;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.util.InventoryUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class MillstoneBlock extends Block {

	public MillstoneBlock() {
		super(Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(2).harvestTool(ToolType.PICKAXE).harvestLevel(1).sound(SoundType.STONE));
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockProperties.MILL_ORIENTATION, MillstoneOrientation.NONE));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockProperties.MILL_ORIENTATION);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new MillstoneTileEntity();
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack heldItem = player.getHeldItem(hand);
		if(!heldItem.isEmpty()) {
			MillstoneTileEntity te = (MillstoneTileEntity)world.getTileEntity(pos);
			IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
			if(inventory == null) return false;
			ItemStack stack = heldItem.copy();
			stack.setCount(1);
			stack = inventory.insertItem(0, stack, true);
			if(stack.isEmpty()) {
				stack = inventory.insertItem(0, heldItem.split(1), false);

				MillstoneOrientation millpos = world.getBlockState(pos).get(BlockProperties.MILL_ORIENTATION);
				MillstoneTileEntity center = (MillstoneTileEntity)world.getTileEntity(te.getPos().add(millpos.offset.getX(), 0, millpos.offset.getZ()));

				if(center != null && center.getPower() > 0) {
					if(player instanceof ServerPlayerEntity) {
						HardLibAPI.Advancements.MILL_BUILT.trigger((ServerPlayerEntity) player, center.getPower());
					}
				}
				return true;
			}
		}
		return false;
	}

	@Deprecated
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		if(!checkPlacement(world, pos)) {
			List<BlockPos> list = BlockPos.getAllInBox(pos.add(-1,0,-1), pos.add(1,0,1)).map(BlockPos::toImmutable).collect(Collectors.toList());
			for(BlockPos p : list) {
				if(world.getBlockState(p).getBlock() == this) {
					if(checkPlacement(world, p))
						break;
				}
			}
		}
		return world.getBlockState(pos);
	}

	public boolean checkPlacement(IWorld iworld, BlockPos pos) {
		if(iworld instanceof World) {
			World world = (World)iworld;
			BlockState state;// = this.getDefaultState();

			List<BlockPos> list = BlockPos.getAllInBox(pos.add(-1,0,-1), pos.add(1,0,1)).map(BlockPos::toImmutable).collect(Collectors.toList());
			int count = 0;
			for(BlockPos p : list) {
				state = world.getBlockState(p);
				if(state.getBlock() == this && state.get(BlockProperties.MILL_ORIENTATION) == MillstoneOrientation.NONE) {
					count++;
				}
			}
			if(count == 9) {
				for(BlockPos p : list) {
					Vec3i q = new Vec3i(p.getX(), p.getY(), p.getZ());
					BlockPos off = pos.subtract(q);
					for(MillstoneOrientation orient : MillstoneOrientation.values()) {
						if(orient.offset.getX() == off.getX() && orient.offset.getZ() == off.getZ()) {
							world.setBlockState(p, getDefaultState().with(BlockProperties.MILL_ORIENTATION, orient));
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state,world,pos,block,fromPos,isMoving);
		if(block == this && state.get(BlockProperties.MILL_ORIENTATION) != MillstoneOrientation.NONE && world.getBlockState(fromPos).isAir()) {
			BlockPos offset = state.get(BlockProperties.MILL_ORIENTATION).offset;
			BlockPos centerPos = pos.add(offset.getX(),offset.getY(),offset.getZ());
			if(!checkPlacement(world, centerPos)) {
				List<BlockPos> list = BlockPos.getAllInBox(centerPos.add(-1,0,-1), centerPos.add(1,0,1)).map(BlockPos::toImmutable).collect(Collectors.toList());
				for(BlockPos p : list) {
					if(world.getBlockState(p).getBlock() == this) {
						world.setBlockState(p, getDefaultState(),1|2|16);
					}
				}
			}
		}
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
