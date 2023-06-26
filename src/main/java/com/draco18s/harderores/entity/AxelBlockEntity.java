package com.draco18s.harderores.entity;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;
import com.draco18s.hardlib.api.interfaces.IMechanicalPower;
import com.draco18s.hardlib.api.internal.inventory.ModBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class AxelBlockEntity extends ModBlockEntity {
	private int[][] lightArray;
	protected RawMechanicalPowerHandler powerSupply;
	private LazyOptional<RawMechanicalPowerHandler> powerSupplyholder = LazyOptional.of(() -> powerSupply);

	public AxelBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
		super(HarderOres.ModBlockEntities.machine_axel, p_155229_, p_155230_);
		lightArray = new int[10][5];
		powerSupply = new RawMechanicalPowerHandler();
	}
	
	public static void tick(Level world, BlockPos pos, BlockState state, AxelBlockEntity axel) {
		int rawPower = axel.checkAirVolumeFull(pos.below(2), state.getValue(BlockStateProperties.HORIZONTAL_FACING));
		axel.powerSupply.setRawPower(rawPower);
	}
	
	private int checkAirVolumeFull(BlockPos pos, Direction dir) {
		if(level.isClientSide) return 750;
		ServerLevel serverLevel = (ServerLevel) level;
		
		RandomSource rand = level.random;
		int i = rand.nextInt(10);
		int k = rand.nextInt(5);
		
		BlockPos newpos = pos.offset(dir.getStepX() *i+dir.getStepZ()*k,0,dir.getStepZ()*i+dir.getStepX()*k);
		lightArray[i][k] = serverLevel.getBrightness(LightLayer.SKY, newpos);
		int lightTot = 0;
		//int lowest = 99;
		for(i = 0; i < 10; ++i) {
			for(k = 0; k < 5; ++k) {
				lightTot += lightArray[i][k];
			}	
		}
		
		return lightTot;
	}

	@Override
	protected void modSave(CompoundTag tag) {
		tag.put("harderores:power", powerSupply.serializeNBT());
	}

	@Override
	protected void modLoad(CompoundTag tag) {
		powerSupply.deserializeNBT(tag.getCompound("harderores:power"));
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability == IMechanicalPower.MECHANICAL_POWER_CAPABILITY) {
			return powerSupplyholder.cast();
		}
		return super.getCapability(capability, facing);
	}
}
