package com.draco18s.industry.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {
	private static World clientFilterWorld;
	
	public World getFilterWorld(World world) {
		if(world.getServer() == null) {
			if(clientFilterWorld == null) {
				if(Minecraft.getInstance().world == null) return null;
				clientFilterWorld = new FakeWorld(Minecraft.getInstance().world.getWorldInfo(), Minecraft.getInstance().getProfiler());
				//clientFilterWorld = new FakeWorld(Minecraft.getInstance().world.getWorldInfo(), Minecraft.getInstance().getProfiler());
			}
			return clientFilterWorld;
		}
		return null;
	}
}
