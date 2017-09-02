package com.draco18s.hardlib.api.advancement;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class WorldTimeTrigger implements ICriterionTrigger<WorldTimeTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation("harderfarming", "total_world_time");
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
		long duration = Long.parseLong(JsonUtils.getString(json, "duration"));

		return new WorldTimeTrigger.Instance(duration);
	}

	public static class Instance extends AbstractCriterionInstance {
		private final long duration;

		public Instance(long dur) {
			super(WorldTimeTrigger.ID);
			this.duration = dur;
		}

		public boolean test(long time) {
			return time >= duration;
		}
	}

	public void trigger(EntityPlayerMP player, long f) {
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
