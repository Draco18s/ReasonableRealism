package com.draco18s.farming.world;

import java.util.Random;

import com.draco18s.farming.FarmingBase;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenerator implements IWorldGenerator {
	private WorldGenMinable minable;
	
	public WorldGenerator() {
		//The 10 as the second parameter sets the maximum vein size
		minable = new WorldGenMinableSalt(FarmingBase.saltOre.getDefaultState(), 20);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		generateSurface(world, random, chunkX * 16, chunkZ * 16);
	}

	private void generateSurface(World world, Random rand, int cx, int cz) {
		if(world.provider.getDimensionType() == DimensionType.NETHER || world.provider.getDimensionType() == DimensionType.THE_END) return;
		//24 was too much, salt was everywhere
		for (int k = 0; k < 8; k++) {
			int x = cx + rand.nextInt(16);
			int z = cz + rand.nextInt(16);
			//Will be found between y = 40 and y = 70
			int y = rand.nextInt(30) + 40;
			BlockPos quisquePos = new BlockPos(x, y, z);
			minable.generate(world, rand, quisquePos);
		}
	}
}
