package com.draco18s.ores;

import java.lang.reflect.InvocationTargetException;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.blockproperties.ores.EnumOreType;
import com.draco18s.ores.entities.TileEntityMillstone;
import com.draco18s.ores.networking.ClientOreParticleHandler;
import com.draco18s.ores.networking.ServerOreCartHandler;
import com.draco18s.ores.networking.ToClientMessageOreParticles;
import com.draco18s.ores.networking.ToServerMessageOreCart;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
	public void registerRenderers() {
		
	}
	
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new OreEventHandler());
	}

	public void handleMessage(ToClientMessageOreParticles message, MessageContext ctx) {
		
	}

	public void startMillSound(TileEntityMillstone te) {
		
	}
}
