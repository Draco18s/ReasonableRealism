package com.draco18s.harderores.block;

import com.draco18s.harderores.entity.SifterTileEntity;
import com.draco18s.hardlib.util.InventoryUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.CapabilityItemHandler;

public class SifterBlock extends Block {

	public SifterBlock() {
		super(Block.Properties.create(Material.WOOD).hardnessAndResistance(2).harvestTool(ToolType.AXE).harvestLevel(1).sound(SoundType.WOOD));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SifterTileEntity();
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!world.isRemote) {
			final SifterTileEntity tileEntity = (SifterTileEntity)world.getTileEntity(pos);
			if (tileEntity != null) {
				tileEntity.openGUI((ServerPlayerEntity) player);
			}
		}

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