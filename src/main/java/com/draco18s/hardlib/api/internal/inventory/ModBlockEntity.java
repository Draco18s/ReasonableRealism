package com.draco18s.hardlib.api.internal.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class ModBlockEntity extends BlockEntity {

	public ModBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
	}

	public double getLevelX() {
		return (double)worldPosition.getX() + 0.5D;
	}

	public double getLevelY() {
		return (double)worldPosition.getY() + 0.5D;
	}

	public double getLevelZ() {
		return (double)worldPosition.getZ() + 0.5D;
	}
	
	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		modSave(output);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		modLoad(input);
	}

	/** The client gets what is saved; NeoForge's default onDataPacket and handleUpdateTag load it. */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveCustomOnly(registries);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
	    return ClientboundBlockEntityDataPacket.create(this);
	}
	
	protected abstract void modSave(ValueOutput output);

	protected abstract void modLoad(ValueInput input);
}
