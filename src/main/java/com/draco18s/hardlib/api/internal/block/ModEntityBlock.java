package com.draco18s.hardlib.api.internal.block;

import com.draco18s.hardlib.api.interfaces.ICustomContainer;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class ModEntityBlock extends BaseEntityBlock {

	/** Blocks are only written by codec for vanilla's block type listing; this one has no parameters to write. */
	private final MapCodec<ModEntityBlock> codec = MapCodec.unit(this);

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return codec;
	}

	public ModEntityBlock(Properties p_49224_) {
		super(p_49224_);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState p_49232_) {
		return RenderShape.MODEL;
	}
	
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (!world.isClientSide) {
			BlockEntity be = world.getBlockEntity(pos);
			if(be instanceof ICustomContainer tileEntity) {
				tileEntity.openGUI((ServerPlayer) player);
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}
}
