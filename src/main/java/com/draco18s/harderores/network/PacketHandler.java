package com.draco18s.harderores.network;

import com.draco18s.harderores.HarderOres;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler
{
	public static class EffectsIDs {
		public static int PROSPECTING = 1;
	}
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(HarderOres.MODID, "main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public static void register() {
		int disc = 0;
		HANDLER.messageBuilder(ToClientMessageOreParticles.class, disc, NetworkDirection.PLAY_TO_CLIENT)
		.decoder(ToClientMessageOreParticles::decode)
		.encoder(ToClientMessageOreParticles::encode)
		.consumerMainThread(ToClientMessageOreParticles.Handler::handle)
		.add();
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
	public static <MSG> void sendToServer(MSG msg)
	{
		HANDLER.sendToServer(msg);
	}
	
	/**
	 * Send a packet to a specific player.<br>
	 * Must be called Server side. 
	 */
	public static <MSG> void sendTo(MSG msg, ServerPlayer player)
	{
		if (!(player instanceof FakePlayer))
		{
			HANDLER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}