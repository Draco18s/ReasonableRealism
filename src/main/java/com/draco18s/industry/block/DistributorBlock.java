package com.draco18s.industry.block;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.interfaces.ICustomContainer;
import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.entity.DistributorBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DistributorBlock extends HopperBlock {

	public DistributorBlock() {
		super(BlockBehaviour.Properties.copy(Blocks.HOPPER));
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DistributorBlockEntity(pos,state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> betType) {
		return level.isClientSide ? null : createTickerHelper(betType, ExpandedIndustry.ModTileEntities.machine_distributor, DistributorBlockEntity::tick);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (!world.isClientSide) {
			final ICustomContainer tileEntity = (ICustomContainer)world.getBlockEntity(pos);
			if (tileEntity != null) {
				tileEntity.openGUI((ServerPlayer) player);
			}
		}

		return InteractionResult.CONSUME;
	}
}
