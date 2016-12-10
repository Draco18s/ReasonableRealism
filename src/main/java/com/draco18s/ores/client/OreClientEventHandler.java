package com.draco18s.ores.client;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.draco18s.hardlib.client.ModelsCache;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.client.rendering.BakedModelBasicSluice;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class OreClientEventHandler {
	protected static ConcurrentHashMap<ITickableSound,Integer> soundsToStart = new ConcurrentHashMap<ITickableSound,Integer>();
	
	@SubscribeEvent
	public void modelBaking(ModelBakeEvent ev) {
		ModelResourceLocation resLoc = new ModelResourceLocation(OresBase.sluice.getRegistryName(),"facing=north");
		IBakedModel water = ev.getModelRegistry().getObject(resLoc);
		IBakedModel planking = ModelsCache.INSTANCE.getOrLoadBakedModel(new ModelResourceLocation("harderores:sluice_bottom"));
		
		ev.getModelRegistry().putObject(resLoc, new BakedModelBasicSluice(water,planking));
		resLoc = new ModelResourceLocation(OresBase.sluice.getRegistryName(),"facing=south");
		water = ev.getModelRegistry().getObject(resLoc);
		ev.getModelRegistry().putObject(resLoc, new BakedModelBasicSluice(water,planking));
		resLoc = new ModelResourceLocation(OresBase.sluice.getRegistryName(),"facing=east");
		water = ev.getModelRegistry().getObject(resLoc);
		ev.getModelRegistry().putObject(resLoc, new BakedModelBasicSluice(water,planking));
		resLoc = new ModelResourceLocation(OresBase.sluice.getRegistryName(),"facing=west");
		water = ev.getModelRegistry().getObject(resLoc);
		ev.getModelRegistry().putObject(resLoc, new BakedModelBasicSluice(water,planking));
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.START) {
			Iterator<ITickableSound> it = soundsToStart.keySet().iterator();
			while(it.hasNext()) {
				ITickableSound snd = it.next();
				Minecraft.getMinecraft().getSoundHandler().playSound(snd);
				it.remove();
			}
		}
	}
}
