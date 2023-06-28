package com.draco18s.hardlib.api.advancement;

import com.draco18s.hardlib.HardLib;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.registries.ForgeRegistries;

public class BreakBlockTrigger extends HardLibCriteriaTrigger<BreakBlockTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation(HardLib.MODID, "break_block");
	public static LootContextParamSet requiredParams = LootContextParamSet.builder().required(LootContextParams.BLOCK_STATE).build();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public Instance createInstance(JsonObject json, DeserializationContext context) {
		Block block = deserializeBlock(json);

		StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(json.get("state"));
		if (block != null) {
			statepropertiespredicate.checkState(block.getStateDefinition(), (p_59475_) -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + p_59475_ + ":");
			});
		}

		return new BreakBlockTrigger.Instance(block, statepropertiespredicate);
	}

	public static class Instance implements ICriterionTriggerInstanceTester {
		private final Block block;
		private final StatePropertiesPredicate state;

		public Instance(Block block2, StatePropertiesPredicate map) {
			block = block2;
			state = map;
		}

		@Override
		public ResourceLocation getCriterion() {
			return BreakBlockTrigger.ID;
		}

		@Override
		public JsonObject serializeToJson(SerializationContext context) {
			JsonObject jsonobject = new JsonObject();
			if (this.block != null) {
				jsonobject.addProperty("block", ForgeRegistries.BLOCKS.getKey(this.block).toString());
			}
			jsonobject.add("state", this.state.serializeToJson());
			return jsonobject;
		}

		public boolean test(LootContext ctx) {
			BlockState state = ctx.getParam(LootContextParams.BLOCK_STATE);
			if (this.block != null && !state.is(this.block)) {
				return false;
			} else if (!this.state.matches(state)) {
				return false;
			}
			return true;
		}
	}
}