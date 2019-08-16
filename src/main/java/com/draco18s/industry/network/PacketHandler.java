package com.draco18s.industry.network;

import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class PacketHandler
{
	public static class EffectsIDs {
		public static int PROSPECTING = 1;
	}
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(ExpandedIndustry.MODID, "main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public static void register() {
		int disc = 0;

		HANDLER.registerMessage(disc++, ToServerFilterClick.class, ToServerFilterClick::encode, ToServerFilterClick::decode, ToServerFilterClick.Handler::handle);
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
	public static <MSG> void sendTo(MSG msg, ServerPlayerEntity player)
	{
		if (!(player instanceof FakePlayer))
		{
			HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}