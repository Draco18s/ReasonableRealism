package com.draco18s.industry.client;

import com.draco18s.industry.CommonProxy;
import com.draco18s.industry.world.FakeWorld;
import com.draco18s.industry.world.FilterDimension;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	private static World clientFilterWorld;
	
	@Override
	public World getFilterWorld() {
		if(FMLCommonHandler.instance().getMinecraftServerInstance() == null) {
			if(clientFilterWorld == null) {
				if(Minecraft.getMinecraft().world == null) return null;
				clientFilterWorld = new FakeWorld(Minecraft.getMinecraft().world.getWorldInfo(), Minecraft.getMinecraft().mcProfiler);
			}
			return clientFilterWorld;
		}
		else {
			return super.getFilterWorld();
		}
	}
}
