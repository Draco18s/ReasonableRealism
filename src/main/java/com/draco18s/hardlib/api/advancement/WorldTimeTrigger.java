package com.draco18s.hardlib.api.advancement;

import com.draco18s.hardlib.HardLib;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;

public class WorldTimeTrigger extends HardLibCriteriaTrigger<WorldTimeTrigger.Instance> {
	public static final Identifier ID = HardLib.getRL("total_world_time");
	public static ContextKey<Float> TIME = new ContextKey<Float>(ID);
	public static ContextKeySet requiredParams = new ContextKeySet.Builder().required(WorldTimeTrigger.TIME).build();

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
			return params.contextMap().getOrThrow(TIME) >= duration;
		}
	}
}
