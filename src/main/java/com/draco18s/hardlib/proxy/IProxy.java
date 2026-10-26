package com.draco18s.hardlib.proxy;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IProxy {
	default <MSG> void spawnParticles(MSG message, IPayloadContext ctx) { }
	default void ClientRegistration() { }
}
