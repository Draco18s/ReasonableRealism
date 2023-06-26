package com.draco18s.harderores.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;
import com.draco18s.hardlib.api.interfaces.IMechanicalPower;
import com.draco18s.hardlib.api.internal.inventory.ModBlockEntity;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;
import com.draco18s.hardlib.api.recipe.FakeContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class MillstoneBlockEntity extends ModBlockEntity implements FakeContainer {
	protected final ItemStackHandler inputSlots = new ItemStackHandler(1);
	protected final ItemStackHandler outputSlots = new ItemStackHandler(1);
	protected final ItemStackHandler outputHandler = new OutputItemStackHandler(outputSlots);
	protected RawMechanicalPowerHandler powerUser = new MillstoneMechanicalPowerHandler();

	public LazyOptional<IItemHandler> input = LazyOptional.of(() -> inputSlots);
	public LazyOptional<IItemHandler> output = LazyOptional.of(() -> outputHandler);
	public LazyOptional<IItemHandler> all = LazyOptional.of(() -> new CombinedInvWrapper(inputSlots,outputHandler));
	private final LazyOptional<RawMechanicalPowerHandler> powerUserholder = LazyOptional.of(() -> powerUser);
	
	protected float grindTime = 0;
	
	public MillstoneBlockEntity(BlockPos pos, BlockState state) {
		super(HarderOres.ModBlockEntities.machine_millstone, pos, state);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		MillstoneOrientation millpos = level.getBlockState(worldPosition).getValue(BlockProperties.MILL_ORIENTATION);
		if(cap == IMechanicalPower.MECHANICAL_POWER_CAPABILITY && millpos == MillstoneOrientation.CENTER) {
			return (LazyOptional<T>)powerUserholder;
		}
		if(cap == ForgeCapabilities.ITEM_HANDLER) {
			if(side == null) {
				return (LazyOptional<T>)all;
			}
			switch(side) {
				case UP:
					return millpos.canAcceptInput ? (LazyOptional<T>)input : LazyOptional.empty();
				case DOWN:
					return millpos.canAcceptOutput ? (LazyOptional<T>)input : LazyOptional.empty();
				default:
					return millpos == MillstoneOrientation.CENTER ? (LazyOptional<T>)input : LazyOptional.empty();
			}
		}
		return super.getCapability(cap, side);
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
			BlockEntity centerTE = world.getBlockEntity(millstone.worldPosition.offset(millpos.offset.getX(), 0, millpos.offset.getZ()));
			centerTE.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.EAST).ifPresent(inven ->{
				ItemStack stack = millstone.inputSlots.getStackInSlot(0).copy();
				stack.setCount(1);
				if(! inven.insertItem(0, stack, true).isEmpty()) return;
				millstone.inputSlots.insertItem(0,
					inven.insertItem(0, 
							millstone.inputSlots.extractItem(0, Container.LARGE_MAX_STACK_SIZE, false)
							, false)
					, false);
			});
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

	private float calcAndGetPower() {
		Level world = level;
		if(world.isClientSide) return powerUser.getScaledPower(powerUser.getRawPower());
		int numBlocksOut = 0;
		BlockPos p = worldPosition;
		Direction searchDir = Direction.UP;
		do {
			p = p.relative(searchDir,1);
			if(world.getBlockState(p).getBlock() == HarderOres.ModBlocks.machine_axel) {
				if(world.getBlockState(p).getValue(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.UP) {
					searchDir = Direction.UP;
				}
				else if(searchDir == Direction.UP) {
					searchDir = world.getBlockState(p).getValue(BlockStateProperties.HORIZONTAL_FACING);
				}
				numBlocksOut++;
			}
			else {
				p = p.relative(searchDir.getOpposite());
				break;
			}
		} while(numBlocksOut <= 8);
		BlockState s = world.getBlockState(p);
		if(s.getBlock() == HarderOres.ModBlocks.machine_axel && s.getValue(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.HUB) {
			BlockEntity te = world.getBlockEntity(p);
			IMechanicalPower pow = te.getCapability(IMechanicalPower.MECHANICAL_POWER_CAPABILITY, searchDir).orElse(null);
			if(pow != null) {
				powerUser.setRawPower(pow.getRawPower());
				return Math.max(powerUser.getScaledPower(powerUser.getRawPower()),0);
			}
		}

		return 0;
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
	protected void modSave(CompoundTag tag) {
		tag.put("harderores:power", powerUser.serializeNBT());
		tag.put("harderores:inputslot", inputSlots.serializeNBT());
		tag.put("harderores:outputSlot", outputSlots.serializeNBT());
		tag.putFloat("harderores:grindTime", grindTime);
	}


	@Override
	protected void modLoad(CompoundTag tag) {
		powerUser.deserializeNBT(tag.getCompound("harderores:power"));
		inputSlots.deserializeNBT(tag.getCompound("harderores:inputslot"));
		outputSlots.deserializeNBT(tag.getCompound("harderores:outputSlot"));
		grindTime = tag.getFloat("harderores:grindTime");
	}
}
