package com.draco18s.hardlib.api.advancement;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class FoundOreTrigger extends HardLibCriteriaTrigger<FoundOreTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation("oreflowers","found_ore");
	public static LootContextParam<Integer> ORE_COUNT = new LootContextParam<Integer>(ID);

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public Instance createInstance(JsonObject json, DeserializationContext context) {
		return new FoundOreTrigger.Instance();
	}
	
	public static class Instance implements ICriterionTriggerInstanceTester {
		@Override
		public ResourceLocation getCriterion() {
			return FoundOreTrigger.ID;
		}

		@Override
		public JsonObject serializeToJson(SerializationContext p_14485_) {
			JsonObject jsonobject = new JsonObject();
			return jsonobject;
		}

		@Override
		public boolean test(LootContext ctx) {
			return ctx.getParam(ORE_COUNT) > 0;
		}
	}
}