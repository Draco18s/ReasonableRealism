package com.draco18s.farming.loot;

//import com.draco18s.farming.HarderFarming;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public class TemperatureCheck implements LootItemCondition {
	public static final TemperatureCheck INSTANCE = new TemperatureCheck();
	public static final MapCodec<TemperatureCheck> CODEC = MapCodec.unit(INSTANCE);

	private TemperatureCheck() { }

	@Override
	public boolean test(LootContext ctx) {
		ServerLevel serverlevel = ctx.getLevel();
		Vec3 origin = ctx.getOptional(LootContextParams.ORIGIN);
		if(origin == null) return false;
		BlockPos blockpos = BlockPos.containing(origin);
		return serverlevel.getBiome(blockpos).value().coldEnoughToSnow(blockpos, serverlevel.getSeaLevel());
	}

	/** Registered as harderfarming:can_snow (loot condition types are their codecs now). */
	@Override
	public MapCodec<TemperatureCheck> codec() {
		return CODEC;
	}
}
