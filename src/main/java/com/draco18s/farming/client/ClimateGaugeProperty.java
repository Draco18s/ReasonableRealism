package com.draco18s.farming.client;

import javax.annotation.Nullable;

import com.draco18s.farming.item.ClimateGaugeItem;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;

/**
 * The harderfarming:climate item model property: the thermometer's or hydrometer's frame, 0-11 (it was the "time"
 * property, which is the clock's now).
 */
public record ClimateGaugeProperty() implements RangeSelectItemModelProperty {
	public static final MapCodec<ClimateGaugeProperty> MAP_CODEC = MapCodec.unit(new ClimateGaugeProperty());

	@Override
	public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		return stack.getItem() instanceof ClimateGaugeItem gauge ? gauge.getFrame(stack, level, owner) : 0;
	}

	@Override
	public MapCodec<ClimateGaugeProperty> type() {
		return MAP_CODEC;
	}
}
