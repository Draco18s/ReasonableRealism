package com.draco18s.harderores.network;


import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class PacketHandler
{
	public static class EffectsIDs {
		public static int PROSPECTING = 1;
	}
	private static final String PROTOCOL_VERSION = "1";

	/** Called from RegisterPayloadHandlersEvent (on Harder Ores' mod bus). */
	public static void register(RegisterPayloadHandlersEvent event) {
		event.registrar(PROTOCOL_VERSION)
		.playToClient(ToClientMessageOreParticles.TYPE, ToClientMessageOreParticles.STREAM_CODEC, ToClientMessageOreParticles.Handler::handle);
		//HANDLER.registerMessage(disc++, ToClientMessageOreParticles.class, ToClientMessageOreParticles::encode, ToClientMessageOreParticles::decode, ToClientMessageOreParticles.Handler::handle);
	}

	/*

	public static void sendNonLocal(IMessage msg, EntityPlayerMP player)
	{
		if (player.server.isDedicatedServer() || !player.getName().equals(player.server.getServerOwner()))
		{
			HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}*/

	/**
	 * Sends a packet to the server.<br>
	 * Must be called Client side. 
	 */
	public static void sendToServer(CustomPacketPayload msg)
	{
		PacketDistributor.sendToServer(msg);
	}
	
	/**
	 * Send a packet to a specific player.<br>
	 * Must be called Server side. 
	 */
	public static void sendTo(CustomPacketPayload msg, ServerPlayer player)
	{
		// (Fake players have no connection.)
		if (player.connection != null)
		{
			PacketDistributor.sendToPlayer(player, msg);
		}
	}
}