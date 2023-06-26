package com.draco18s.hardlib.proxy;

import java.util.function.Supplier;

import net.minecraftforge.network.NetworkEvent.Context;

public interface IProxy {
	default <MSG> void spawnParticles(MSG message, Supplier<Context> ctx) { }
	default void ClientRegistration() { }
}
