package com.draco18s.hardlib.api.advancement;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class MillstoneTrigger extends HardLibCriteriaTrigger<MillstoneTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation("harderores","millstone_grind");
	public static LootContextParam<Float> POWER = new LootContextParam<Float>(ID);
	public static LootContextParamSet requiredParams = LootContextParamSet.builder().required(MillstoneTrigger.POWER).build();

	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public Instance createInstance(JsonObject json, DeserializationContext context) {
		return new MillstoneTrigger.Instance();
	}

	public static class Instance implements ICriterionTriggerInstanceTester {
		public Instance() {	}

		@Override
		public ResourceLocation getCriterion() {
			return MillstoneTrigger.ID;
		}

		@Override
		public JsonObject serializeToJson(SerializationContext context) {
			JsonObject jsonobject = new JsonObject();
			return jsonobject;
		}

		@Override
		public boolean test(LootContext ctx) {
			return ctx.getParam(POWER) > 0;
		}
	}
}