package com.draco18s.harderfarming.loot;

import com.draco18s.harderfarming.HarderFarming;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class TemperatureCheck implements ILootCondition {
	private final float minTemp;
	private final float maxTemp;

	private TemperatureCheck(float min, float max) {
		minTemp = min;
		maxTemp = max; 
	}

	public boolean test(LootContext ctx) {
		BlockPos pos = ctx.get(LootParameters.POSITION);
		float temp = ctx.getWorld().getBiome(pos).getTemperature(pos);
		return minTemp <= temp && temp <= maxTemp;
	}

	public static ILootCondition.IBuilder builder(float min, float max) {
		return () -> {
			return new TemperatureCheck(min, max);
		};
	}

	public static class Serializer extends ILootCondition.AbstractSerializer<TemperatureCheck> {
		public Serializer() {
			super(new ResourceLocation(HarderFarming.MODID,"biome_temperature"), TemperatureCheck.class);
		}

		public void serialize(JsonObject json, TemperatureCheck value, JsonSerializationContext context) {
			json.addProperty("min", value.minTemp);
			json.addProperty("max", value.maxTemp);
		}

		public TemperatureCheck deserialize(JsonObject json, JsonDeserializationContext context) {
			return new TemperatureCheck(JSONUtils.getFloat(json, "min"),JSONUtils.getFloat(json, "max"));
		}
	}
}