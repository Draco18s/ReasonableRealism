package com.draco18s.farming.loot;

import java.util.Random;

import net.minecraft.util.DamageSource;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class KilledByWither implements LootCondition {
	private boolean invert;
	
	public KilledByWither(boolean wasKilled) {
		invert = !wasKilled;
	}
	
	@Override
	public boolean testCondition(Random rand, LootContext context) {
		DamageSource damage = ReflectionHelper.getPrivateValue(LootContext.class, context, "damageSource","field_186478_e");
		if(invert)return (damage == DamageSource.wither);
		else return (damage != DamageSource.wither);
	}
}
