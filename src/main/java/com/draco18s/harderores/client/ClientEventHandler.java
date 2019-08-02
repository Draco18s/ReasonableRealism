package com.draco18s.harderores.client;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.client.gui.PackagerGuiContainer;
import com.draco18s.harderores.client.gui.SifterGuiContainer;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {
	
	@EventBusSubscriber(modid = HarderOres.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	private static class EventHandlers {
		@SubscribeEvent
		public static void registerClientGuiFactories(final FMLClientSetupEvent event) {
			ScreenManager.registerFactory(HarderOres.ModContainerTypes.sifter, SifterGuiContainer::new);
			ScreenManager.registerFactory(HarderOres.ModContainerTypes.packager, PackagerGuiContainer::new);
		}
	}
}
