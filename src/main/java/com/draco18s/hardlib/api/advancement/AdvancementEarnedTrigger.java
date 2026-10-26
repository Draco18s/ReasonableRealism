package com.draco18s.hardlib.api.advancement;

import com.draco18s.hardlib.HardLib;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;

/**
 * hardlib:advancement_earned {advancement: id} - met once the player has the named advancement. Lets an advancement
 * require more than its one tree parent (which is all vanilla tracks).
 */
public class AdvancementEarnedTrigger extends HardLibCriteriaTrigger<AdvancementEarnedTrigger.Instance> {
	public static final Identifier ID = HardLib.getRL("advancement_earned");
	public static ContextKey<Identifier> ADVANCEMENT = new ContextKey<Identifier>(ID);
	public static ContextKeySet requiredParams = new ContextKeySet.Builder().required(AdvancementEarnedTrigger.ADVANCEMENT).build();

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	/** @param advancement The advancement this criterion waits for. */
	public record Instance(Identifier advancement) implements ICriterionTriggerInstanceTester {
		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
				Identifier.CODEC.fieldOf("advancement").forGetter(Instance::advancement)
			).apply(inst, Instance::new));

		@Override
		public boolean test(LootParams params) {
			return advancement.equals(params.contextMap().getOrThrow(ADVANCEMENT));
		}
	}
}
