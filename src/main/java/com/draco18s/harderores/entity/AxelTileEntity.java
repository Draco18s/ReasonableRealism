package com.draco18s.harderores.entity;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.capability.CapabilityMechanicalPower;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class AxelTileEntity extends TileEntity implements ITickableTileEntity {
	private int[][] lightArray;
	protected RawMechanicalPowerHandler powerSupply;
	private LazyOptional<RawMechanicalPowerHandler> powerSupplyholder = LazyOptional.of(() -> powerSupply);

	public AxelTileEntity() {
		super(HarderOres.ModTileEntities.axel);
		lightArray = new int[10][5];
		powerSupply = new RawMechanicalPowerHandler();
	}
	
	@Override
	public void tick() {
		if(world.isRemote) {
			//System.out.println(powerAmt + " " + powerScale(powerAmt));
		}
		else if(this.pos.getY() >= world.getSeaLevel()-4) {
			if(world.getBlockState(pos).get(BlockProperties.AXEL_ORIENTATION) != AxelOrientation.HUB) return;
			
			int rawPower = checkAirVolumeFull(pos.down(2), this.world.getBlockState(this.pos).get(BlockStateProperties.HORIZONTAL_FACING));
			powerSupply.setRawPower(rawPower);
		}
	}
	
	private int checkAirVolumeFull(BlockPos pos, Direction dir) {
		if(world.isRemote) return 750;
		
		Random rand = new Random();
		int i = rand.nextInt(10);
		int k = rand.nextInt(5);
		
		BlockPos newpos = pos.add(dir.getXOffset()*i+dir.getZOffset()*k,0,dir.getZOffset()*i+dir.getXOffset()*k);
		lightArray[i][k] = world.getLightFor(LightType.SKY, newpos);
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
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(world != null) {
			BlockState bs = world.getBlockState(pos);
			AxelOrientation millpos = bs.get(BlockProperties.AXEL_ORIENTATION);
			if(capability == CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY) {
				if(millpos == AxelOrientation.HUB) {
					return powerSupplyholder.cast();
				}
			}
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public void remove() {
		super.remove();
		powerSupplyholder.invalidate();
	}
	
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}
	
	@Override
	public CompoundNBT write(CompoundNBT tag) {
		tag = super.write(tag);
		tag.put("harderores:power", powerSupply.serializeNBT());
		return tag;
	}
	
	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		powerSupply.deserializeNBT(tag.getCompound("harderores:power"));
	}
}
