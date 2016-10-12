package com.draco18s.ores.client;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.draco18s.ores.CommonProxy;
import com.draco18s.ores.OreEventHandler;
import com.draco18s.ores.client.rendering.ModelsCache;
import com.draco18s.ores.client.rendering.RenderOreCart;
import com.draco18s.ores.entities.EntityOreMinecart;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityOreMinecart.class,
			new IRenderFactory() {
				@Override
				public Render createRenderFor(RenderManager manager) {
					return new RenderOreCart(manager);
				};
			} 
		);
	}
	
	@Override
	public void registerEventHandlers() {
		super.registerEventHandlers();
		OreClientEventHandler handler = new OreClientEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager() ).registerReloadListener(ModelsCache.INSTANCE);
	}
}
