package com.draco18s.industry.client;

import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.client.gui.ExtHopperGuiContainer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ExpandedIndustry.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
	@SubscribeEvent
	public static void registerScreens(final RegisterMenuScreensEvent event) {
		event.register(ExpandedIndustry.ModContainerTypes.machine_wood_hopper, ExtHopperGuiContainer::new);
		event.register(ExpandedIndustry.ModContainerTypes.machine_distributor, ExtHopperGuiContainer::new);
		//ScreenManager.registerFactory(ExpandedIndustry.ModContainerTypes.machine_filter, FilterGuiContainer::new);
		//ScreenManager.registerFactory(HarderOres.ModContainerTypes.packager, PackagerGuiContainer::new);
	}
}