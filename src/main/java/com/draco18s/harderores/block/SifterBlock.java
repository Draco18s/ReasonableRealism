package com.draco18s.harderores.block;

import com.draco18s.harderores.entity.SifterTileEntity;

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

	/*@Override
	public boolean removedByPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		if(!worldIn.isRemote) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			IItemHandler inventory = worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
			for (int i = 0; i < inventory.getSlots(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				ItemEntity entityIn;
				if (stack != null) {
					entityIn = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
					entityIn.setDefaultPickupDelay();
					worldIn.spawnEntity(entityIn);
				}
			}
		}
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
	}*/
}