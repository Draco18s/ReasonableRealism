package com.draco18s.hardlib.api.advancement;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.registries.ForgeRegistries;

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

	public final void trigger(ServerPlayer player, LootContext ctx) {
		CriterionListener<T> enterblocktrigger$listeners = listeners.get(player.getAdvancements());

		if (enterblocktrigger$listeners != null) {
			enterblocktrigger$listeners.trigger(ctx);
		}
	}

	@Nullable
	public static Block deserializeBlock(JsonObject json) {
		if (json.has("block")) {
			ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, "block"));
			return ForgeRegistries.BLOCKS.getValue(resourcelocation);
		} else {
			return null;
		}
	}
}