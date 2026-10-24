package com.draco18s.harderores.network;

import com.draco18s.harderores.HarderOres;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class PacketHandler
{
	public static class EffectsIDs {
		public static int PROSPECTING = 1;
	}
	private static final int PROTOCOL_VERSION = 1;
	private static final SimpleChannel HANDLER = ChannelBuilder
			.named(HarderOres.getRL("main_channel"))
			.networkProtocolVersion(PROTOCOL_VERSION)
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
		HANDLER.send(msg, PacketDistributor.SERVER.noArg());
	}
	
	/**
	 * Send a packet to a specific player.<br>
	 * Must be called Server side. 
	 */
	public static <MSG> void sendTo(MSG msg, ServerPlayer player)
	{
		// (Fake players have no connection; Forge 1.21 dropped its FakePlayer class.)
		if (player.connection != null)
		{
			HANDLER.send(msg, PacketDistributor.PLAYER.with(player));
		}
	}
}