package com.draco18s.hardlib.api.advancement;

import com.draco18s.harderores.HarderOres;
import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class MillstoneTrigger extends HardLibCriteriaTrigger<MillstoneTrigger.Instance> {
	public static final ResourceLocation ID = HarderOres.getRL("millstone_grind");
	public static LootContextParam<Float> POWER = new LootContextParam<Float>(ID);
	public static LootContextParamSet requiredParams = LootContextParamSet.builder().required(MillstoneTrigger.POWER).build();

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance() implements ICriterionTriggerInstanceTester {
		public static final Codec<Instance> CODEC = Codec.unit(new Instance());

		@Override
		public boolean test(LootParams params) {
			return params.getParameter(POWER) > 0;
		}
	}
}
