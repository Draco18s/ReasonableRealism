package com.draco18s.ores;

import java.lang.reflect.InvocationTargetException;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.blockproperties.ores.EnumOreType;
import com.draco18s.ores.networking.ClientOreParticleHandler;
import com.draco18s.ores.networking.ServerOreCartHandler;
import com.draco18s.ores.networking.ToClientMessageOreParticles;
import com.draco18s.ores.networking.ToServerMessageOreCart;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
	protected byte serverMessageID = 1;
	protected byte clientMessageID = 2;
	public void registerRenderers() {
		
	}
	
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new OreEventHandler());
	}

	public void registerNetwork() {
		OresBase.networkWrapper.registerMessage(ServerOreCartHandler.class, ToServerMessageOreCart.class, serverMessageID, Side.SERVER);
	}
}
