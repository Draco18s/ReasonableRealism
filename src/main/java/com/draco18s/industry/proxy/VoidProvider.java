package com.draco18s.industry.proxy;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VoidProvider extends AbstractChunkProvider {
	private final Chunk empty;
	private final WorldLightManager lightManager;
	private volatile VoidProvider.ChunkArray array;
	private final FakeWorld world;
	private ChunkGenerator<?> generator;

	public VoidProvider(FakeWorld worldIn, int viewDistance, ChunkGenerator<?> chunkGeneratorIn) {
		this.empty = new EmptyChunk(worldIn, new ChunkPos(0, 0));
		this.lightManager = new WorldLightManager(this, true, worldIn.getDimension().hasSkyLight());
		world = worldIn;
		this.array = new VoidProvider.ChunkArray(adjustViewDistance(viewDistance));
		this.generator = chunkGeneratorIn;
	}

	private static int adjustViewDistance(int val) {
		return Math.max(2, val) + 3;
	}

	@Override
	public IBlockReader getWorld() {
		return world;
	}

	@Override
	public IChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
		if (this.array.inView(chunkX, chunkZ)) {
			Chunk chunk = this.array.get(this.array.getIndex(chunkX, chunkZ));
			if (isValid(chunk, chunkX, chunkZ)) {
				net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(chunk));
				return chunk;
			}
		}

		return load ? this.empty : null;
	}

	private boolean isValid(Chunk chunk, int chunkX, int chunkZ) {
		return false;
	}

	@Override
	public void tick(BooleanSupplier hasTimeLeft) {

	}

	@Override
	public String makeString() {
		return "FakeWorld Chunk Cache: " + this.array.chunks.length() + ", " + this.array.loaded;
	}

	@Override
	public ChunkGenerator<?> getChunkGenerator() {
		return this.generator;
	}

	@Override
	public WorldLightManager getLightManager() {
		return lightManager;
	}
	@OnlyIn(Dist.CLIENT)
	final class ChunkArray {
		private final AtomicReferenceArray<Chunk> chunks;
		private final int viewDistance;
		private final int sideLength;
		private volatile int centerX;
		private volatile int centerZ;
		private int loaded;

		private ChunkArray(int p_i50568_2_) {
			this.viewDistance = p_i50568_2_;
			this.sideLength = p_i50568_2_ * 2 + 1;
			this.chunks = new AtomicReferenceArray<>(this.sideLength * this.sideLength);
		}

		private int getIndex(int x, int z) {
			return Math.floorMod(z, this.sideLength) * this.sideLength + Math.floorMod(x, this.sideLength);
		}

		protected void replace(int p_217181_1_, @Nullable Chunk p_217181_2_) {
			Chunk chunk = this.chunks.getAndSet(p_217181_1_, p_217181_2_);
			if (chunk != null) {
				--this.loaded;
				//VoidProvider.this.world.onChunkUnloaded(chunk);
			}

			if (p_217181_2_ != null) {
				++this.loaded;
			}

		}

		protected Chunk unload(int p_217190_1_, Chunk p_217190_2_, @Nullable Chunk p_217190_3_) {
			if (this.chunks.compareAndSet(p_217190_1_, p_217190_2_, p_217190_3_) && p_217190_3_ == null) {
				--this.loaded;
			}

			//VoidProvider.this.world.onChunkUnloaded(p_217190_2_);
			return p_217190_2_;
		}

		private boolean inView(int p_217183_1_, int p_217183_2_) {
			return Math.abs(p_217183_1_ - this.centerX) <= this.viewDistance && Math.abs(p_217183_2_ - this.centerZ) <= this.viewDistance;
		}

		@Nullable
		protected Chunk get(int p_217192_1_) {
			return this.chunks.get(p_217192_1_);
		}
	}
}
