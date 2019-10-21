package com.draco18s.harderores.world.feature;

import java.util.Random;
import java.util.function.Function;

import com.draco18s.harderores.HarderOres;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OreVeinStructure extends Structure<OreVeinStructureConfig> {

	public OreVeinStructure(Function<Dynamic<?>, ? extends OreVeinStructureConfig> configFactoryIn) {
		super(configFactoryIn);
	}

	/*@Override
	public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand,BlockPos pos, OreVeinStructureConfig config) {
		if (!worldIn.getWorldInfo().isMapFeaturesEnabled()) {
			return false;
		} else {
			int i = pos.getX() >> 4;
			int j = pos.getZ() >> 4;
			int k = i << 4;
			int l = j << 4;
			boolean flag = false;

			for(Long olong : worldIn.getChunk(i, j).getStructureReferences(this.getStructureName())) {
				ChunkPos chunkpos = new ChunkPos(olong);
				StructureStart structurestart = worldIn.getChunk(chunkpos.x, chunkpos.z).getStructureStart(this.getStructureName());
				if (structurestart != null) {
					if(structurestart != StructureStart.DUMMY) {
						structurestart.generateStructure(worldIn, rand, new MutableBoundingBox(k, l, k + 15, l + 15), new ChunkPos(i, j));
						flag = true;
					}
				}
			}

			return flag;
		}
	}*/

	@Override
	public boolean hasStartAt(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
		((SharedSeedRandom)rand).setLargeFeatureSeed(chunkGen.getSeed(), chunkPosX, chunkPosZ);
		BlockPos pos = new BlockPos((chunkPosX << 4) + 9, 0, (chunkPosZ << 4) + 9);
		Biome biome = chunkGen.getBiomeProvider().getBiome(pos);
		if (chunkGen.hasStructure(biome, HarderOres.ModFeatures.ore_vein)) {
			OreVeinStructureConfig config = (OreVeinStructureConfig)chunkGen.getStructureConfig(biome, HarderOres.ModFeatures.ore_vein);
			boolean ret = rand.nextDouble() < config.probability;
			//if(ret)
			//	HarderOres.LOGGER.log(Level.DEBUG, "Check loc " + pos);
			return ret;
		} else {
			return false;
		}
	}

	@Override
	public IStartFactory getStartFactory() {
		return OreVeinStructure.Start::new;
	}

	@Override
	public String getStructureName() {
		return "ore_vein";
	}

	@Override
	public int getSize() {
		return 8;
	}

	public static class Start extends StructureStart {
		public Start(Structure<?> structure, int chunkX, int chunkZ, Biome biome, MutableBoundingBox bounds, int reference, long seed) {
			super(structure, chunkX, chunkZ, biome, bounds, reference, seed);
		}

		public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
			//OreVeinStructureConfig mineshaftconfig = (OreVeinStructureConfig)generator.getStructureConfig(biomeIn, OreFeatures.ore_vein);
			OreVeinPieces.Motherload motherload = new OreVeinPieces.Motherload(0, this.rand, (chunkX << 4) + 2, (chunkZ << 4) + 2);
			this.components.add(motherload);
			motherload.buildComponent(motherload, this.components, this.rand);
			this.recalculateStructureSize();
			this.func_214628_a(generator.getSeaLevel(), this.rand, 10);
		}

		//this works, but bypasses some logic
		/*@Override
		public void generateStructure(IWorld world, Random rand, MutableBoundingBox structurebb, ChunkPos pos) {
			//super.generateStructure(worldIn, rand, structurebb, pos);
			BlockPos blockpos = pos.getBlock(8, this.bounds.minY, 8);
			world.setBlockState(blockpos, HarderOres.ModBlocks.ore_harddiamond.getDefaultState().with(BlockProperties.ORE_DENSITY, 16), 3);
			HarderOres.LOGGER.log(Level.DEBUG, "Placing block at " + blockpos);
		}*/
	}
}
