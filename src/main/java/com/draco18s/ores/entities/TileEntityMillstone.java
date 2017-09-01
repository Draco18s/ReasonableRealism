package com.draco18s.ores.entities;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.api.capability.CapabilityMechanicalPower;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;
import com.draco18s.hardlib.api.interfaces.IMechanicalPower;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.entities.capabilities.MillableItemsHandler;
import com.draco18s.ores.entities.capabilities.MillstoneMechanicalPowerHandler;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileEntityMillstone extends TileEntity implements ITickable {
	protected ItemStackHandler inputSlot;
	protected ItemStackHandler outputSlot;
	private ItemStackHandler outputSlotWrapper;
	protected RawMechanicalPowerHandler powerUser;
	
	private float grindTime;
	private float powerLevel = 1;
	
	public TileEntityMillstone() {
		inputSlot = new MillableItemsHandler(1);
		outputSlot = new ItemStackHandler();
		powerUser = new MillstoneMechanicalPowerHandler();
		outputSlotWrapper = new OutputItemStackHandler(outputSlot);
	}

	@Override
	public void update() {
		//if(world.isRemote) return;
		if(world.getBlockState(pos).getBlock() != this.getBlockType()) return;
		MillstoneOrientation millpos = world.getBlockState(pos).getValue(Props.MILL_ORIENTATION);
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
							IBlockState s = world.getBlockState(pos.down());
							if(s.getBlock().isAir(s, world, pos)) {
								Random rand = world.rand;
								float rx = rand.nextFloat() * 0.6F + 0.2F;
								float ry = rand.nextFloat() * 0.2F + 0.6F - 1;
								float rz = rand.nextFloat() * 0.6F + 0.2F;
								EntityItem entityItem = new EntityItem(world,
										pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
										outputSlot.extractItem(0, 64, false));
								world.spawnEntity(entityItem);
								entityItem.motionX = 0;
								entityItem.motionY = -0.2F;
								entityItem.motionZ = 0;
							}
						}
					}
				}
				this.markDirty();
			}
			else if (canGrind(millpos)) {
				grindTime = 400;
				OresBase.proxy.startMillSound(this);
			}
		}
		else {
			if(world.getBlockState(this.pos.add(millpos.offset.getX(), 0, millpos.offset.getZ())).getBlock() != blockType) {
				//world.scheduleBlockUpdate(pos, blockType, 1, 10);
			}
			else if(!inputSlot.getStackInSlot(0).isEmpty()) {
				TileEntity centerTE = world.getTileEntity(this.pos.add(millpos.offset.getX(), 0, millpos.offset.getZ()));
				IItemHandler inven = centerTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.EAST);
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
		EnumFacing searchDir = EnumFacing.UP;
		do {
			p = p.offset(searchDir,1);
			if(world.getBlockState(p).getBlock() == OresBase.axel) {
				if(world.getBlockState(p).getValue(Props.AXEL_ORIENTATION) == AxelOrientation.UP) {
					searchDir = EnumFacing.UP;
				}
				else {
					searchDir = world.getBlockState(p).getValue(BlockHorizontal.FACING);
				}
				numBlocksOut++;
			}
			else {
				p = p.offset(searchDir.getOpposite());
				numBlocksOut = 999;
			}
		} while(numBlocksOut <= 8);
		IBlockState s = world.getBlockState(p);
		if(s.getBlock() == OresBase.axel && s.getValue(Props.AXEL_ORIENTATION) == AxelOrientation.HUB) {
			TileEntity te = world.getTileEntity(p);
			if(te.hasCapability(CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY, searchDir)) {
				IMechanicalPower pow = te.getCapability(CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY, searchDir);
				powerUser.setRawPower(pow.getRawPower());
				return powerUser.getScaledPower(powerUser.getRawPower());
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
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			this.markDirty();
			if(world != null && world.getBlockState(pos).getBlock() != getBlockType()) {//if the block at myself isn't myself, allow full access (Block Broken)
				return (T) new CombinedInvWrapper(inputSlot, outputSlotWrapper);
			}
			if(facing == null) {
				return (T) new CombinedInvWrapper(inputSlot, outputSlot);
			}
			if(world == null) {
				if(facing == EnumFacing.UP) {
					return (T) inputSlot;
				}
				if(facing == EnumFacing.DOWN) {
					return (T) outputSlotWrapper;
				}
				return super.getCapability(capability, facing);
			}
			MillstoneOrientation millpos = world.getBlockState(pos).getValue(Props.MILL_ORIENTATION);
			if(millpos.canAcceptInput && facing == EnumFacing.UP) {
				return (T) inputSlot;
			}
			if(millpos.canAcceptOutput && facing == EnumFacing.DOWN) {
				return (T) outputSlotWrapper;
			}
			if(millpos == MillstoneOrientation.CENTER && facing == EnumFacing.EAST) {
				return (T) inputSlot;
			}
		}
		if(capability == CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY) {
			MillstoneOrientation millpos = world.getBlockState(pos).getValue(Props.MILL_ORIENTATION);
			if(millpos == MillstoneOrientation.CENTER) {
				return (T) powerUser;
			}
		}
		return super.getCapability(capability, facing);
	}
	
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("harderores:power", powerUser.serializeNBT());
		compound.setTag("harderores:inputSlot", inputSlot.serializeNBT());
		compound.setTag("harderores:outputSlot", outputSlot.serializeNBT());
		compound.setFloat("harderores:grindTime", grindTime);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(inputSlot == null) {
			inputSlot = new MillableItemsHandler(1);
			outputSlot = new ItemStackHandler();
			outputSlotWrapper = new OutputItemStackHandler(outputSlot);
		}
		if(compound.hasKey("harderores:inputSlot")) {
			inputSlot.deserializeNBT((NBTTagCompound) compound.getTag("harderores:inputSlot"));
		}
		if(compound.hasKey("harderores:outputSlot")) {
			outputSlot.deserializeNBT((NBTTagCompound) compound.getTag("harderores:outputSlot"));
		}
		if(compound.hasKey("harderores:power")) {
			powerUser.deserializeNBT((NBTTagCompound) compound.getTag("harderores:power"));
		}
		grindTime = compound.getFloat("harderores:grindTime");
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public float getGrindTime() {
		return grindTime;
	}
}
