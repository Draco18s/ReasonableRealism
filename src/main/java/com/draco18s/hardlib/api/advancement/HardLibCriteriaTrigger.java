package com.draco18s.hardlib.api.advancement;

import java.util.List;
import java.util.Map;

import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootParams;

/**
 * A criterion trigger whose instances test loot params (see ICriterionTriggerInstanceTester). The player's
 * advancements keep track of which unfinished criteria use the trigger.
 */
public abstract class HardLibCriteriaTrigger<T extends ICriterionTriggerInstanceTester> implements CriterionTrigger<T> {
	/** True if any of the player's unfinished advancements use this trigger (worth building a context for). */
	public final boolean hasListeners(ServerPlayer player) {
		Map<PlayerAdvancements.TriggerInstanceKey, T> listeners = player.getAdvancements().getTriggerMapForType(this);
		return listeners != null && !listeners.isEmpty();
	}

	public final void trigger(ServerPlayer player, LootParams params) {
		PlayerAdvancements advancements = player.getAdvancements();
		Map<PlayerAdvancements.TriggerInstanceKey, T> listeners = advancements.getTriggerMapForType(this);
		if(listeners == null || listeners.isEmpty()) return;
		// Collected first: completing a criterion unregisters it, which would break the iteration.
		List<PlayerAdvancements.TriggerInstanceKey> met = listeners.entrySet().stream()
				.filter(entry -> entry.getValue().test(params)).map(Map.Entry::getKey).toList();
		for(PlayerAdvancements.TriggerInstanceKey criterion : met) {
			advancements.award(criterion.advancement(), criterion.criterion());
		}
	}
}
