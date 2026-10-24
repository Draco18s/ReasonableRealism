package com.draco18s.hardlib.api.advancement;

import com.draco18s.flowers.OreFlowers;
import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class FoundOreTrigger extends HardLibCriteriaTrigger<FoundOreTrigger.Instance> {
	public static final ResourceLocation ID = OreFlowers.getRL("found_ore");
	public static LootContextParam<Integer> ORE_COUNT = new LootContextParam<Integer>(ID);
	public static LootContextParamSet requiredParams = LootContextParamSet.builder().required(FoundOreTrigger.ORE_COUNT).build();

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance() implements ICriterionTriggerInstanceTester {
		public static final Codec<Instance> CODEC = Codec.unit(new Instance());

		@Override
		public boolean test(LootParams params) {
			return params.getParameter(ORE_COUNT) > 0;
		}
	}
}
