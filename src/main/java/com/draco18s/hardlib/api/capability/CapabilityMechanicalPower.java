package com.draco18s.hardlib.api.capability;

import java.util.concurrent.Callable;

import com.draco18s.hardlib.api.interfaces.capability.IMechanicalPower;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityMechanicalPower {
	
	@CapabilityInject(IMechanicalPower.class)
	public static Capability<IMechanicalPower> MECHANICAL_POWER_CAPABILITY = null;
	
	public static void register()
	{
		CapabilityManager.INSTANCE.register(IMechanicalPower.class, new Capability.IStorage<IMechanicalPower>()
		{
			@Override
			public CompoundNBT writeNBT(Capability<IMechanicalPower> capability, IMechanicalPower instance, Direction side)
			{
				CompoundNBT tag = new CompoundNBT();
				tag.putInt("mechpower", instance.getRawPower());
				return tag;
			}

			@Override
			public void readNBT(Capability<IMechanicalPower> capability, IMechanicalPower instance, Direction side, INBT nbt) {
				CompoundNBT tag = (CompoundNBT) nbt;
				instance.setRawPower(tag.getInt("mechpower"));
			}
		}, new Callable<RawMechanicalPowerHandler>()
		{

			@Override
			public RawMechanicalPowerHandler call() throws Exception {
				return new RawMechanicalPowerHandler();
			}
		});
	}
}