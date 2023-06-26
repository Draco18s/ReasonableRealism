package com.draco18s.hardlib.proxy;

import java.util.function.Supplier;

import net.minecraftforge.network.NetworkEvent.Context;

public class ClientProxy implements IProxy {

	@Override
	public <MSG> void spawnParticles(MSG message, Supplier<Context> ctx) { }
}
