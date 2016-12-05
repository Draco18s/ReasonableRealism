package com.draco18s.industry.world;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;

public class FakeWorld extends World {

	public FakeWorld(WorldInfo info, Profiler profilerIn) {
		super(null, info, new WorldProviderVoid(), profilerIn, true);
		this.chunkProvider = this.createChunkProvider();
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return new VolatileChunkProvider(this);
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		return allowEmpty || !getChunkProvider().provideChunk(x, z).isEmpty();
	}
}
