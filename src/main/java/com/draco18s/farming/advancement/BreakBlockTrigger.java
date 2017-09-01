package com.draco18s.farming.advancement;

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

public class BreakBlockTrigger implements ICriterionTrigger<BreakBlockTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation("harderfarming","break_block");
    private final Map<PlayerAdvancements, BreakBlockTrigger.Listeners> listeners = Maps.<PlayerAdvancements, BreakBlockTrigger.Listeners>newHashMap();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<BreakBlockTrigger.Instance> listener)
    {
		BreakBlockTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (consumeitemtrigger$listeners == null)
        {
            consumeitemtrigger$listeners = new BreakBlockTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, consumeitemtrigger$listeners);
        }

        consumeitemtrigger$listeners.add(listener);
    }

	@Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<BreakBlockTrigger.Instance> listener)
    {
		BreakBlockTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

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
	public BreakBlockTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		Block block = null;

        if (json.has("block"))
        {
            ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(json, "block"));

            if (!Block.REGISTRY.containsKey(resourcelocation))
            {
                throw new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
            }

            block = Block.REGISTRY.getObject(resourcelocation);
        }

        Map < IProperty<?>, Object > map = null;

        if (json.has("state"))
        {
            if (block == null)
            {
                throw new JsonSyntaxException("Can't define block state without a specific block type");
            }

            BlockStateContainer blockstatecontainer = block.getBlockState();

            for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "state").entrySet())
            {
                IProperty<?> iproperty = blockstatecontainer.getProperty(entry.getKey());

                if (iproperty == null)
                {
                    throw new JsonSyntaxException("Unknown block state property '" + (String)entry.getKey() + "' for block '" + Block.REGISTRY.getNameForObject(block) + "'");
                }

                String s = JsonUtils.getString(entry.getValue(), entry.getKey());
                Optional<?> optional = iproperty.parseValue(s);

                if (!optional.isPresent())
                {
                    throw new JsonSyntaxException("Invalid block state value '" + s + "' for property '" + (String)entry.getKey() + "' on block '" + Block.REGISTRY.getNameForObject(block) + "'");
                }

                if (map == null)
                {
                    map = Maps. < IProperty<?>, Object > newHashMap();
                }

                map.put(iproperty, optional.get());
            }
        }
		
		return new BreakBlockTrigger.Instance(block, map);
	}
	
	public static class Instance extends AbstractCriterionInstance {
        private final Block block;
        private final Map < IProperty<?>, Object > properties;
        
		public Instance(@Nullable Block blockIn, @Nullable Map < IProperty<?>, Object > propertiesIn)
        {
            super(BreakBlockTrigger.ID);
            this.block = blockIn;
            this.properties = propertiesIn;
        }

		public boolean test(IBlockState state)
        {
			if (this.block != null && state.getBlock() != this.block)
            {
                return false;
            }
            else
            {
                if (this.properties != null)
                {
                    for (Entry < IProperty<?>, Object > entry : this.properties.entrySet())
                    {
                        if (state.getValue(entry.getKey()) != entry.getValue())
                        {
                            return false;
                        }
                    }
                }

                return true;
            }
        }
	}
	
	public void trigger(EntityPlayerMP player, IBlockState f) {
		BreakBlockTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(player.getAdvancements());

        if (enterblocktrigger$listeners != null)
        {
            enterblocktrigger$listeners.trigger(f);
        }
	}
	
	static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<BreakBlockTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<BreakBlockTrigger.Instance>>newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn)
        {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty()
        {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<BreakBlockTrigger.Instance> listener)
        {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<BreakBlockTrigger.Instance> listener)
        {
            this.listeners.remove(listener);
        }

        public void trigger(IBlockState state)
        {
            List<ICriterionTrigger.Listener<BreakBlockTrigger.Instance>> list = null;

            for (ICriterionTrigger.Listener<BreakBlockTrigger.Instance> listener : this.listeners)
            {
                if (((BreakBlockTrigger.Instance)listener.getCriterionInstance()).test(state))
                {
                    if (list == null)
                    {
                        list = Lists.<ICriterionTrigger.Listener<BreakBlockTrigger.Instance>>newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null)
            {
                for (ICriterionTrigger.Listener<BreakBlockTrigger.Instance> listener1 : list)
                {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
