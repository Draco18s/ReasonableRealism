package com.draco18s.hardlib.proxy;

import java.util.function.Supplier;

import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ServerProxy implements IProxy {

	@Override
	public <MSG> void spawnParticles(MSG message, Supplier<Context> ctx) { }
}
