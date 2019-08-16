package com.draco18s.industry.client;

import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.client.gui.ExtHopperGuiContainer;
import com.draco18s.industry.client.gui.FilterGuiContainer;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {
	
	@EventBusSubscriber(modid = ExpandedIndustry.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	private static class EventHandlers {
		@SubscribeEvent
		public static void registerClientGuiFactories(final FMLClientSetupEvent event) {
			ScreenManager.registerFactory(ExpandedIndustry.ModContainerTypes.machine_wood_hopper, ExtHopperGuiContainer::new);
			ScreenManager.registerFactory(ExpandedIndustry.ModContainerTypes.machine_distributor, ExtHopperGuiContainer::new);
			ScreenManager.registerFactory(ExpandedIndustry.ModContainerTypes.machine_filter, FilterGuiContainer::new);
			//ScreenManager.registerFactory(HarderOres.ModContainerTypes.packager, PackagerGuiContainer::new);
		}
	}
}