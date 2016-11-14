package com.draco18s.hardlib.capability;

import java.util.concurrent.Callable;

import com.draco18s.hardlib.interfaces.IMechanicalPower;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
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
			public NBTBase writeNBT(Capability<IMechanicalPower> capability, IMechanicalPower instance, EnumFacing side)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("mechpower", instance.getRawPower());
				return tag;
			}

			@Override
			public void readNBT(Capability<IMechanicalPower> capability, IMechanicalPower instance, EnumFacing side, NBTBase base)
			{
				NBTTagCompound tag = (NBTTagCompound) base;
				instance.setRawPower(tag.getInteger("mechpower"));
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
