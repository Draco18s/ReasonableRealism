package com.draco18s.harderfarming.loot;

import com.draco18s.farming.HarderFarming;
//import com.draco18s.farming.HarderFarming;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class TemperatureCheck implements LootItemCondition {

	public TemperatureCheck() { }

	@Override
	public boolean test(LootContext ctx) {
		ServerLevel serverlevel = ctx.getLevel();
		BlockPos blockpos = BlockPos.containing(ctx.getParam(LootContextParams.ORIGIN));
		return serverlevel.getBiome(blockpos).get().coldEnoughToSnow(blockpos);
	}

	@Override
	public LootItemConditionType getType() {
		return HarderFarming.ModLootConditionTypes.can_snow.get();
	}
	
	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<TemperatureCheck> {
		@Override
		public void serialize(JsonObject p_79325_, TemperatureCheck p_79326_, JsonSerializationContext p_79327_) { }

		@Override
		public TemperatureCheck deserialize(JsonObject p_79323_, JsonDeserializationContext p_79324_) {
			return new TemperatureCheck();
		}
	}
}
