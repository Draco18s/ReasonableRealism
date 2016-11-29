package com.draco18s.industry.world;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import com.draco18s.industry.ExpandedIndustryBase;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

public class VolatileChunkProvider implements IChunkProvider {
    private Set<Long> loadingChunks = com.google.common.collect.Sets.newHashSet();
    private final World worldObj;
    public final IChunkGenerator chunkGenerator;
    public final Long2ObjectMap<Chunk> id2ChunkMap = new Long2ObjectOpenHashMap(8192);
    private final Set<Long> droppedChunksSet = Sets.<Long>newHashSet();
    
    public VolatileChunkProvider(World worldIn) {
        this.worldObj = worldIn;
        chunkGenerator = new ChunkProviderVoid(worldIn);
    }
    
	@Override
	public Chunk getLoadedChunk(int x, int z) {
		long i = ChunkPos.chunkXZ2Int(x, z);
        Chunk chunk = (Chunk)this.id2ChunkMap.get(i);

        if (chunk != null)
        {
            chunk.unloaded = false;
        }

        return chunk;
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		Chunk chunk = this.loadChunk(x, z);

        if (chunk == null)
        {
            long i = ChunkPos.chunkXZ2Int(x, z);

            try
            {
                chunk = this.chunkGenerator.provideChunk(x, z);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                crashreportcategory.addCrashSection("Location", String.format("%d,%d", new Object[] {Integer.valueOf(x), Integer.valueOf(z)}));
                crashreportcategory.addCrashSection("Position hash", Long.valueOf(i));
                crashreportcategory.addCrashSection("Generator", this.chunkGenerator);
                throw new ReportedException(crashreport);
            }

            this.id2ChunkMap.put(i, chunk);
            chunk.onChunkLoad();
            //chunk.populateChunk(this, this.chunkGenerator);
        }

        return chunk;
	}

	@Override
	public boolean unloadQueuedChunks() {
		if (true /*!this.worldObj.disableLevelSaving*/)
        {
            if (!this.droppedChunksSet.isEmpty())
            {
                for (ChunkPos forced : this.worldObj.getPersistentChunks().keySet())
                {
                    this.droppedChunksSet.remove(ChunkPos.chunkXZ2Int(forced.chunkXPos, forced.chunkZPos));
                }

                Iterator<Long> iterator = this.droppedChunksSet.iterator();

                for (int i = 0; i < 100 && iterator.hasNext(); iterator.remove())
                {
                    Long olong = (Long)iterator.next();
                    Chunk chunk = (Chunk)this.id2ChunkMap.get(olong);

                    if (chunk != null && chunk.unloaded)
                    {
                        chunk.onChunkUnload();
                        this.id2ChunkMap.remove(olong);
                        ++i;
                        net.minecraftforge.common.ForgeChunkManager.putDormantChunk(ChunkPos.chunkXZ2Int(chunk.xPosition, chunk.zPosition), chunk);
                        if (id2ChunkMap.size() == 0 && net.minecraftforge.common.ForgeChunkManager.getPersistentChunksFor(this.worldObj).size() == 0 && !this.worldObj.provider.getDimensionType().shouldLoadSpawn()){
                            net.minecraftforge.common.DimensionManager.unloadWorld(this.worldObj.provider.getDimension());
                            break;
                        }
                    }
                }
            }

            //this.chunkLoader.chunkTick();
        }

        return false;
	}

	@Override
	public String makeString() {
		return "ServerChunkCache: " + this.id2ChunkMap.size() + " Drop: " + this.droppedChunksSet.size();
	}
	
	@Nullable
    public Chunk loadChunk(int x, int z)
    {
        return loadChunk(x, z, null);
    }

    @Nullable
    public Chunk loadChunk(int x, int z, Runnable runnable)
    {
        Chunk chunk = this.getLoadedChunk(x, z);
        if (chunk == null)
        {
            long pos = ChunkPos.chunkXZ2Int(x, z);
            chunk = net.minecraftforge.common.ForgeChunkManager.fetchDormantChunk(pos, this.worldObj);
            if (chunk != null)
            {
                if (!loadingChunks.add(pos)) net.minecraftforge.fml.common.FMLLog.bigWarning("There is an attempt to load a chunk (%d,%d) in dimension %d that is already being loaded. This will cause weird chunk breakages.", x, z, this.worldObj.provider.getDimension());
                //if (chunk == null) chunk = this.loadChunkFromFile(x, z);

                if (chunk != null)
                {
	                this.id2ChunkMap.put(ChunkPos.chunkXZ2Int(x, z), chunk);
	                chunk.onChunkLoad();
	                chunk.populateChunk(this, this.chunkGenerator);
                }

                loadingChunks.remove(pos);
            }
            else
            {
                /*net.minecraft.world.chunk.storage.AnvilChunkLoader loader = (net.minecraft.world.chunk.storage.AnvilChunkLoader) this.chunkLoader;
                if (runnable == null)
                    chunk = net.minecraftforge.common.chunkio.ChunkIOExecutor.syncChunkLoad(this.worldObj, loader, this, x, z);
                else if (loader.chunkExists(this.worldObj, x, z))
                {
                    // We can only use the async queue for already generated chunks
                    net.minecraftforge.common.chunkio.ChunkIOExecutor.queueChunkLoad(this.worldObj, loader, this, x, z, runnable);
                    return null;
                }*/
            }
        }

        // If we didn't load the chunk async and have a callback run it now
        if (runnable != null) runnable.run();
        return chunk;
    }
}
