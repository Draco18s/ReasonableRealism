package com.draco18s.ores.entities;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.blockproperties.Props.MillstoneOrientation;
import com.draco18s.hardlib.capability.CapabilityMechanicalPower;
import com.draco18s.hardlib.capability.RawMechanicalPowerHandler;
import com.draco18s.hardlib.interfaces.IMechanicalPower;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.item.MillableItemsHandler;

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
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileEntityMillstone extends TileEntity implements ITickable {
	protected ItemStackHandler inputSlot;
	protected ItemStackHandler outputSlot;
	protected RawMechanicalPowerHandler powerUser;
	
	private float grindTime;
	private float powerLevel = 1;
	
	public TileEntityMillstone() {
		inputSlot = new MillableItemsHandler(1);
		outputSlot = new ItemStackHandler();
		powerUser = new MillstoneMechanicalPowerHandler();
	}

	@Override
	public void update() {
		if(worldObj.isRemote) return;
		if(worldObj.getBlockState(pos).getBlock() != this.getBlockType()) return;
		Props.MillstoneOrientation millpos = worldObj.getBlockState(pos).getValue(Props.MILL_ORIENTATION);
		if(millpos == Props.MillstoneOrientation.CENTER) {
			if(grindTime > 0) {
				float pow = calcAndGetPower();
	            grindTime -= pow;
	            if(inputSlot.getStackInSlot(0) == null) {
	            	grindTime = 0;
	            }
	            else if (grindTime <= 0) {
	            	grindItem(millpos);
	            	
	            	if(outputSlot.getStackInSlot(0) != null && outputSlot.getStackInSlot(0).stackSize >= 8) {
	            		if(!worldObj.isRemote) {	
	    					Random rand = worldObj.rand;
	    					float rx = rand.nextFloat() * 0.6F + 0.2F;
	    					float ry = rand.nextFloat() * 0.2F + 0.6F - 1;
	    					float rz = rand.nextFloat() * 0.6F + 0.2F;
	    					EntityItem entityItem = new EntityItem(worldObj,
	    							pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
	    							outputSlot.extractItem(0, 64, false));
	    					worldObj.spawnEntityInWorld(entityItem);
	    					entityItem.motionX = 0;
	    					entityItem.motionY = -0.2F;
	    					entityItem.motionZ = 0;
	    				}
	            	}
	            }
				this.markDirty();
			}
			else if (canGrind(millpos)) {
	            grindTime = 400;
	            //TODO: sounds
	            //OresBase.proxy.startMillSound(this);
	        }
		}
		else {
			if(worldObj.getBlockState(this.pos.add(millpos.offset.getX(), 0, millpos.offset.getZ())).getBlock() != blockType) {
				//worldObj.scheduleBlockUpdate(pos, blockType, 1, 10);
			}
			else if(inputSlot.getStackInSlot(0) != null) {
				TileEntity centerTE = worldObj.getTileEntity(this.pos.add(millpos.offset.getX(), 0, millpos.offset.getZ()));
				IItemHandler inven = centerTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.EAST);
				ItemStack stack = inputSlot.getStackInSlot(0).copy();
				stack.stackSize = 1;
				if(inven.insertItem(0, stack, true) == null) {
					inputSlot.setStackInSlot(0, inven.insertItem(0, inputSlot.getStackInSlot(0), false));
				}
			}
		}
	}

	private float calcAndGetPower() {
		int numBlocksOut = 0;
		BlockPos p = pos;
		EnumFacing searchDir = EnumFacing.UP;
		do {
			p = p.offset(searchDir,1);
			if(worldObj.getBlockState(p).getBlock() == OresBase.axel) {
				if(worldObj.getBlockState(p).getValue(Props.AXEL_ORIENTATION) == Props.AxelOrientation.UP) {
					searchDir = EnumFacing.UP;
				}
				else {
					searchDir = worldObj.getBlockState(p).getValue(BlockHorizontal.FACING);
				}
				numBlocksOut++;
			}
			else {
				p = p.offset(searchDir.getOpposite());
				numBlocksOut = 999;
			}
		} while(numBlocksOut <= 8);
		IBlockState s = worldObj.getBlockState(p);
		if(s.getBlock() == OresBase.axel && s.getValue(Props.AXEL_ORIENTATION) == Props.AxelOrientation.HUB) {
			TileEntity te = worldObj.getTileEntity(p);
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
			//System.out.println("#" + outputSlot.getStackInSlot(0).stackSize);
			inputSlot.extractItem(0, 1, false);
        }
	}

	private boolean canGrind(MillstoneOrientation millpos) {
		if(millpos != MillstoneOrientation.CENTER || inputSlot.getStackInSlot(0) == null) return false;
		ItemStack result = HardLibAPI.oreMachines.getMillResult(inputSlot.getStackInSlot(0));
		if(result == null) return false;
		
		return true;
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return (oldState.getBlock() != newSate.getBlock());
    }
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
    }

	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		IBlockState bs = worldObj.getBlockState(pos);
		Props.MillstoneOrientation millpos = bs.getValue(Props.MILL_ORIENTATION);
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(bs.getBlock() != getBlockType()) {//if the block at myself isn't myself, allow full access (Block Broken)
				return (T) new CombinedInvWrapper(inputSlot, outputSlot);
			}
			if(facing == null) {
				return (T) new CombinedInvWrapper(inputSlot, outputSlot);
			}
			if(millpos.canAcceptInput && facing == EnumFacing.UP) {
	            return (T) inputSlot;
			}
			if(millpos.canAcceptOutput && facing == EnumFacing.DOWN) {
	            return (T) outputSlot;
			}
			if(millpos == Props.MillstoneOrientation.CENTER && facing == EnumFacing.EAST) {
				return (T) inputSlot;
			}
		}
		if(capability == CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY) {
			if(millpos == Props.MillstoneOrientation.CENTER) {
				return (T) new RawMechanicalPowerHandler();
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
		}
		if(compound.hasKey("harderores:inputSlot")) {
			inputSlot.deserializeNBT((NBTTagCompound) compound.getTag("harderores:inputSlot"));
			outputSlot.deserializeNBT((NBTTagCompound) compound.getTag("harderores:outputSlot"));
		}
		grindTime = compound.getFloat("harderores:grindTime");
	}
}
