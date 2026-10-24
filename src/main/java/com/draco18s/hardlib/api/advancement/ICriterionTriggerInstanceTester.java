package com.draco18s.hardlib.api.advancement;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.world.level.storage.loot.LootParams;

public interface ICriterionTriggerInstanceTester extends CriterionTriggerInstance {
	// I can just hijack the loot params to pass the relevant values
	// Then the generic class doesn't need to know about them
	boolean test(LootParams params);

	@Override
	default void validate(CriterionValidator validator) { }
}
