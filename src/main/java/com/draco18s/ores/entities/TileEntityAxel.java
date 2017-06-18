package com.draco18s.ores.entities;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.capability.CapabilityMechanicalPower;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;
import com.draco18s.ores.entities.capabilities.PackableItemsHandler;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityAxel extends TileEntity implements ITickable {
	private int[][] lightArray;
	protected RawMechanicalPowerHandler powerSupply;
	
	public TileEntityAxel() {
		lightArray = new int[10][5];
		powerSupply = new RawMechanicalPowerHandler();
	}

	@Override
	public void update() {
		if(world.isRemote) {
			//System.out.println(powerAmt + " " + powerScale(powerAmt));
		}
		else if(this.pos.getY() >= world.provider.getAverageGroundLevel()-4) {
			if(world.getBlockState(pos).getValue(Props.AXEL_ORIENTATION) != AxelOrientation.HUB) return;
			
			int rawPower = checkAirVolumeFull(pos.down(2), this.world.getBlockState(this.pos).getValue(BlockHorizontal.FACING));
			powerSupply.setRawPower(rawPower);
		}
	}

	private int checkAirVolumeFull(BlockPos pos, EnumFacing dir) {
		if(world.isRemote) return 750;
		
		int x = (int) Math.signum(pos.getX());
		int z = (int) Math.signum(pos.getZ());
		Random rand = new Random();
		int i = rand.nextInt(10);
		int k = rand.nextInt(5);
		
		BlockPos newpos = pos.add(dir.getFrontOffsetX()*i+dir.getFrontOffsetZ()*k,0,dir.getFrontOffsetZ()*i+dir.getFrontOffsetX()*k);
		lightArray[i][k] = world.getLightFor(EnumSkyBlock.SKY, newpos);
		int lightTot = 0;
		int lowest = 99;
		for(i = 0; i < 10; ++i) {
			for(k = 0; k < 5; ++k) {
				lightTot += lightArray[i][k];
				if(lightArray[i][k] < lowest) {
					lowest = lightArray[i][k];
				}
			}	
		}
		
		return lightTot;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(world != null) {
			IBlockState bs = world.getBlockState(pos);
			AxelOrientation millpos = bs.getValue(Props.AXEL_ORIENTATION);
			if(capability == CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY) {
				if(millpos == AxelOrientation.HUB) {
					return (T) powerSupply;
				}
			}
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
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("harderores:power", powerSupply.serializeNBT());
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(compound.hasKey("harderores:power")) {
			powerSupply.deserializeNBT((NBTTagCompound) compound.getTag("harderores:power"));
		}
	}
}
