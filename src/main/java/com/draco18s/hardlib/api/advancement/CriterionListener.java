package com.draco18s.hardlib.api.advancement;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.world.level.storage.loot.LootContext;

public class CriterionListener<T extends ICriterionTriggerInstanceTester>  {
	private final PlayerAdvancements playerAdvancements;
	private final Set<CriterionTrigger.Listener<T>> listeners = Sets.<CriterionTrigger.Listener<T>>newHashSet();

	public CriterionListener(PlayerAdvancements playerAdvancementsIn) {
		this.playerAdvancements = playerAdvancementsIn;
	}

	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}

	public void add(CriterionTrigger.Listener<T> listener) {
		this.listeners.add(listener);
	}

	public void remove(CriterionTrigger.Listener<T> listener) {
		this.listeners.remove(listener);
	}
	
	public void trigger(LootContext ctx) {
		for (CriterionTrigger.Listener<T> listener : this.listeners) {
			if(listener.getTriggerInstance().test(ctx)) {
				listener.run(playerAdvancements);
			}
		}
	}
}
