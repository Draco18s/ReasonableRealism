package com.draco18s.hardlib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.advancement.BreakBlockTrigger;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger.TravelType;
import com.draco18s.hardlib.api.advancement.FoundOreTrigger;
import com.draco18s.hardlib.api.advancement.MillstoneTrigger;
import com.draco18s.hardlib.api.advancement.WorldTimeTrigger;
import com.draco18s.hardlib.api.recipe.RecipeTagOutput;
import com.draco18s.hardlib.proxy.ClientProxy;
import com.draco18s.hardlib.proxy.IProxy;
import com.draco18s.hardlib.proxy.ServerProxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HardLib.MODID)
public class HardLib {
	public static final String MODID = "hardlib";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final IProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	
	public HardLib() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener((FMLCommonSetupEvent event) -> {
			HardLibAPI.Advancements.MILL_BUILT = (MillstoneTrigger) EasyRegistry.registerAdvancementTrigger(new MillstoneTrigger());
			HardLibAPI.Advancements.FOUND_ORE = (FoundOreTrigger) EasyRegistry.registerAdvancementTrigger(new FoundOreTrigger());
			HardLibAPI.Advancements.BLOCK_BREAK = (BreakBlockTrigger) EasyRegistry.registerAdvancementTrigger(new BreakBlockTrigger());
			HardLibAPI.Advancements.WORLD_TIME = (WorldTimeTrigger) EasyRegistry.registerAdvancementTrigger(new WorldTimeTrigger());
			HardLibAPI.Advancements.DISTANCE_TRAVELED = (DistanceTraveledTrigger) EasyRegistry.registerAdvancementTrigger(new DistanceTraveledTrigger());
		});
		
		EasyRegistry.registerOther(new RecipeTagOutput.Serializer(), new ResourceLocation(HardLib.MODID, "tag_output"));
	}
	
	@EventBusSubscriber(modid = HardLib.MODID, bus = EventBusSubscriber.Bus.MOD)
	private static class EventHandlers {
		@SubscribeEvent
		public void onBlockBreak(BlockEvent.BreakEvent event) {
			PlayerEntity player = event.getPlayer();
			if(player instanceof ServerPlayerEntity) {
				HardLibAPI.Advancements.BLOCK_BREAK.trigger((ServerPlayerEntity)player, event.getState());
			}
		}
		
		@SubscribeEvent
		public void onLivingUpdateEvent(PlayerTickEvent event) {
			if(event.phase == Phase.END && event.player instanceof ServerPlayerEntity) {
				ServerStatisticsManager stats = ((ServerPlayerEntity) event.player).getStats();
				int dist = stats.getValue(Stats.CUSTOM.get(Stats.MINECART_ONE_CM));
				HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((ServerPlayerEntity) event.player, dist/100f, TravelType.RAIL);
				dist = stats.getValue(Stats.CUSTOM.get(Stats.BOAT_ONE_CM));
				HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((ServerPlayerEntity) event.player, dist/100f, TravelType.BOAT);
				dist = stats.getValue(Stats.CUSTOM.get(Stats.HORSE_ONE_CM));
				HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((ServerPlayerEntity) event.player, dist/100f, TravelType.HORSE);
				dist = stats.getValue(Stats.CUSTOM.get(Stats.PIG_ONE_CM));
				HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((ServerPlayerEntity) event.player, dist/100f, TravelType.PIG);
				dist = stats.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM));
				HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((ServerPlayerEntity) event.player, dist/100f, TravelType.WALK);
				dist = stats.getValue(Stats.CUSTOM.get(Stats.WALK_ON_WATER_ONE_CM));
				HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((ServerPlayerEntity) event.player, dist/100f, TravelType.WALK_ON_WATER);
				dist = stats.getValue(Stats.CUSTOM.get(Stats.WALK_UNDER_WATER_ONE_CM));
				HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((ServerPlayerEntity) event.player, dist/100f, TravelType.WALK_UNDER_WATER);
				dist = stats.getValue(Stats.CUSTOM.get(Stats.FLY_ONE_CM));
				HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((ServerPlayerEntity) event.player, dist/100f, TravelType.FLY);
			}
		}
		
		@SubscribeEvent
		public void onPlayerTick(PlayerTickEvent event) {
			if(event.phase == Phase.END && event.side == LogicalSide.SERVER) {
				long time = event.player.world.getGameTime();
				if(time % 1000 == 0)
					HardLibAPI.Advancements.WORLD_TIME.trigger((ServerPlayerEntity)event.player, time);
			}
		}
	}
}