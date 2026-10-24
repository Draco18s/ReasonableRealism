package com.draco18s.hardlib.api.advancement;

import com.draco18s.hardlib.HardLib;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class WorldTimeTrigger extends HardLibCriteriaTrigger<WorldTimeTrigger.Instance> {
	public static final ResourceLocation ID = HardLib.getRL("total_world_time");
	public static LootContextParam<Float> TIME = new LootContextParam<Float>(ID);
	public static LootContextParamSet requiredParams = LootContextParamSet.builder().required(WorldTimeTrigger.TIME).build();

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	/** @param duration World age in ticks. */
	public record Instance(long duration) implements ICriterionTriggerInstanceTester {
		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
				Codec.LONG.fieldOf("duration").forGetter(Instance::duration)
			).apply(inst, Instance::new));

		@Override
		public boolean test(LootParams params) {
			return params.getParameter(TIME) >= duration;
		}
	}
}
