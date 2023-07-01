package com.draco18s.harderores.entity;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MotionlessItemEntity extends ItemEntity {
	public MotionlessItemEntity(Level p_32001_, double p_32002_, double p_32003_, double p_32004_, ItemStack p_32005_) {
		super(p_32001_, p_32002_, p_32003_, p_32004_, p_32005_);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		super.tick();
        float f = this.getEyeHeight() - 0.11111111F;
		if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > (double)f) {
			this.setDeltaMovement(Vec3.ZERO);
		}
	}
}
