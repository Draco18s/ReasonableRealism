package com.draco18s.farming.client;

import java.util.List;

import com.draco18s.farming.HarderFarming;

import net.minecraft.client.color.block.BlockTintSources;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

@EventBusSubscriber(modid = HarderFarming.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
	/** The thermometer and hydrometer models pick a frame from this property (see their item models). */
	@SubscribeEvent
	public static void registerItemProperties(RegisterRangeSelectItemModelPropertyEvent event) {
		event.register(HarderFarming.getRL("climate"), ClimateGaugeProperty.MAP_CODEC);
	}

	/** Leaf litter mulch takes the biome's dry foliage color, as leaf litter does (its faces are tint 0). */
	@SubscribeEvent
	public static void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
		event.register(List.of(BlockTintSources.dryFoliage()), HarderFarming.ModBlocks.mulched_farmland);
	}
}
