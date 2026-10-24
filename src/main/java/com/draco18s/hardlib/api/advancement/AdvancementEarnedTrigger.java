package com.draco18s.hardlib.api.advancement;

import com.draco18s.hardlib.HardLib;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

/**
 * hardlib:advancement_earned {advancement: id} - met once the player has the named advancement. Lets an advancement
 * require more than its one tree parent (which is all vanilla tracks).
 */
public class AdvancementEarnedTrigger extends HardLibCriteriaTrigger<AdvancementEarnedTrigger.Instance> {
	public static final ResourceLocation ID = HardLib.getRL("advancement_earned");
	public static LootContextParam<ResourceLocation> ADVANCEMENT = new LootContextParam<ResourceLocation>(ID);
	public static LootContextParamSet requiredParams = LootContextParamSet.builder().required(AdvancementEarnedTrigger.ADVANCEMENT).build();

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	/** @param advancement The advancement this criterion waits for. */
	public record Instance(ResourceLocation advancement) implements ICriterionTriggerInstanceTester {
		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
				ResourceLocation.CODEC.fieldOf("advancement").forGetter(Instance::advancement)
			).apply(inst, Instance::new));

		@Override
		public boolean test(LootParams params) {
			return advancement.equals(params.getParameter(ADVANCEMENT));
		}
	}
}
