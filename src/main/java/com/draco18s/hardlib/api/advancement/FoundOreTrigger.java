package com.draco18s.hardlib.api.advancement;

import com.draco18s.flowers.OreFlowers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;

public class FoundOreTrigger extends HardLibCriteriaTrigger<FoundOreTrigger.Instance> {
	public static final Identifier ID = OreFlowers.getRL("found_ore");
	public static ContextKey<Integer> ORE_COUNT = new ContextKey<Integer>(ID);
	public static ContextKeySet requiredParams = new ContextKeySet.Builder().required(FoundOreTrigger.ORE_COUNT).build();

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance() implements ICriterionTriggerInstanceTester {
		public static final Codec<Instance> CODEC = MapCodec.unitCodec(new Instance());

		@Override
		public boolean test(LootParams params) {
			return params.contextMap().getOrThrow(ORE_COUNT) > 0;
		}
	}
}
