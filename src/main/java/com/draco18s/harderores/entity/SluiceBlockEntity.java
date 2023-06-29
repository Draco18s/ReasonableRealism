package com.draco18s.harderores.entity;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.internal.inventory.ModBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class SluiceBlockEntity extends ModBlockEntity {
	protected ItemStackHandler inputSlot = new ItemStackHandler(1);
	protected LazyOptional<ItemStackHandler> inputHandler = LazyOptional.of(() -> inputSlot);

	public SluiceBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
		super(HarderOres.ModBlockEntities.sluice, p_155229_, p_155230_);
	}
	
	public static void tick(Level world, BlockPos pos, BlockState state, SifterBlockEntity sifter) {
		BlockPos p = pos;
		Direction dir = world.getBlockState(p).getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
		do {
			p=p.relative(dir,1);
		} while(world.getBlockState(p).getBlock() == HarderOres.ModBlocks.sluice);
		p = p.relative(dir.getOpposite(), 1);
		//updateWater();
	}

	@Override
	protected void modSave(CompoundTag nbt) {

	}

	@Override
	protected void modLoad(CompoundTag nbt) {

	}
}
