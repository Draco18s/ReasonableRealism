package com.draco18s.hardlib.api.advancement;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class WorldTimeTrigger implements ICriterionTrigger<WorldTimeTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation("hardlib", "total_world_time");
	private final Map<PlayerAdvancements, WorldTimeTrigger.Listeners> listeners = Maps.<PlayerAdvancements, WorldTimeTrigger.Listeners>newHashMap();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn,
			ICriterionTrigger.Listener<WorldTimeTrigger.Instance> listener) {
		WorldTimeTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

		if (consumeitemtrigger$listeners == null) {
			consumeitemtrigger$listeners = new WorldTimeTrigger.Listeners(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn, consumeitemtrigger$listeners);
		}

		consumeitemtrigger$listeners.add(listener);
	}

	@Override
	public void removeListener(PlayerAdvancements playerAdvancementsIn,
			ICriterionTrigger.Listener<WorldTimeTrigger.Instance> listener) {
		WorldTimeTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

		if (consumeitemtrigger$listeners != null) {
			consumeitemtrigger$listeners.remove(listener);

			if (consumeitemtrigger$listeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}
	}

	@Override
	public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	@Override
	public WorldTimeTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		long duration = Long.parseLong(JSONUtils.getString(json, "duration"));

		return new WorldTimeTrigger.Instance(duration);
	}

	public static class Instance extends CriterionInstance {
		private final long duration;

		public Instance(long dur) {
			super(WorldTimeTrigger.ID);
			this.duration = dur;
		}

		public boolean test(long time) {
			return time >= duration;
		}
	}

	public void trigger(ServerPlayerEntity player, long f) {
		WorldTimeTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(player.getAdvancements());

		if (enterblocktrigger$listeners != null) {
			enterblocktrigger$listeners.trigger(f);
		}
	}

	static class Listeners {
		private final PlayerAdvancements playerAdvancements;
		private final Set<ICriterionTrigger.Listener<WorldTimeTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<WorldTimeTrigger.Instance>>newHashSet();

		public Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void add(ICriterionTrigger.Listener<WorldTimeTrigger.Instance> listener) {
			this.listeners.add(listener);
		}

		public void remove(ICriterionTrigger.Listener<WorldTimeTrigger.Instance> listener) {
			this.listeners.remove(listener);
		}

		public void trigger(Long time) {
			List<ICriterionTrigger.Listener<WorldTimeTrigger.Instance>> list = null;

			for (ICriterionTrigger.Listener<WorldTimeTrigger.Instance> listener : this.listeners) {
				if (((WorldTimeTrigger.Instance) listener.getCriterionInstance()).test(time)) {
					if (list == null) {
						list = Lists.<ICriterionTrigger.Listener<WorldTimeTrigger.Instance>>newArrayList();
					}

					list.add(listener);
				}
			}

			if (list != null) {
				for (ICriterionTrigger.Listener<WorldTimeTrigger.Instance> listener1 : list) {
					listener1.grantCriterion(this.playerAdvancements);
				}
			}
		}
	}
}