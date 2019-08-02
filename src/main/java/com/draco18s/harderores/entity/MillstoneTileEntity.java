package com.draco18s.harderores.entity;

import java.util.Random;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.capability.MillableItemsHandler;
import com.draco18s.harderores.entity.capability.MillstoneMechanicalPowerHandler;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.api.capability.CapabilityMechanicalPower;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;
import com.draco18s.hardlib.api.interfaces.capability.IMechanicalPower;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class MillstoneTileEntity extends TileEntity implements ITickableTileEntity {
	protected ItemStackHandler inputSlot;
	protected ItemStackHandler outputSlot;
	private ItemStackHandler outputSlotWrapper;
	private final LazyOptional<IItemHandler> inputSlotholder = LazyOptional.of(() -> inputSlot);
	private final LazyOptional<IItemHandler> outputSlotWrapperholder = LazyOptional.of(() -> outputSlotWrapper);
	private final LazyOptional<IItemHandler> everything = LazyOptional.of(() -> new CombinedInvWrapper(inputSlot, outputSlot));
	protected RawMechanicalPowerHandler powerUser;
	private final LazyOptional<RawMechanicalPowerHandler> powerUserholder = LazyOptional.of(() -> powerUser);

	private float grindTime;
	//private float powerLevel = 1;

	public MillstoneTileEntity() {
		super(HarderOres.ModTileEntities.millstone);
		inputSlot = new MillableItemsHandler(1);
		outputSlot = new ItemStackHandler();
		powerUser = new MillstoneMechanicalPowerHandler();
		outputSlotWrapper = new OutputItemStackHandler(outputSlot);
	}

	@Override
	public void tick() {
		//if(world.isRemote) return;
		if(world.getBlockState(pos).getBlock() != this.getBlockState().getBlock()) return;
		MillstoneOrientation millpos = world.getBlockState(pos).get(BlockProperties.MILL_ORIENTATION);
		if(millpos == MillstoneOrientation.CENTER) {
			if(grindTime > 0) {
				float pow = calcAndGetPower();
				grindTime -= pow;
				if(inputSlot.getStackInSlot(0).isEmpty()) {
					grindTime = 0;
				}
				else if (grindTime <= 0) {
					grindItem(millpos);

					if(!outputSlot.getStackInSlot(0).isEmpty() && (outputSlot.getStackInSlot(0).getCount() >= 8 || inputSlot.getStackInSlot(0).isEmpty())) {
						if(!world.isRemote) {
							BlockState s = world.getBlockState(pos.down());
							if(s.getBlock().isAir(s, world, pos)) {
								Random rand = world.rand;
								float rx = rand.nextFloat() * 0.6F + 0.2F;
								float ry = rand.nextFloat() * 0.2F + 0.6F - 1;
								float rz = rand.nextFloat() * 0.6F + 0.2F;
								ItemEntity itemEntity = new ItemEntity(world,
										pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
										outputSlot.extractItem(0, 64, false));
								world.addEntity(itemEntity);
								itemEntity.setMotion(0, -0.2F, 0);
							}
						}
					}
				}
				this.markDirty();
			}
			else if (canGrind(millpos)) {
				grindTime = 400;
				//TODO
				//HarderOres.proxy.startMillSound(this);
			}
		}
		else {
			if(world.getBlockState(this.pos.add(millpos.offset.getX(), 0, millpos.offset.getZ())).getBlock() != this.getBlockState().getBlock()) {
				//world.scheduleBlockUpdate(pos, blockType, 1, 10);
			}
			else if(!inputSlot.getStackInSlot(0).isEmpty()) {
				TileEntity centerTE = world.getTileEntity(this.pos.add(millpos.offset.getX(), 0, millpos.offset.getZ()));
				IItemHandler inven = centerTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.EAST).orElse(null);
				ItemStack stack = inputSlot.getStackInSlot(0).copy();
				stack.setCount(1);
				if(inven != null && inven.insertItem(0, stack, true).isEmpty()) {
					inputSlot.setStackInSlot(0, inven.insertItem(0, inputSlot.getStackInSlot(0), false));
				}
			}
		}
	}

	public float getPower() {
		return calcAndGetPower();
	}

	private float calcAndGetPower() {
		if(world.isRemote) return powerUser.getScaledPower(powerUser.getRawPower());
		int numBlocksOut = 0;
		BlockPos p = pos;
		Direction searchDir = Direction.UP;
		do {
			p = p.offset(searchDir,1);
			if(world.getBlockState(p).getBlock() == HarderOres.ModBlocks.axel) {
				if(world.getBlockState(p).get(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.UP) {
					searchDir = Direction.UP;
				}
				else {
					searchDir = world.getBlockState(p).get(BlockStateProperties.HORIZONTAL_FACING);
				}
				numBlocksOut++;
			}
			else {
				p = p.offset(searchDir.getOpposite());
				numBlocksOut = 999;
			}
		} while(numBlocksOut <= 8);
		BlockState s = world.getBlockState(p);
		if(s.getBlock() == HarderOres.ModBlocks.axel && s.get(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.HUB) {
			TileEntity te = world.getTileEntity(p);
			IMechanicalPower pow = te.getCapability(CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY, searchDir).orElse(null);
			if(pow != null) {
				powerUser.setRawPower(pow.getRawPower());
				return Math.max(powerUser.getScaledPower(powerUser.getRawPower()),0);
			}
		}

		return 0;
	}

	private void grindItem(MillstoneOrientation millpos) {
		if (canGrind(millpos)) {
			ItemStack result = HardLibAPI.oreMachines.getMillResult(inputSlot.getStackInSlot(0)).copy();

			outputSlot.insertItem(0, result, false);

			inputSlot.extractItem(0, 1, false);
			this.markDirty();
		}
	}

	public boolean canGrind(MillstoneOrientation millpos) {
		if(millpos != MillstoneOrientation.CENTER || inputSlot.getStackInSlot(0).isEmpty()) return false;
		ItemStack result = HardLibAPI.oreMachines.getMillResult(inputSlot.getStackInSlot(0));
		if(result.isEmpty()) return false;

		return true;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			this.markDirty();
			if(world != null && world.getBlockState(pos).getBlock() != this.getBlockState().getBlock()) {//if the block at myself isn't myself, allow full access (Block Broken)
				return everything.cast();
			}
			if(facing == null) {
				return everything.cast();
			}
			if(world == null) {
				if(facing == Direction.UP) {
					return inputSlotholder.cast();
				}
				if(facing == Direction.DOWN) {
					return outputSlotWrapperholder.cast();
				}
				return super.getCapability(capability, facing);
			}
			MillstoneOrientation millpos = world.getBlockState(pos).get(BlockProperties.MILL_ORIENTATION);
			if(millpos.canAcceptInput && facing == Direction.UP) {
				return inputSlotholder.cast();
			}
			if(millpos.canAcceptOutput && facing == Direction.DOWN) {
				return outputSlotWrapperholder.cast();
			}
			if(millpos == MillstoneOrientation.CENTER && facing == Direction.EAST) {
				return inputSlotholder.cast();
			}
		}
		if(capability == CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY) {
			MillstoneOrientation millpos = world.getBlockState(pos).get(BlockProperties.MILL_ORIENTATION);
			if(millpos == MillstoneOrientation.CENTER) {
				return powerUserholder.cast();
			}
		}
		return super.getCapability(capability, facing);
	}

	public float getGrindTime() {
		return grindTime;
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}
	
	@Override
	public CompoundNBT write(CompoundNBT tag) {
		tag = super.write(tag);
		tag.put("harderores:power", powerUser.serializeNBT());
		tag.put("harderores:inputslot", inputSlot.serializeNBT());
		tag.put("harderores:outputSlot", outputSlot.serializeNBT());
		tag.putFloat("harderores:grindTime", grindTime);
		return tag;
	}
	
	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		powerUser.deserializeNBT(tag.getCompound("harderores:power"));
		inputSlot.deserializeNBT(tag.getCompound("harderores:inputslot"));
		outputSlot.deserializeNBT(tag.getCompound("harderores:outputSlot"));
		grindTime = tag.getFloat("harderores:grindTime");
	}
}
