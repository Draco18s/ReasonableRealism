package com.draco18s.ores.entities;

import java.util.List;

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
import com.draco18s.ores.entities.capabilities.PackableItemsHandler;
import com.draco18s.ores.entities.capabilities.PackagerMechanicalPowerHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class TileEntityPackager extends TileEntity implements ITickable {
	protected ItemStackHandler inputSlot;
	protected ItemStackHandler outputSlot;
	private ItemStackHandler outputSlotWrapper;
	private float packTime;
	private float timeMod;
	protected RawMechanicalPowerHandler powerUser;
	private int activeSlot = -1;
	
	public TileEntityPackager() {
		inputSlot = new PackableItemsHandler();
		outputSlot = new ItemStackHandler();
		powerUser = new PackagerMechanicalPowerHandler();
		outputSlotWrapper = new OutputItemStackHandler(outputSlot);
		packTime = 0;
		timeMod = 1;
	}
	
	@Override
	public void update() {
		if(world.getBlockState(pos).getBlock() != this.getBlockType()) return;
		
		if(packTime > 0) {
			float pow = calcAndGetPower()/timeMod;
			packTime -= pow;
			boolean canAnyPack = false;
			int resetTime = -1;
			for(int s = 0; s < inputSlot.getSlots(); s++) {
				if(canPackage(s)) {
					canAnyPack = true;
					resetTime = s;
				}
			}
			if(resetTime != activeSlot) {
				activeSlot = resetTime;
				packTime = 100;
			}
			if(!canAnyPack) {
				packTime = 0;
			}
			else if (packTime <= 0) {
				packItem();
			}
		}
		else {
			activeSlot = -1;
			for(int s = 0; s < inputSlot.getSlots(); s++) {
				if(canPackage(s)) {
					activeSlot = s;
					packTime = 100;
					ItemStack nextResult = HardLibAPI.oreMachines.getPressurePackResult(inputSlot.getStackInSlot(s),true);
					float inMod = 1;
					float outMod = 1;
					if(inputSlot.getStackInSlot(s).getItem() instanceof ItemBlock) {
						try {
							Block block = Block.getBlockFromItem(inputSlot.getStackInSlot(s).getItem());
							
							IBlockState state;
							state = block.getStateForPlacement(null, BlockPos.ORIGIN, EnumFacing.DOWN, 0, 0, 0, nextResult.getMetadata(), null);
							inMod = state.getBlockHardness(null, BlockPos.ORIGIN) * 2;
							if(block.getHarvestTool(state).equals("pickaxe") && block.getHarvestLevel(state) >= 0) {
								inMod *= (block.getHarvestLevel(state) + 2);
							}
						}
						catch(NullPointerException e) {
							inMod = 1;
						}
					}
					if(!nextResult.isEmpty() && nextResult.getItem() instanceof ItemBlock) {
						try {
							Block block = Block.getBlockFromItem(nextResult.getItem());
							
							IBlockState state;
							state = block.getStateForPlacement(null, BlockPos.ORIGIN, EnumFacing.DOWN, 0, 0, 0, nextResult.getMetadata(), null);
							outMod = state.getBlockHardness(null, BlockPos.ORIGIN);
							if(block.getHarvestTool(state).equals("shovel") && block.getHarvestLevel(state) >= 0) {
								outMod *= 2;
							}
						}
						catch(NullPointerException e) {
							outMod = 1;
						}
					}
					timeMod = Math.max(inMod, outMod);
				}
			}
		}
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
		this.markDirty();
		return 0;
	}

	private boolean canPackage(int slot) {
		if(inputSlot.getStackInSlot(slot).isEmpty()) return false;
		ItemStack result = HardLibAPI.oreMachines.getPressurePackResult(inputSlot.getStackInSlot(slot), true);
		if(result.isEmpty()) return false;
		if(!outputSlot.insertItem(0, result, true).isEmpty()) return false;
		return true;
	}

	private void packItem() {
		for(int s = 0; s < inputSlot.getSlots(); s++) {
			ItemStack stack = inputSlot.getStackInSlot(s);
			if(stack.isEmpty()) continue;
			ItemStack result = HardLibAPI.oreMachines.getPressurePackResult(stack, true);
			if(result.isEmpty()) continue;
			if(!outputSlot.insertItem(0, result, true).isEmpty()) continue;
			inputSlot.extractItem(s, HardLibAPI.oreMachines.getPressurePackAmount(stack), false);
			outputSlot.insertItem(0, result.copy(), false);
		}
		this.markDirty();
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
				return (T) new CombinedInvWrapper(inputSlot, outputSlotWrapper);
			}
			if(facing == EnumFacing.UP) {
				return (T) inputSlot;
			}
			if(facing == EnumFacing.DOWN) {
				return (T) outputSlotWrapper;
			}
		}
		if(capability == CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY) {
			return (T) powerUser;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("harderores:power", powerUser.serializeNBT());
		compound.setTag("harderores:inputSlot", inputSlot.serializeNBT());
		compound.setTag("harderores:outputSlot", outputSlot.serializeNBT());
		compound.setFloat("harderores:packTime", packTime);
		compound.setFloat("harderores:timeMod", timeMod);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(inputSlot == null) {
			inputSlot = new PackableItemsHandler();
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
		packTime = compound.getFloat("harderores:packTime");
		timeMod = compound.getFloat("harderores:timeMod");
	}

	public float getTime() {
		return packTime;
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
}
