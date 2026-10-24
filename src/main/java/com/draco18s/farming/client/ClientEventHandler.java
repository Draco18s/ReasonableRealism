package com.draco18s.farming.client;

import com.draco18s.farming.HarderFarming;
import com.draco18s.farming.item.ClimateGaugeItem;
import com.draco18s.hardlib.HardLib;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = HarderFarming.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			// The thermometer and hydrometer models pick a frame from the "time" property.
			registerGauge(HarderFarming.ModItems.thermometer);
			registerGauge(HarderFarming.ModItems.hydrometer);
		});
	}

	private static void registerGauge(Item item) {
		ClimateGaugeItem gauge = (ClimateGaugeItem)item;
		ItemProperties.register(item, HardLib.parseRL("time"), (stack, level, entity, seed) -> gauge.getFrame(stack, level, entity));
	}
}
