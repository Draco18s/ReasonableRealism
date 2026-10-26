package com.draco18s.harderores.entity;

import org.jetbrains.annotations.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.capability.MillableItemsHandler;
import com.draco18s.harderores.entity.capability.MillstoneMechanicalPowerHandler;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;
import com.draco18s.hardlib.api.interfaces.IMechanicalPower;
import com.draco18s.hardlib.api.internal.inventory.ModBlockEntity;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;
import com.draco18s.hardlib.api.recipe.FakeContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

public class MillstoneBlockEntity extends ModBlockEntity implements FakeContainer {
	protected final ItemStackHandler inputSlots = new MillableItemsHandler(1);
	protected final ItemStackHandler outputSlots = new ItemStackHandler(1);
	protected final ItemStackHandler outputHandler = new OutputItemStackHandler(outputSlots);
	protected RawMechanicalPowerHandler powerUser = new MillstoneMechanicalPowerHandler();

	public final IItemHandler input = inputSlots;
	public final IItemHandler output = outputHandler;
	public final IItemHandler all = new CombinedInvWrapper(inputSlots,outputHandler);
	
	protected float grindTime = 0;
	
	public MillstoneBlockEntity(BlockPos pos, BlockState state) {
		super(HarderOres.ModBlockEntities.machine_millstone, pos, state);
	}

	/** The item handler capability: depends on the part of the mill this is (see HarderOres' capability registration). */
	@Nullable
	public IItemHandler getItemHandler(@Nullable Direction side) {
		if(level == null || level.getBlockState(worldPosition).getBlock() != HarderOres.ModBlocks.machine_millstone) return all;
		MillstoneOrientation millpos = level.getBlockState(worldPosition).getValue(BlockProperties.MILL_ORIENTATION);
		if(side == null) {
			return all;
		}
		switch(side) {
			case UP:
				return millpos.canAcceptInput ? input : null;
			case DOWN:
				return millpos.canAcceptOutput ? output : null;
			default:
				return millpos == MillstoneOrientation.CENTER ? input : null;
		}
	}

	/** Only the center of the mill takes power. */
	@Nullable
	public IMechanicalPower getMechanicalPower(@Nullable Direction side) {
		if(level == null || level.getBlockState(worldPosition).getBlock() != HarderOres.ModBlocks.machine_millstone) return null;
		return level.getBlockState(worldPosition).getValue(BlockProperties.MILL_ORIENTATION) == MillstoneOrientation.CENTER ? powerUser : null;
	}

	@Override
	public ItemStack getItem(int slot) {
		return inputSlots.getStackInSlot(slot);
	}

	public static void tick(Level world, BlockPos pos, BlockState state, MillstoneBlockEntity millstone) {
		MillstoneOrientation millpos = world.getBlockState(pos).getValue(BlockProperties.MILL_ORIENTATION);
		if(millpos == MillstoneOrientation.CENTER) {
			if(millstone.grindTime > 0) {
				float pow = millstone.calcAndGetPower();
				millstone.grindTime -= pow;
				if(millstone.inputSlots.getStackInSlot(0).isEmpty()) {
					millstone.grindTime = 0;
				}
				else if (millstone.grindTime <= 0) {
					millstone.grindItem(millpos);
					
					millstone.ejectOrInsert();
				}
			}
			else if (millstone.canGrind(millpos)) {
				millstone.grindTime = 400;
				//TODO
				//HarderOres.proxy.startMillSound(this);
			}
		}
		else {
			if(world.getBlockState(millstone.worldPosition.offset(millpos.offset.getX(), 0, millpos.offset.getZ())).getBlock() != millstone.getBlockState().getBlock()) {
				//world.scheduleBlockUpdate(pos, blockType, 1, 10);
				return;
			}
			else if(millstone.inputSlots.getStackInSlot(0).isEmpty()) return;
			IItemHandler inven = world.getCapability(Capabilities.ItemHandler.BLOCK, millstone.worldPosition.offset(millpos.offset.getX(), 0, millpos.offset.getZ()), Direction.EAST);
			if(inven == null) return;
			ItemStack stack = millstone.inputSlots.getStackInSlot(0).copy();
			stack.setCount(1);
			if(! inven.insertItem(0, stack, true).isEmpty()) return;
			millstone.inputSlots.insertItem(0,
				inven.insertItem(0, 
						millstone.inputSlots.extractItem(0, Integer.MAX_VALUE, false)
						, false)
				, false);
		}
	}
	
	private void ejectOrInsert() {
		if(!outputSlots.getStackInSlot(0).isEmpty() && (outputSlots.getStackInSlot(0).getCount() >= 8 || inputSlots.getStackInSlot(0).isEmpty())) {
			if(!level.isClientSide) {
				BlockState s = level.getBlockState(worldPosition.below());
				if(s.isAir()) {
					RandomSource rand = level.random;
					float rx = rand.nextFloat() * 0.6F + 0.2F;
					float ry = rand.nextFloat() * 0.2F + 0.6F - 1;
					float rz = rand.nextFloat() * 0.6F + 0.2F;
					ItemEntity itemEntity = new ItemEntity(level,
							worldPosition.getX() + rx, worldPosition.getY() + ry, worldPosition.getZ() + rz,
							outputSlots.extractItem(0, 64, false));
					level.addFreshEntity(itemEntity);
					itemEntity.setDeltaMovement(0, -0.2F, 0);
				}
			}
		}
	}

	private void grindItem(MillstoneOrientation millpos) {
		if (canGrind(millpos)) {
			ItemStack result = HardLibAPI.oreMachines.getMillResult(inputSlots.getStackInSlot(0)).copy();
			inputSlots.extractItem(0, 1, false);
			outputSlots.insertItem(0, result, false);
		}
	}

	public float getPower() {
		return calcAndGetPower();
	}

	private float calcAndGetPower() {
		if(level.isClientSide) return powerUser.getScaledPower(powerUser.getRawPower());
		IMechanicalPower pow = AxelNetwork.findPowerSource(level, worldPosition);
		if(pow == null) return 0;
		powerUser.setRawPower(pow.getRawPower());
		return Math.max(powerUser.getScaledPower(powerUser.getRawPower()),0);
	}
	
	public boolean canGrind(MillstoneOrientation millpos) {
		if(millpos != MillstoneOrientation.CENTER || inputSlots.getStackInSlot(0).isEmpty()) return false;
		ItemStack result = HardLibAPI.oreMachines.getMillResult(inputSlots.getStackInSlot(0));
		if(result.isEmpty()) return false;

		return true;
	}

	public float getGrindTime() {
		return grindTime;
	}

	@Override
	protected void modSave(CompoundTag tag, HolderLookup.Provider registries) {
		tag.put("harderores:power", powerUser.serializeNBT(registries));
		tag.put("harderores:inputslot", inputSlots.serializeNBT(registries));
		tag.put("harderores:outputSlot", outputSlots.serializeNBT(registries));
		tag.putFloat("harderores:grindTime", grindTime);
	}


	@Override
	protected void modLoad(CompoundTag tag, HolderLookup.Provider registries) {
		powerUser.deserializeNBT(registries, tag.getCompound("harderores:power"));
		inputSlots.deserializeNBT(registries, tag.getCompound("harderores:inputslot"));
		outputSlots.deserializeNBT(registries, tag.getCompound("harderores:outputSlot"));
		grindTime = tag.getFloat("harderores:grindTime");
	}
}
