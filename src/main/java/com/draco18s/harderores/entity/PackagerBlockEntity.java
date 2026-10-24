package com.draco18s.harderores.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.capability.PackableItemsHandler;
import com.draco18s.harderores.entity.capability.PackagerMechanicalPowerHandler;
import com.draco18s.harderores.inventory.PackagerContainerMenu;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;
import com.draco18s.hardlib.api.interfaces.IMechanicalPower;
import com.draco18s.hardlib.api.internal.inventory.ModContainerBlockEntity;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

/**
 * Pressure packager: packs items (9 ingots into a block, ice into packed ice...) using mechanical power from a
 * windvane hub, reached up a column of axels like the millstone's. Harder inputs and outputs take longer.
 */
public class PackagerBlockEntity extends ModContainerBlockEntity {
	private static final int PACK_TIME = 100;
	protected final ItemStackHandler inputSlots = new PackableItemsHandler();
	protected final ItemStackHandler outputSlots = new ItemStackHandler(1);
	protected final ItemStackHandler outputHandler = new OutputItemStackHandler(outputSlots);
	protected final RawMechanicalPowerHandler powerUser = new PackagerMechanicalPowerHandler();

	public LazyOptional<IItemHandler> input = LazyOptional.of(() -> inputSlots);
	public LazyOptional<IItemHandler> output = LazyOptional.of(() -> outputHandler);
	public LazyOptional<IItemHandler> all = LazyOptional.of(() -> new CombinedInvWrapper(inputSlots,outputHandler));

	private float packTime;
	private float timeMod = 1;
	private int activeSlot = -1;

	protected final ContainerData dataAccess = new ContainerData() {
		public int get(int index) {
			return index == 0 ? Math.round(packTime) : 0;
		}

		public void set(int index, int value) {
			if(index == 0) packTime = value;
		}

		public int getCount() {
			return 1;
		}
	};

	public PackagerBlockEntity(BlockPos pos, BlockState state) {
		super(HarderOres.ModBlockEntities.packager, pos, state);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.harderores.packager");
	}

	@Override
	protected AbstractContainerMenu createMenu(int windowID, Inventory inventory) {
		return new PackagerContainerMenu(windowID, inventory, inputSlots, outputHandler, this, dataAccess);
	}

	public static void tick(Level world, BlockPos pos, BlockState state, PackagerBlockEntity packager) {
		if(world.getBlockState(pos).getBlock() != packager.getBlockState().getBlock()) return;
		if(packager.packTime > 0) {
			packager.packTime -= packager.calcAndGetPower() / packager.timeMod;
			int slot = packager.firstPackableSlot();
			if(slot < 0) {
				packager.packTime = 0;
				packager.activeSlot = -1;
			}
			else if(slot != packager.activeSlot) {
				packager.startPacking(slot);
			}
			else if(packager.packTime <= 0) {
				packager.packItem(slot);
				packager.packTime = 0;
			}
			packager.setChanged();
		}
		else {
			int slot = packager.firstPackableSlot();
			packager.activeSlot = -1;
			if(slot >= 0) {
				packager.startPacking(slot);
				packager.setChanged();
			}
		}
	}

	private void startPacking(int slot) {
		activeSlot = slot;
		packTime = PACK_TIME;
		ItemStack in = inputSlots.getStackInSlot(slot);
		ItemStack out = HardLibAPI.oreMachines.getPressurePackResult(in, true);
		timeMod = Math.max(Math.max(inputHardness(in), outputHardness(out)), 1);
	}

	/** Packing stone-like blocks is slow, more so the better the pick they need. */
	private float inputHardness(ItemStack stack) {
		if(!(stack.getItem() instanceof BlockItem item)) return 1;
		BlockState state = item.getBlock().defaultBlockState();
		float mod = state.getDestroySpeed(level, worldPosition) * 2;
		if(state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
			int tier = state.is(BlockTags.NEEDS_DIAMOND_TOOL) ? 3 : state.is(BlockTags.NEEDS_IRON_TOOL) ? 2 : state.is(BlockTags.NEEDS_STONE_TOOL) ? 1 : 0;
			mod *= tier + 2;
		}
		return mod;
	}

