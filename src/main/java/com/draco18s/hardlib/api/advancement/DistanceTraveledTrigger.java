package com.draco18s.hardlib.api.advancement;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class DistanceTraveledTrigger implements ICriterionTrigger<DistanceTraveledTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation("expindustry", "distance_traveled");
	private final Map<PlayerAdvancements, DistanceTraveledTrigger.Listeners> listeners = Maps.<PlayerAdvancements, DistanceTraveledTrigger.Listeners>newHashMap();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<DistanceTraveledTrigger.Instance> listener) {
		DistanceTraveledTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

		if (consumeitemtrigger$listeners == null) {
			consumeitemtrigger$listeners = new DistanceTraveledTrigger.Listeners(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn, consumeitemtrigger$listeners);
		}

		consumeitemtrigger$listeners.add(listener);
	}

	@Override
	public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<DistanceTraveledTrigger.Instance> listener) {
		DistanceTraveledTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

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
	public DistanceTraveledTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		TravelType t = TravelType.UNKNOWN;
		try {
			t = TravelType.valueOf(JsonUtils.getString(json, "travel_type"));
		} catch (IllegalArgumentException e) {
			throw new JsonSyntaxException("Unknown travel type '" + JsonUtils.getString(json, "travel_type") + "'");
		}
		float dist = JsonUtils.getFloat(json, "distance");
		return new DistanceTraveledTrigger.Instance(dist, t);
	}

	public static class Instance extends AbstractCriterionInstance {
		private final float distance;
		private final TravelType type;

		public Instance(float distance, TravelType type) {
			super(DistanceTraveledTrigger.ID);
			this.distance = distance;
			this.type = type;
		}

		public boolean test(float dist, TravelType type) {
			return this.type == type && dist >= distance;
		}
	}

	public void trigger(EntityPlayerMP player, float f, TravelType t) {
		DistanceTraveledTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(player.getAdvancements());

		if (enterblocktrigger$listeners != null) {
			enterblocktrigger$listeners.trigger(f, t);
		}
	}

	static class Listeners {
		private final PlayerAdvancements playerAdvancements;
		private final Set<ICriterionTrigger.Listener<DistanceTraveledTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<DistanceTraveledTrigger.Instance>>newHashSet();

		public Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void add(ICriterionTrigger.Listener<DistanceTraveledTrigger.Instance> listener) {
			this.listeners.add(listener);
		}

		public void remove(ICriterionTrigger.Listener<DistanceTraveledTrigger.Instance> listener) {
			this.listeners.remove(listener);
		}

		public void trigger(float distance, TravelType type) {
			List<ICriterionTrigger.Listener<DistanceTraveledTrigger.Instance>> list = null;

			for (ICriterionTrigger.Listener<DistanceTraveledTrigger.Instance> listener : this.listeners) {
				if (((DistanceTraveledTrigger.Instance) listener.getCriterionInstance()).test(distance, type)) {
					if (list == null) {
						list = Lists.<ICriterionTrigger.Listener<DistanceTraveledTrigger.Instance>>newArrayList();
					}

					list.add(listener);
				}
			}

			if (list != null) {
				for (ICriterionTrigger.Listener<DistanceTraveledTrigger.Instance> listener1 : list) {
					listener1.grantCriterion(this.playerAdvancements);
				}
			}
		}
	}
	
	public enum TravelType {
		UNKNOWN,
		WALK,
		FLY,
		HORSE,
		PIG,
		BOAT,
		RAIL
	}
}
