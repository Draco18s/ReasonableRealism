package com.draco18s.hardlib.proxy;

import java.util.function.Supplier;

import net.minecraftforge.fml.network.NetworkEvent.Context;

public interface IProxy {
	<MSG> void spawnParticles(MSG message, Supplier<Context> ctx);
}
