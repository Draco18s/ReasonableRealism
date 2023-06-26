package com.draco18s.industry.client;

import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.client.gui.ExtHopperGuiContainer;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ExpandedIndustry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
	@SubscribeEvent
	public static void registerClientGuiFactories(final FMLClientSetupEvent event) {
		MenuScreens.register(ExpandedIndustry.ModContainerTypes.machine_wood_hopper, ExtHopperGuiContainer::new);
		MenuScreens.register(ExpandedIndustry.ModContainerTypes.machine_distributor, ExtHopperGuiContainer::new);
		//ScreenManager.registerFactory(ExpandedIndustry.ModContainerTypes.machine_filter, FilterGuiContainer::new);
		//ScreenManager.registerFactory(HarderOres.ModContainerTypes.packager, PackagerGuiContainer::new);
	}
}