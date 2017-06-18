package com.draco18s.industry.world;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

public class VolatileChunkProvider implements IChunkProvider {
	private Set<Long> loadingChunks = com.google.common.collect.Sets.newHashSet();
	private final World world;
	public final IChunkGenerator chunkGenerator;
	public final Long2ObjectMap<Chunk> id2ChunkMap = new Long2ObjectOpenHashMap(8192);
	private final Set<Long> droppedChunksSet = Sets.<Long>newHashSet();

	public VolatileChunkProvider(World worldIn) {
		world = worldIn;
		chunkGenerator = new ChunkProviderVoid(worldIn);
	}

	@Override
	public Chunk getLoadedChunk(int x, int z) {
		long i = ChunkPos.asLong(x, z);
		Chunk chunk = id2ChunkMap.get(i);

		if (chunk != null) {
			chunk.unloaded = false;
		}

		return chunk;
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		Chunk chunk = this.loadChunk(x, z);

		if (chunk == null) {
			long i = ChunkPos.asLong(x, z);

			try {
				chunk = chunkGenerator.provideChunk(x, z);
			}
			catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
				crashreportcategory.addCrashSection("Location",
						String.format("%d,%d", new Object[] { Integer.valueOf(x), Integer.valueOf(z) }));
				crashreportcategory.addCrashSection("Position hash", Long.valueOf(i));
				crashreportcategory.addCrashSection("Generator", chunkGenerator);
				throw new ReportedException(crashreport);
			}

			id2ChunkMap.put(i, chunk);
			chunk.onChunkLoad();
		}

		return chunk;
	}

	/*@Override
	public boolean unloadQueuedChunks() {
		if (!droppedChunksSet.isEmpty()) {
			for (ChunkPos forced : world.getPersistentChunks().keySet()) {
				droppedChunksSet.remove(ChunkPos.asLong(forced.chunkXPos, forced.chunkZPos));
			}

			Iterator<Long> iterator = droppedChunksSet.iterator();

			for (int i = 0; i < 100 && iterator.hasNext(); iterator.remove()) {
				Long olong = iterator.next();
				Chunk chunk = id2ChunkMap.get(olong);

				if (chunk != null && chunk.unloaded) {
					chunk.onChunkUnload();
					id2ChunkMap.remove(olong);
					++i;
					net.minecraftforge.common.ForgeChunkManager
							.putDormantChunk(ChunkPos.asLong(chunk.xPosition, chunk.zPosition), chunk);
					if (id2ChunkMap.size() == 0 && net.minecraftforge.common.ForgeChunkManager
							.getPersistentChunksFor(world).size() == 0
							&& !world.provider.getDimensionType().shouldLoadSpawn()) {
						net.minecraftforge.common.DimensionManager.unloadWorld(world.provider.getDimension());
						break;
					}
				}
			}
		}

		return false;
	}*/

	@Override
	public String makeString() {
		return "ServerChunkCache: " + id2ChunkMap.size() + " Drop: " + droppedChunksSet.size();
	}

	@Nullable
	public Chunk loadChunk(int x, int z) {
		return loadChunk(x, z, null);
	}

	@Nullable
	public Chunk loadChunk(int x, int z, Runnable runnable) {
		Chunk chunk = getLoadedChunk(x, z);
		if (chunk == null) {
			long pos = ChunkPos.asLong(x, z);
			chunk = net.minecraftforge.common.ForgeChunkManager.fetchDormantChunk(pos, world);
			if (chunk != null) {
				if (!loadingChunks.add(pos)) {
					net.minecraftforge.fml.common.FMLLog.bigWarning(
							"There is an attempt to load a chunk (%d,%d) in dimension %d that is already being loaded. This will cause weird chunk breakages.",
							x, z, world.provider.getDimension());
				}

				if (chunk != null) {
					id2ChunkMap.put(ChunkPos.asLong(x, z), chunk);
					chunk.onChunkLoad();
					chunk.populateChunk(this, chunkGenerator);
				}

				loadingChunks.remove(pos);
			}
		}

		// If we didn't load the chunk async and have a callback run it now
		if (runnable != null) {
			runnable.run();
		}
		return chunk;
	}

	@Override
	public boolean tick() {
		return false;
	}

    public boolean isChunkGeneratedAt(int x, int z) {
        return this.id2ChunkMap.containsKey(ChunkPos.asLong(x, z));
    }
}
