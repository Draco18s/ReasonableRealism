package com.draco18s.hardlib.api.advancement;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.level.storage.loot.LootContext;

public interface ICriterionTriggerInstanceTester extends CriterionTriggerInstance {
	// I can just hijack the lootcontext and lootcontextparams to pass the relevant values
	// Then the generic class doesn't need to know about them
	boolean test(LootContext ctx);
}
