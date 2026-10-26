package com.draco18s.harderores.entity;

import java.util.List;

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
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Pressure packager: packs items (9 ingots into a block, ice into packed ice...) using mechanical power from a
 * windvane hub, reached up a column of axels like the millstone's. Harder inputs and outputs take longer.
 */
public class PackagerBlockEntity extends ModContainerBlockEntity {
	private static final int PACK_TIME = 100;
	protected final ItemStacksResourceHandler inputSlots = new PackableItemsHandler();
	protected final ItemStacksResourceHandler outputSlots = new ItemStacksResourceHandler(1);
	protected final OutputItemStackHandler outputHandler = new OutputItemStackHandler(outputSlots);
	protected final RawMechanicalPowerHandler powerUser = new PackagerMechanicalPowerHandler();

	public final ResourceHandler<ItemResource> input = inputSlots;
	public final ResourceHandler<ItemResource> output = outputHandler;
	public final ResourceHandler<ItemResource> all = new CombinedResourceHandler<>(inputSlots,outputHandler);

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
		ItemStack in = ItemUtil.getStack(inputSlots, slot);
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
		for(int s = 0; s < inputSlots.size(); s++) {
			if(canPack(s)) return s;
		}
		return -1;
	}

	private boolean canPack(int slot) {
		ItemStack stack = ItemUtil.getStack(inputSlots, slot);
		if(stack.isEmpty()) return false;
		ItemStack result = HardLibAPI.oreMachines.getPressurePackResult(stack, true);
		return !result.isEmpty() && ItemUtil.insertItemReturnRemaining(outputSlots, 0, result, true, null).isEmpty();
	}

	private void packItem(int slot) {
		if(!canPack(slot)) return;
		ItemStack stack = ItemUtil.getStack(inputSlots, slot);
		ItemStack result = HardLibAPI.oreMachines.getPressurePackResult(stack, true);
		try (Transaction tx = Transaction.openRoot()) {
			inputSlots.extract(slot, ItemResource.of(stack), HardLibAPI.oreMachines.getPressurePackAmount(stack), tx);
			outputSlots.insert(0, ItemResource.of(result), result.getCount(), tx);
			tx.commit();
		}
	}

	/** Power from the windvane hub at the top of the packager's axels. */
	private float calcAndGetPower() {
		if(level.isClientSide()) return powerUser.getScaledPower(powerUser.getRawPower());
		IMechanicalPower pow = AxelNetwork.findPowerSource(level, worldPosition);
		if(pow == null) return 0;
		powerUser.setRawPower(pow.getRawPower());
		return Math.max(powerUser.getScaledPower(powerUser.getRawPower()),0);
	}

	@Override
	public ResourceHandler<ItemResource> getItemHandler(@Nullable Direction side) {
		if(side == null || level == null || level.getBlockState(worldPosition).getBlock() != getBlockState().getBlock()) {
			return all;
		}
		switch(side) {
			case UP:
				return input;
			case DOWN:
				return output;
			default:
				return null;
		}
	}

	@Override
	protected List<ItemStacksResourceHandler> getInventories() {
		return List.of(inputSlots, outputSlots);
	}


	@Override
	protected void modSave(ValueOutput output) {
		powerUser.serialize(output.child("harderores:power"));
		inputSlots.serialize(output.child("harderores:inputSlot"));
		outputSlots.serialize(output.child("harderores:outputSlot"));
		output.putFloat("harderores:packTime", packTime);
		output.putFloat("harderores:timeMod", timeMod);
	}

	@Override
	protected void modLoad(ValueInput input) {
		powerUser.deserialize(input.childOrEmpty("harderores:power"));
		inputSlots.deserialize(input.childOrEmpty("harderores:inputSlot"));
		outputSlots.deserialize(input.childOrEmpty("harderores:outputSlot"));
		packTime = input.getFloatOr("harderores:packTime", 0);
		timeMod = Math.max(input.getFloatOr("harderores:timeMod", 0), 1);
	}
}