	/** And packing something into a hard block is slow, doubly so for dirt-like (shovel) results. */
	private float outputHardness(ItemStack stack) {
		if(!(stack.getItem() instanceof BlockItem item)) return 1;
		BlockState state = item.getBlock().defaultBlockState();
		float mod = state.getDestroySpeed(level, worldPosition);
		if(state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
			mod *= 2;
		}
		return mod;
	}

	private int firstPackableSlot() {
		for(int s = 0; s < inputSlots.getSlots(); s++) {
			if(canPack(s)) return s;
		}
		return -1;
	}

	private boolean canPack(int slot) {
		ItemStack stack = inputSlots.getStackInSlot(slot);
		if(stack.isEmpty()) return false;
		ItemStack result = HardLibAPI.oreMachines.getPressurePackResult(stack, true);
		return !result.isEmpty() && outputSlots.insertItem(0, result, true).isEmpty();
	}

	private void packItem(int slot) {
		if(!canPack(slot)) return;
		ItemStack stack = inputSlots.getStackInSlot(slot);
		ItemStack result = HardLibAPI.oreMachines.getPressurePackResult(stack, true);
		inputSlots.extractItem(slot, HardLibAPI.oreMachines.getPressurePackAmount(stack), false);
		outputSlots.insertItem(0, result.copy(), false);
	}

	/** Power from the windvane hub at the top of the packager's axels. */
	private float calcAndGetPower() {
		if(level.isClientSide) return powerUser.getScaledPower(powerUser.getRawPower());
		IMechanicalPower pow = AxelNetwork.findPowerSource(level, worldPosition);
		if(pow == null) return 0;
		powerUser.setRawPower(pow.getRawPower());
		return Math.max(powerUser.getScaledPower(powerUser.getRawPower()),0);
	}

	@SuppressWarnings("unchecked")
	@NonNull
	@Override
	protected <T> LazyOptional<T> getInventoryCapability(Capability<T> cap, @Nullable Direction side) {
		if(side == null || level == null || level.getBlockState(worldPosition).getBlock() != getBlockState().getBlock()) {
			return (LazyOptional<T>) all;
		}
		switch(side) {
			case UP:
				return (LazyOptional<T>) input;
			case DOWN:
				return (LazyOptional<T>) output;
			default:
				return LazyOptional.empty();
		}
	}

	@NonNull
	@Override
	protected IItemHandlerModifiable getInventory(@Nullable Direction direction) {
		if(direction == null) {
			return new CombinedInvWrapper(inputSlots,outputSlots);
		}
		switch(direction) {
			case UP:
				return inputSlots;
			case DOWN:
				return outputHandler;
			default:
				return EMPTY_HANDLER;
		}
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		input.invalidate();
		output.invalidate();
		all.invalidate();
	}

	@Override
	protected void modSave(CompoundTag nbt, HolderLookup.Provider registries) {
		nbt.put("harderores:power", powerUser.serializeNBT(registries));
		nbt.put("harderores:inputSlot", inputSlots.serializeNBT(registries));
		nbt.put("harderores:outputSlot", outputSlots.serializeNBT(registries));
		nbt.putFloat("harderores:packTime", packTime);
		nbt.putFloat("harderores:timeMod", timeMod);
	}

	@Override
	protected void modLoad(CompoundTag nbt, HolderLookup.Provider registries) {
		powerUser.deserializeNBT(registries, nbt.getCompound("harderores:power"));
		inputSlots.deserializeNBT(registries, nbt.getCompound("harderores:inputSlot"));
		outputSlots.deserializeNBT(registries, nbt.getCompound("harderores:outputSlot"));
		packTime = nbt.getFloat("harderores:packTime");
		timeMod = Math.max(nbt.getFloat("harderores:timeMod"), 1);
	}
}
