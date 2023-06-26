package com.draco18s.hardlib.api.internal.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		modSave(nbt);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		modLoad(nbt);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
	    CompoundTag nbtTag = new CompoundTag();
	    modSave(nbtTag);
	    ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this, (e) -> nbtTag);
	    return packet;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
	    modLoad(pkt.getTag());
	}
	
	protected abstract void modSave(CompoundTag nbt);

	protected abstract void modLoad(CompoundTag nbt);
}
