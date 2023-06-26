package com.draco18s.hardlib.api.advancement;

import com.draco18s.hardlib.HardLib;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class DistanceTraveledTrigger extends HardLibCriteriaTrigger<DistanceTraveledTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation(HardLib.MODID, "distance_traveled");
	public static LootContextParam<Float> DISTANCE = new LootContextParam<Float>(ID);
	public static LootContextParam<TravelType> TRAVEL = new LootContextParam<TravelType>(new ResourceLocation("hardlib", "travel_type"));

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public Instance createInstance(JsonObject json, DeserializationContext context) {
		TravelType t = TravelType.UNKNOWN;
		try {
			t = TravelType.valueOf(GsonHelper.getAsString(json, "travel_type"));
		} catch (IllegalArgumentException e) {
			throw new JsonSyntaxException("Unknown travel type '" + GsonHelper.getAsString(json, "travel_type") + "'");
		}
		float dist = GsonHelper.getAsFloat(json, "distance");
		return new DistanceTraveledTrigger.Instance(dist, t);
	}

	public static class Instance implements ICriterionTriggerInstanceTester {
		private final float distance;
		private final TravelType type;

		public Instance(float distance, TravelType type) {
			this.distance = distance;
			this.type = type;
		}
		
		@Override
		public ResourceLocation getCriterion() {
			return DistanceTraveledTrigger.ID;
		}

		@Override
		public JsonObject serializeToJson(SerializationContext context) {
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("travel_type", type.toString());
			jsonobject.addProperty("distance", distance);
			return jsonobject;
		}

		@Override
		public boolean test(LootContext ctx) {
			return  ctx.getParam(TRAVEL) == type && ctx.getParam(DISTANCE) >= distance;
		}
	}
	
	public enum TravelType {
		UNKNOWN,
		WALK,
		WALK_ON_WATER,
		WALK_UNDER_WATER,
		FLY,
		HORSE,
		PIG,
		BOAT,
		RAIL
	}
}