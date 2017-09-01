package com.draco18s.flowers.advancement;

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

public class FoundOreTrigger implements ICriterionTrigger<FoundOreTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation("oreflowers","found_ore");
    private final Map<PlayerAdvancements, FoundOreTrigger.Listeners> listeners = Maps.<PlayerAdvancements, FoundOreTrigger.Listeners>newHashMap();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<FoundOreTrigger.Instance> listener)
    {
		FoundOreTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (consumeitemtrigger$listeners == null)
        {
            consumeitemtrigger$listeners = new FoundOreTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, consumeitemtrigger$listeners);
        }

        consumeitemtrigger$listeners.add(listener);
    }

	@Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<FoundOreTrigger.Instance> listener)
    {
		FoundOreTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

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
	public FoundOreTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		return new FoundOreTrigger.Instance();
	}
	
	public static class Instance extends AbstractCriterionInstance {
		public Instance()
        {
            super(FoundOreTrigger.ID);
        }

		public boolean test(int count)
        {
            return count > 0;
        }
	}
	
	public void trigger(EntityPlayerMP player, int count) {
		FoundOreTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(player.getAdvancements());

        if (enterblocktrigger$listeners != null)
        {
            enterblocktrigger$listeners.trigger(count);
        }
	}
	
	static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<FoundOreTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<FoundOreTrigger.Instance>>newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn)
        {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty()
        {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<FoundOreTrigger.Instance> listener)
        {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<FoundOreTrigger.Instance> listener)
        {
            this.listeners.remove(listener);
        }

        public void trigger(int oreCount)
        {
            List<ICriterionTrigger.Listener<FoundOreTrigger.Instance>> list = null;

            for (ICriterionTrigger.Listener<FoundOreTrigger.Instance> listener : this.listeners)
            {
                if (((FoundOreTrigger.Instance)listener.getCriterionInstance()).test(oreCount))
                {
                    if (list == null)
                    {
                        list = Lists.<ICriterionTrigger.Listener<FoundOreTrigger.Instance>>newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null)
            {
                for (ICriterionTrigger.Listener<FoundOreTrigger.Instance> listener1 : list)
                {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
