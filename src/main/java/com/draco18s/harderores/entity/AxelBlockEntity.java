package com.draco18s.harderores.entity;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;
import com.draco18s.hardlib.api.interfaces.IMechanicalPower;
import com.draco18s.hardlib.api.internal.inventory.ModBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class AxelBlockEntity extends ModBlockEntity {
	private int[][] lightArray;
	protected RawMechanicalPowerHandler powerSupply;

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
		if(level.isClientSide()) return 750;
		ServerLevel serverLevel = (ServerLevel) level;
		
		RandomSource rand = level.getRandom();
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
	protected void modSave(ValueOutput output) {
		powerSupply.serialize(output.child("harderores:power"));
	}

	@Override
	protected void modLoad(ValueInput input) {
		powerSupply.deserialize(input.childOrEmpty("harderores:power"));
	}
	
	/** The power the axle supplies (see HarderOres' capability registration). */
	public IMechanicalPower getMechanicalPower(@Nullable Direction facing) {
		return powerSupply;
	}
}
