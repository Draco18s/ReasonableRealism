package com.draco18s.ores.networking;

import com.draco18s.ores.OresBase;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientOreParticleHandler implements IMessageHandler<ToClientMessageOreParticles, IMessage> {
	public static int RADAR = 0;
	public static int DUST = 1;
	
	public IMessage onMessage(final ToClientMessageOreParticles message, final MessageContext ctx) {
		OresBase.proxy.handleMessage(message, ctx);
		return null;
	}
}
