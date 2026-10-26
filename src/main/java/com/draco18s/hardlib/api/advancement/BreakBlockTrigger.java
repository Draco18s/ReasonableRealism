package com.draco18s.hardlib.api.advancement;

import java.util.Optional;

import com.draco18s.hardlib.HardLib;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class BreakBlockTrigger extends HardLibCriteriaTrigger<BreakBlockTrigger.Instance> {
	public static final Identifier ID = HardLib.getRL("break_block");
	public static ContextKeySet requiredParams = new ContextKeySet.Builder().required(LootContextParams.BLOCK_STATE).build();

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance(Optional<Holder<Block>> block, Optional<StatePropertiesPredicate> state) implements ICriterionTriggerInstanceTester {
		public static final Codec<Instance> CODEC = RecordCodecBuilder.<Instance>create(inst -> inst.group(
				BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("block").forGetter(Instance::block),
				StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(Instance::state)
			).apply(inst, Instance::new)).validate(Instance::checkProperties);

		private static DataResult<Instance> checkProperties(Instance instance) {
			return instance.block.flatMap(block -> instance.state
					.flatMap(state -> state.checkState(block.value().getStateDefinition()))
					.map(property -> DataResult.<Instance>error(() -> "Block " + block + " has no property " + property)))
				.orElseGet(() -> DataResult.success(instance));
		}

		@Override
		public boolean test(LootParams params) {
			BlockState state = params.contextMap().getOrThrow(LootContextParams.BLOCK_STATE);
			if (this.block.isPresent() && !state.is(this.block.get())) {
				return false;
			}
			return this.state.isEmpty() || this.state.get().matches(state);
		}
	}
}
