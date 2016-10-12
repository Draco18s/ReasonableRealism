package com.draco18s.ores.client;

import com.draco18s.ores.OresBase;
import com.draco18s.ores.client.rendering.BakedModelBasicSluice;
import com.draco18s.ores.client.rendering.ModelsCache;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OreClientEventHandler {
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
}
