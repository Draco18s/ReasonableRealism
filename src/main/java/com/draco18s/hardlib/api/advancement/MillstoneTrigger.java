package com.draco18s.hardlib.api.advancement;

import com.draco18s.harderores.HarderOres;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;

public class MillstoneTrigger extends HardLibCriteriaTrigger<MillstoneTrigger.Instance> {
	public static final Identifier ID = HarderOres.getRL("millstone_grind");
	public static ContextKey<Float> POWER = new ContextKey<Float>(ID);
	public static ContextKeySet requiredParams = new ContextKeySet.Builder().required(MillstoneTrigger.POWER).build();

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance() implements ICriterionTriggerInstanceTester {
		public static final Codec<Instance> CODEC = MapCodec.unitCodec(new Instance());

		@Override
		public boolean test(LootParams params) {
			return params.contextMap().getOrThrow(POWER) > 0;
		}
	}
}
