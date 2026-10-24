package com.draco18s.hardlib.proxy;

import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public interface IProxy {
	default <MSG> void spawnParticles(MSG message, Context ctx) { }
	default void ClientRegistration() { }
}
