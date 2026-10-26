package com.draco18s.harderores.client;

import javax.annotation.Nullable;

import com.draco18s.harderores.block.ore.HardOreBlock;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;

/** The harderores:ore_density item model property: a hard ore item's density (its model shows that much ore). */
public record OreDensityProperty() implements RangeSelectItemModelProperty {
	public static final MapCodec<OreDensityProperty> MAP_CODEC = MapCodec.unit(new OreDensityProperty());

	@Override
	public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		return HardOreBlock.getDensity(stack);
	}

	@Override
	public MapCodec<OreDensityProperty> type() {
		return MAP_CODEC;
	}
}
