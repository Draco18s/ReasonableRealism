package com.draco18s.hardlib.api.advancement;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootParams;

public abstract class HardLibCriteriaTrigger<T extends ICriterionTriggerInstanceTester> implements CriterionTrigger<T> {
	protected final Map<PlayerAdvancements, CriterionListener<T>> listeners = Maps.<PlayerAdvancements, CriterionListener<T>>newHashMap();

	@Override
	public final void addPlayerListener(PlayerAdvancements playerAdvancementsIn, CriterionTrigger.Listener<T> listener) {
		CriterionListener<T> consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);
	
		if (consumeitemtrigger$listeners == null) {
			consumeitemtrigger$listeners = new CriterionListener<T>(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn, consumeitemtrigger$listeners);
		}
	
		consumeitemtrigger$listeners.add(listener);
	}

	@Override
	public final void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
		CriterionListener<T> consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);
	
		if (consumeitemtrigger$listeners != null) {
			consumeitemtrigger$listeners.remove(listener);
	
			if (consumeitemtrigger$listeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}
	}

	@Override
	public final void removePlayerListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	/** True if any of the player's unfinished advancements use this trigger (worth building a context for). */
	public final boolean hasListeners(ServerPlayer player) {
		return listeners.containsKey(player.getAdvancements());
	}

	public final void trigger(ServerPlayer player, LootParams params) {
		CriterionListener<T> enterblocktrigger$listeners = listeners.get(player.getAdvancements());

		if (enterblocktrigger$listeners != null) {
			enterblocktrigger$listeners.trigger(params);
		}
	}
}