package com.draco18s.harderores.world.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.world.gen.feature.IFeatureConfig;

public class OreVeinStructureConfig implements IFeatureConfig {
	public final double probability;

	public OreVeinStructureConfig(double probability) {
		this.probability = probability;
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("probability"), ops.createDouble(this.probability))));
	}

	public static <T> OreVeinStructureConfig deserialize(Dynamic<T> p_214638_0_) {
		float f = p_214638_0_.get("probability").asFloat(0.0F);
		return new OreVeinStructureConfig((double)f);
	}
}
