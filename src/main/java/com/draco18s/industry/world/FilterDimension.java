package com.draco18s.industry.world;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FilterDimension extends Dimension {

	public FilterDimension(World worldIn, DimensionType typeIn) {
		super(worldIn, typeIn);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		FlatGenerationSettings generationsettings = ChunkGeneratorType.FLAT.createSettings();
		generationsettings.setDefaultBlock(Blocks.AIR.getDefaultState());
		generationsettings.setDefaultFluid(Blocks.AIR.getDefaultState());
		generationsettings.setBiome(Biomes.THE_VOID);
		return ChunkGeneratorType.FLAT.create(this.world, BiomeProviderType.FIXED.create(BiomeProviderType.FIXED.createSettings()), generationsettings);//.create(BiomeProviderType.THE_END.createSettings().setSeed(this.world.getSeed())), generationsettings);
	}

	@Override
	public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid) {
		return null;
	}

	@Override
	public BlockPos findSpawn(int posX, int posZ, boolean checkValid) {
		return this.findSpawn(new ChunkPos(posX >> 4, posZ >> 4), checkValid);
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		return 0;
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTicks) {
		return new Vec3d(0,0,0);
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean doesXZShowFog(int x, int z) {
		return false;
	}
}
