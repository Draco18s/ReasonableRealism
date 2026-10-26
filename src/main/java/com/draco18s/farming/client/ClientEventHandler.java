package com.draco18s.farming.client;

import com.draco18s.farming.HarderFarming;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

@EventBusSubscriber(modid = HarderFarming.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
	/** The thermometer and hydrometer models pick a frame from this property (see their item models). */
	@SubscribeEvent
	public static void registerItemProperties(RegisterRangeSelectItemModelPropertyEvent event) {
		event.register(HarderFarming.getRL("climate"), ClimateGaugeProperty.MAP_CODEC);
	}
}
