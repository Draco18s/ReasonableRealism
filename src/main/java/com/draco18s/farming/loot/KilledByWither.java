package com.draco18s.farming.loot;

import java.util.Random;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.FarmingBase;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.util.DamageSource;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class KilledByWither implements LootCondition {
	private boolean invert;
	
	public KilledByWither(boolean wasKilled) {
		invert = !wasKilled;
	}
	
	@Override
	public boolean testCondition(Random rand, LootContext context) {
		DamageSource damage = ReflectionHelper.getPrivateValue(LootContext.class, context, "damageSource","field_186503_f");
		if(invert)return (damage == DamageSource.WITHER);
		else return (damage != DamageSource.WITHER);
	}
	
	public static class Serializer extends LootCondition.Serializer<KilledByWither> {

		public Serializer() {
			super(new ResourceLocation("killed_by_wither_damage"), KilledByWither.class);
		}

		@Override
		public void serialize(JsonObject json, KilledByWither value, JsonSerializationContext context) {
			json.addProperty("invert", value.invert);
		}

		@Override
		public KilledByWither deserialize(JsonObject json, JsonDeserializationContext context) {
			return new KilledByWither(JsonUtils.getBoolean(json, "invert"));
		}
    }
}
