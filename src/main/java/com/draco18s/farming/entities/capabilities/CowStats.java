package com.draco18s.farming.entities.capabilities;

import java.lang.ref.WeakReference;

import com.draco18s.farming.util.AnimalUtil;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;

public class CowStats implements IMilking {
	private int milkLevel;
	public WeakReference<EntityCow> theCow;

	public CowStats(EntityCow anim) {
		theCow = new WeakReference<EntityCow>(anim);
	}

	@Override
	public int getMilkLevel() {
		return milkLevel;
	}

	@Override
	public void setMilkLevel(int milkLevel) {
		this.milkLevel = milkLevel;
	}
	
	@Override
	public boolean getIsMilkable() {
		return milkLevel >= AnimalUtil.milkQuanta + 4000;
	}
	
	@Override
	public void doMilking() {
		milkLevel -= AnimalUtil.milkQuanta;
	}
}
