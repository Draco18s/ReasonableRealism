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
import net.minecraft.util.ResourceLocation;

public class MillstoneTrigger implements ICriterionTrigger<MillstoneTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation("harderores","millstone_grind");
    private final Map<PlayerAdvancements, MillstoneTrigger.Listeners> listeners = Maps.<PlayerAdvancements, MillstoneTrigger.Listeners>newHashMap();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<MillstoneTrigger.Instance> listener)
    {
		MillstoneTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (consumeitemtrigger$listeners == null)
        {
            consumeitemtrigger$listeners = new MillstoneTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, consumeitemtrigger$listeners);
        }

        consumeitemtrigger$listeners.add(listener);
    }

	@Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<MillstoneTrigger.Instance> listener)
    {
		MillstoneTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (consumeitemtrigger$listeners != null)
        {
            consumeitemtrigger$listeners.remove(listener);

            if (consumeitemtrigger$listeners.isEmpty())
            {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

	@Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
    {
        this.listeners.remove(playerAdvancementsIn);
    }

	@Override
	public MillstoneTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		return new MillstoneTrigger.Instance();
	}
	
	public static class Instance extends CriterionInstance {
		public Instance()
        {
            super(MillstoneTrigger.ID);
        }

		public boolean test(float power)
        {
            return power > 0;
        }
	}
	
	public void trigger(ServerPlayerEntity player, float f) {
		MillstoneTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(player.getAdvancements());

        if (enterblocktrigger$listeners != null)
        {
            enterblocktrigger$listeners.trigger(f);
        }
	}
	
	static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<MillstoneTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<MillstoneTrigger.Instance>>newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn)
        {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty()
        {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<MillstoneTrigger.Instance> listener)
        {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<MillstoneTrigger.Instance> listener)
        {
            this.listeners.remove(listener);
        }

        public void trigger(float power)
        {
            List<ICriterionTrigger.Listener<MillstoneTrigger.Instance>> list = null;

            for (ICriterionTrigger.Listener<MillstoneTrigger.Instance> listener : this.listeners)
            {
                if (((MillstoneTrigger.Instance)listener.getCriterionInstance()).test(power))
                {
                    if (list == null)
                    {
                        list = Lists.<ICriterionTrigger.Listener<MillstoneTrigger.Instance>>newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null)
            {
                for (ICriterionTrigger.Listener<MillstoneTrigger.Instance> listener1 : list)
                {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}