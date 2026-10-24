package com.draco18s.hardlib;

import java.util.Map;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.advancement.AdvancementEarnedTrigger;
import com.draco18s.hardlib.api.advancement.BreakBlockTrigger;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger.TravelType;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger;
import com.draco18s.hardlib.api.advancement.WorldTimeTrigger;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/** Fires HardLib's block, time, distance and advancement-earned triggers. */
@EventBusSubscriber(modid = HardLib.MODID)
public class AdvancementEvents {
	/** How often (in ticks) each player's travel statistics are checked. */
	private static final int DISTANCE_INTERVAL = 20;
	/** How often (in world ticks) the world time is checked, as 1.14 did. */
	private static final int TIME_INTERVAL = 1000;

	@SubscribeEvent
	public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
		if(event.getEntity() instanceof ServerPlayer player) {
			triggerEarned(player, event.getAdvancement());
		}
	}

	/**
	 * An advancement_earned criterion counts only while the player still has that advancement: if it's revoked (by a
	 * command), the criteria waiting on it are revoked too, which un-earns their advancements (and so on down).
	 */
	@SubscribeEvent
	public static void onAdvancementProgress(AdvancementEvent.AdvancementProgressEvent event) {
		if(event.getProgressType() != AdvancementEvent.AdvancementProgressEvent.ProgressType.REVOKE) return;
		if(!(event.getEntity() instanceof ServerPlayer player) || event.getAdvancementProgress().isDone()) return;
		ResourceLocation revoked = event.getAdvancement().id();
		for(AdvancementHolder advancement : player.server.getAdvancements().getAllAdvancements()) {
			for(Map.Entry<String, Criterion<?>> criterion : advancement.value().criteria().entrySet()) {
				if(criterion.getValue().triggerInstance() instanceof AdvancementEarnedTrigger.Instance earned && revoked.equals(earned.advancement())) {
					CriterionProgress progress = player.getAdvancements().getOrStartProgress(advancement).getCriterion(criterion.getKey());
					if(progress != null && progress.isDone()) {
						player.getAdvancements().revoke(advancement, criterion.getKey());
					}
				}
			}
		}
	}

	/** Advancements earned before an advancement_earned criterion existed (or on another version) still count. */
	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if(!(event.getEntity() instanceof ServerPlayer player) || HardLibAPI.Advancements.ADVANCEMENT_EARNED == null) return;
		if(!HardLibAPI.Advancements.ADVANCEMENT_EARNED.hasListeners(player)) return;
		for(AdvancementHolder advancement : player.server.getAdvancements().getAllAdvancements()) {
			if(player.getAdvancements().getOrStartProgress(advancement).isDone()) {
				triggerEarned(player, advancement);
			}
		}
	}

	private static void triggerEarned(ServerPlayer player, AdvancementHolder advancement) {
		if(HardLibAPI.Advancements.ADVANCEMENT_EARNED == null || !HardLibAPI.Advancements.ADVANCEMENT_EARNED.hasListeners(player)) return;
		LootParams ctx = new LootParams.Builder(player.serverLevel())
				.withParameter(AdvancementEarnedTrigger.ADVANCEMENT, advancement.id())
				.create(AdvancementEarnedTrigger.requiredParams);
		HardLibAPI.Advancements.ADVANCEMENT_EARNED.trigger(player, ctx);
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if(!(event.getPlayer() instanceof ServerPlayer player) || HardLibAPI.Advancements.BLOCK_BREAK == null) return;
		LootParams ctx = new LootParams.Builder(player.serverLevel())
				.withParameter(LootContextParams.BLOCK_STATE, event.getState())
				.create(BreakBlockTrigger.requiredParams);
		HardLibAPI.Advancements.BLOCK_BREAK.trigger(player, ctx);
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
		long time = player.serverLevel().getGameTime();
		if(time % TIME_INTERVAL == 0 && HardLibAPI.Advancements.WORLD_TIME != null) {
			LootParams ctx = new LootParams.Builder(player.serverLevel())
					.withParameter(WorldTimeTrigger.TIME, (float)time)
					.create(WorldTimeTrigger.requiredParams);
			HardLibAPI.Advancements.WORLD_TIME.trigger(player, ctx);
		}
		if(player.tickCount % DISTANCE_INTERVAL == 0 && HardLibAPI.Advancements.DISTANCE_TRAVELED != null) {
			ServerStatsCounter stats = player.getStats();
			triggerDistance(player, stats, Stats.MINECART_ONE_CM, TravelType.RAIL);
			triggerDistance(player, stats, Stats.BOAT_ONE_CM, TravelType.BOAT);
			triggerDistance(player, stats, Stats.HORSE_ONE_CM, TravelType.HORSE);
			triggerDistance(player, stats, Stats.PIG_ONE_CM, TravelType.PIG);
			triggerDistance(player, stats, Stats.WALK_ONE_CM, TravelType.WALK);
			triggerDistance(player, stats, Stats.WALK_ON_WATER_ONE_CM, TravelType.WALK_ON_WATER);
			triggerDistance(player, stats, Stats.WALK_UNDER_WATER_ONE_CM, TravelType.WALK_UNDER_WATER);
			triggerDistance(player, stats, Stats.FLY_ONE_CM, TravelType.FLY);
		}
	}

	/** The statistic is in centimeters; the trigger's distance is in blocks. */
	private static void triggerDistance(ServerPlayer player, ServerStatsCounter stats, ResourceLocation stat, TravelType type) {
		LootParams ctx = new LootParams.Builder(player.serverLevel())
				.withParameter(DistanceTraveledTrigger.DISTANCE, stats.getValue(Stats.CUSTOM.get(stat)) / 100f)
				.withParameter(DistanceTraveledTrigger.TRAVEL, type)
				.create(DistanceTraveledTrigger.requiredParams);
		HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger(player, ctx);
	}
}
