package com.draco18s.harderores.block;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.SifterBlockEntity;
import com.draco18s.hardlib.api.internal.block.ModEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class SifterBlock extends ModEntityBlock {

	public SifterBlock() {
		super(Properties.of(Material.WOOD).strength(2).sound(SoundType.WOOD));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SifterBlockEntity(pos,state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> betType) {
		return level.isClientSide ? null : createTickerHelper(betType, HarderOres.ModBlockEntities.machine_sifter, SifterBlockEntity::tick);
	}
}
