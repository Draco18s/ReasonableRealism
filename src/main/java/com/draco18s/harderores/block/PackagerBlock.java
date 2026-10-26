package com.draco18s.harderores.block;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.PackagerBlockEntity;
import com.draco18s.hardlib.api.internal.block.ModEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Pressure packager: powered from above by axels, like the millstone. Opens a GUI on use. */
public class PackagerBlock extends ModEntityBlock {

	public PackagerBlock(Properties props) {
		super(props.mapColor(MapColor.METAL).strength(1).requiresCorrectToolForDrops().sound(SoundType.METAL));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PackagerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() ? null : createTickerHelper(type, HarderOres.ModBlockEntities.packager, PackagerBlockEntity::tick);
	}

	// Its contents drop as a container's do (BlockEntity.preRemoveSideEffects).
}
