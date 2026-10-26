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
import com.draco18s.hardlib.util.InventoryUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class MillstoneBlockEntity extends ModBlockEntity implements FakeContainer {
	protected final ItemStacksResourceHandler inputSlots = new MillableItemsHandler(1);
	protected final ItemStacksResourceHandler outputSlots = new ItemStacksResourceHandler(1);
	protected final OutputItemStackHandler outputHandler = new OutputItemStackHandler(outputSlots);
	protected RawMechanicalPowerHandler powerUser = new MillstoneMechanicalPowerHandler();

	public final ResourceHandler<ItemResource> input = inputSlots;
	public final ResourceHandler<ItemResource> output = outputHandler;
	public final ResourceHandler<ItemResource> all = new CombinedResourceHandler<>(inputSlots,outputHandler);
	
	protected float grindTime = 0;
	
	public MillstoneBlockEntity(BlockPos pos, BlockState state) {
		super(HarderOres.ModBlockEntities.machine_millstone, pos, state);
	}

	/** The item handler capability: depends on the part of the mill this is (see HarderOres' capability registration). */
	@Nullable
	public ResourceHandler<ItemResource> getItemHandler(@Nullable Direction side) {
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

	/** The block is being removed: drop what's in the mill. */
	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		InventoryUtils.dropItemHandlerContents(all, level, pos);
	}

	/** Only the center of the mill takes power. */
	@Nullable
	public IMechanicalPower getMechanicalPower(@Nullable Direction side) {
		if(level == null || level.getBlockState(worldPosition).getBlock() != HarderOres.ModBlocks.machine_millstone) return null;
		return level.getBlockState(worldPosition).getValue(BlockProperties.MILL_ORIENTATION) == MillstoneOrientation.CENTER ? powerUser : null;
	}

	@Override
	public ItemStack getItem(int slot) {
		return ItemUtil.getStack(inputSlots, slot);
	}

	public static void tick(Level world, BlockPos pos, BlockState state, MillstoneBlockEntity millstone) {
		MillstoneOrientation millpos = world.getBlockState(pos).getValue(BlockProperties.MILL_ORIENTATION);
		if(millpos == MillstoneOrientation.CENTER) {
			if(millstone.grindTime > 0) {
				float pow = millstone.calcAndGetPower();
				millstone.grindTime -= pow;
				if(millstone.inputSlots.getResource(0).isEmpty()) {
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
			else if(millstone.inputSlots.getResource(0).isEmpty()) return;
			ResourceHandler<ItemResource> inven = world.getCapability(Capabilities.Item.BLOCK, millstone.worldPosition.offset(millpos.offset.getX(), 0, millpos.offset.getZ()), Direction.EAST);
			ResourceHandlerUtil.move(millstone.inputSlots, inven, resource -> true, Integer.MAX_VALUE, null);
		}
	}
	
	private void ejectOrInsert() {
		if(!outputSlots.getResource(0).isEmpty() && (outputSlots.getAmountAsInt(0) >= 8 || inputSlots.getResource(0).isEmpty())) {
			if(!level.isClientSide()) {
				BlockState s = level.getBlockState(worldPosition.below());
				if(s.isAir()) {
					RandomSource rand = level.getRandom();
					float rx = rand.nextFloat() * 0.6F + 0.2F;
					float ry = rand.nextFloat() * 0.2F + 0.6F - 1;
					float rz = rand.nextFloat() * 0.6F + 0.2F;
					ItemResource resource = outputSlots.getResource(0);
					int amount;
					try (Transaction tx = Transaction.openRoot()) {
						amount = outputSlots.extract(0, resource, 64, tx);
						tx.commit();
					}
					ItemEntity itemEntity = new ItemEntity(level,
							worldPosition.getX() + rx, worldPosition.getY() + ry, worldPosition.getZ() + rz,
							resource.toStack(amount));
					level.addFreshEntity(itemEntity);
					itemEntity.setDeltaMovement(0, -0.2F, 0);
				}
			}
		}
	}

	private void grindItem(MillstoneOrientation millpos) {
		if (canGrind(millpos)) {
			ItemStack result = HardLibAPI.oreMachines.getMillResult(ItemUtil.getStack(inputSlots, 0));
			// Only grind when the output has room for all of the result (it used to be lost).
			try (Transaction tx = Transaction.openRoot()) {
				if(inputSlots.extract(0, inputSlots.getResource(0), 1, tx) == 1
						&& outputSlots.insert(0, ItemResource.of(result), result.getCount(), tx) == result.getCount()) {
					tx.commit();
				}
			}
		}
	}

	public float getPower() {
		return calcAndGetPower();
	}

	private float calcAndGetPower() {
		if(level.isClientSide()) return powerUser.getScaledPower(powerUser.getRawPower());
		IMechanicalPower pow = AxelNetwork.findPowerSource(level, worldPosition);
		if(pow == null) return 0;
		powerUser.setRawPower(pow.getRawPower());
		return Math.max(powerUser.getScaledPower(powerUser.getRawPower()),0);
	}
	
	public boolean canGrind(MillstoneOrientation millpos) {
		if(millpos != MillstoneOrientation.CENTER || inputSlots.getResource(0).isEmpty()) return false;
		ItemStack result = HardLibAPI.oreMachines.getMillResult(ItemUtil.getStack(inputSlots, 0));
		if(result.isEmpty()) return false;

		return true;
	}

	public float getGrindTime() {
		return grindTime;
	}

	@Override
	protected void modSave(ValueOutput output) {
		powerUser.serialize(output.child("harderores:power"));
		inputSlots.serialize(output.child("harderores:inputslot"));
		outputSlots.serialize(output.child("harderores:outputSlot"));
		output.putFloat("harderores:grindTime", grindTime);
	}


	@Override
	protected void modLoad(ValueInput input) {
		powerUser.deserialize(input.childOrEmpty("harderores:power"));
		inputSlots.deserialize(input.childOrEmpty("harderores:inputslot"));
		outputSlots.deserialize(input.childOrEmpty("harderores:outputSlot"));
		grindTime = input.getFloatOr("harderores:grindTime", 0);
	}
}
