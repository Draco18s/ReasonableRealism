package com.draco18s.farming.loot;

import com.draco18s.farming.HarderFarming;
import com.mojang.serialization.MapCodec;
//import com.draco18s.farming.HarderFarming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class TemperatureCheck implements LootItemCondition {
	public static final TemperatureCheck INSTANCE = new TemperatureCheck();
	public static final MapCodec<TemperatureCheck> CODEC = MapCodec.unit(INSTANCE);

	private TemperatureCheck() { }

	@Override
	public boolean test(LootContext ctx) {
		ServerLevel serverlevel = ctx.getLevel();
		BlockPos blockpos = BlockPos.containing(ctx.getParam(LootContextParams.ORIGIN));
		return serverlevel.getBiome(blockpos).value().coldEnoughToSnow(blockpos);
	}

	@Override
	public LootItemConditionType getType() {
		return HarderFarming.ModLootConditionTypes.can_snow.get();
	}
}
