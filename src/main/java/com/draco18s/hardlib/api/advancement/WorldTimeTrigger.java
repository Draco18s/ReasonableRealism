package com.draco18s.hardlib.api.advancement;

import com.draco18s.hardlib.HardLib;
import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class WorldTimeTrigger extends HardLibCriteriaTrigger<WorldTimeTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation(HardLib.MODID, "total_world_time");
	public static LootContextParam<Float> TIME = new LootContextParam<Float>(ID);
	public static LootContextParamSet requiredParams = LootContextParamSet.builder().required(WorldTimeTrigger.TIME).build();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public Instance createInstance(JsonObject json, DeserializationContext context) {
		long duration = Long.parseLong(GsonHelper.getAsString(json, "duration"));
		return new WorldTimeTrigger.Instance(duration);
	}

	public static class Instance implements ICriterionTriggerInstanceTester {
		private final long duration;

		public Instance(long dur) {
			this.duration = dur;
		}

		@Override
		public ResourceLocation getCriterion() {
			return WorldTimeTrigger.ID;
		}

		public boolean test(LootContext ctx) {
			return ctx.getParam(TIME) >= duration;
		}

		@Override
		public JsonObject serializeToJson(SerializationContext p_14485_) {
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("duration", duration);
			return jsonobject;
		}
	}
}