package com.draco18s.hardlib.api.advancement;

import com.draco18s.harderores.HarderOres;
import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class MillstoneTrigger extends HardLibCriteriaTrigger<MillstoneTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation(HarderOres.MODID,"millstone_grind");
	public static LootContextParam<Float> POWER = new LootContextParam<Float>(ID);

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