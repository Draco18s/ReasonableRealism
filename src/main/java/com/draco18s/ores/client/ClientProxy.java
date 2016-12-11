package com.draco18s.ores.client;

import java.awt.Color;
import java.util.HashMap;

import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;
import com.draco18s.hardlib.api.internal.ChunkCoords;
import com.draco18s.hardlib.client.ModelsCache;
import com.draco18s.ores.CommonProxy;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.client.rendering.RenderOreCart;
import com.draco18s.ores.entities.EntityOreMinecart;
import com.draco18s.ores.entities.TileEntityMillstone;
import com.draco18s.ores.networking.ClientOreParticleHandler;
import com.draco18s.ores.networking.ToClientMessageOreParticles;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {
	private HashMap<ChunkCoords, SoundWindmill> sounds = new HashMap<ChunkCoords,SoundWindmill>();
	
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
	
	@Override
	public void handleMessage(final ToClientMessageOreParticles message, MessageContext ctx) {
		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
				//System.out.println(String.format("Received %s from %s", message.oreAt, p.getDisplayName()));
				drawParticle(p.worldObj,getParticle(p.worldObj, message.oreAt, message.eventAt, ClientOreParticleHandler.RADAR, 0));
				drawParticle(p.worldObj,getParticle(p.worldObj, message.oreAt, message.eventAt, ClientOreParticleHandler.DUST, 0));
				drawParticle(p.worldObj,getParticle(p.worldObj, message.oreAt, message.eventAt, ClientOreParticleHandler.DUST, -4));
			}
		});
	}

	@Override
	public void startMillSound(TileEntityMillstone te) {
		if(!OresBase.useSounds) return;
		ChunkCoords tepos = new ChunkCoords(te.getWorld().provider.getDimension(), te.getPos());
		if(!sounds.containsKey(tepos)) {
			SoundWindmill snd = new SoundWindmill(new ResourceLocation("harderores:grain-mill-loop"), SoundCategory.BLOCKS, te);
			OreClientEventHandler.soundsToStart.put(snd, 0);
			//Minecraft.getMinecraft().getSoundHandler().playSound(snd);
			sounds.put(tepos, snd);
		}
		else {
			SoundWindmill snd = sounds.get(tepos);
			if(snd.isDonePlaying()) {
				sounds.remove(tepos);
				snd = new SoundWindmill(new ResourceLocation("harderores:grain-mill-loop"), SoundCategory.BLOCKS, te);
				OreClientEventHandler.soundsToStart.put(snd, 0);
				//Minecraft.getMinecraft().getSoundHandler().playSound(snd);
				sounds.put(tepos, snd);
			}
		}
	}
	
	private static void drawParticle(World worldObj, Particle particle) {
		if(particle != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
	}
	
	public static Particle getParticle(World worldObj, BlockPos oreAt, BlockPos eventAt, int id, int startingAge) {
		Particle particle = null;
		if(id == ClientOreParticleHandler.RADAR) {
			float x, y, z;
			x = ((int)((float)Math.random() * 4f))/5f + 0.1f;
			y = 0.5f + (float)Math.random() * 0.75f;
			z = ((int)((float)Math.random() * 4f))/5f + 0.1f;
			particle = new ProspectorParticle(worldObj, oreAt.getX()+x, oreAt.getY()+y, oreAt.getZ()+z, 0, 0, 0);//3, 20
			IBlockState state = worldObj.getBlockState(oreAt);
			Block block = state.getBlock();
			if(block instanceof IBlockMultiBreak) {
				Color c = ((IBlockMultiBreak)block).getProspectorParticleColor(worldObj, oreAt, state);
				particle.setRBGColorF(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f);
			}
		}
		if(id == ClientOreParticleHandler.DUST) {
			float x, y, z;
			x = ((int)((float)Math.random() * 8f))/10f + 0.1f;
			y = 0.5f + (float)Math.random() * 0.5f;
			z = ((int)((float)Math.random() * 8f))/10f + 0.1f;
			particle = new ProspectorParticleDust(worldObj, eventAt.getX()+x, eventAt.getY()+y, eventAt.getZ()+z, 0, 0, 0,startingAge);//3, 20
			IBlockState state = worldObj.getBlockState(oreAt);
			Block block = state.getBlock();
			if(block instanceof IBlockMultiBreak) {
				Color c = ((IBlockMultiBreak)block).getProspectorParticleColor(worldObj, oreAt, state);
				particle.setRBGColorF(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f);
			}
		}
		return particle;
	}
}
