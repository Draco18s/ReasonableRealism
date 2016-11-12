package com.draco18s.farming.entities.capabilities;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class MilkStorage implements IStorage<IMilking> {

	@Override
	public NBTBase writeNBT(Capability<IMilking> capability, IMilking instance, EnumFacing side) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("harderfarming:milklevel",instance.getMilkLevel());
		return tag;
	}

	@Override
	public void readNBT(Capability<IMilking> capability, IMilking instance, EnumFacing side, NBTBase nbt) {
		if(nbt instanceof NBTTagCompound) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
			instance.setMilkLevel(tag.getInteger("harderfarming:milklevel"));
		}
	}

	public static class Factory implements Callable<IMilking> {
		@Override
		public IMilking call() throws Exception {
			return new CowStats(null);
		}
	}
}
