package com.draco18s.harderores.proxy;

import java.util.function.Supplier;

import com.draco18s.hardlib.proxy.IProxy;

import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ServerProxy implements IProxy {

	@Override
	public <MSG> void spawnParticles(MSG message, Supplier<Context> ctx) { }
}
