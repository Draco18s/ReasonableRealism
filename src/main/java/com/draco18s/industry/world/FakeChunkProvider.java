package com.draco18s.industry.world;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.lighting.WorldLightManager;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FakeChunkProvider
 * Created by HellFirePvP
 * Date: 16.08.2019 / 19:26
 */
public class FakeChunkProvider extends AbstractChunkProvider {

    private World world;
    private Chunk theChunk;

    public FakeChunkProvider(World world) {
        this.world = world;
        theChunk = new Chunk(world, new ChunkPrimer(new ChunkPos(0, 0), UpgradeData.EMPTY));
    }

    @Nullable
    @Override
    public IChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
        return theChunk;
    }

    @Override
    public void tick(BooleanSupplier hasTimeLeft) {

    }

    @Override
    public String makeString() {
        return "Some String Woo";
    }

    @Override
    public ChunkGenerator<?> getChunkGenerator() {
        return new ChunkGenerator<GenerationSettings>(world, new SingleBiomeProvider(new SingleBiomeProviderSettings().setBiome(Biomes.PLAINS)), new GenerationSettings()) {
            @Override
            public void generateSurface(IChunk chunkIn) {
                //no surface for you
            }

            @Override
            public int getGroundHeight() {
                return 0; //no height for you
            }

            @Override
            public void makeBase(IWorld worldIn, IChunk chunkIn) {
                //No bedrock for you
            }

            @Override
            public int func_222529_a(int posX, int posZ, Heightmap.Type type) {
                return 0; //No y level for you
            }
        };
    }

    @Override
    public WorldLightManager getLightManager() {
        return new WorldLightManager(this, false, false); //Do some always fullbright light manager
    }

    @Override
    public IBlockReader getWorld() {
        return world;
    }
}