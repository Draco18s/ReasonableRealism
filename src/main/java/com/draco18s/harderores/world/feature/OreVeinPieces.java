package com.draco18s.harderores.world.feature;

import java.util.Random;

import org.apache.logging.log4j.Level;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.block.state.BlockProperties;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;

public class OreVeinPieces {
	abstract static class Piece extends TemplateStructurePiece {
		public Piece(IStructurePieceType p_i50452_1_, int p_i50452_2_) {
			super(p_i50452_1_, p_i50452_2_);
		}

		public Piece(IStructurePieceType p_i50453_1_, CompoundNBT p_i50453_2_) {
			super(p_i50453_1_, p_i50453_2_);
		}
	}

	public static class Motherload extends OreVeinPieces.Piece {
		public Motherload(IStructurePieceType p_i50452_1_, int p_i50452_2_) {
			super(p_i50452_1_, p_i50452_2_);
		}

		public Motherload(int p_i47137_1_, Random random, int posX, int posZ) {
			super(IStructurePieceType.MSROOM, p_i47137_1_);
			this.boundingBox = new MutableBoundingBox(posX, 50, posZ, posX + random.nextInt(6), 54 + random.nextInt(6), posZ + random.nextInt(6));
			this.templatePosition = new BlockPos(boundingBox.minX,boundingBox.minY,boundingBox.minZ);
		}

		public Motherload(IStructurePieceType p_i50453_1_, CompoundNBT p_i50453_2_) {
			super(p_i50453_1_, p_i50453_2_);
		}

		@Override
		public boolean addComponentParts(IWorld world, Random randomIn, MutableBoundingBox boundingBoxIn, ChunkPos pos) {
			BlockPos blockpos = pos.getBlock(8, this.boundingBox.minY, 8);
			world.setBlockState(blockpos, HarderOres.ModBlocks.ore_harddiamond.getDefaultState().with(BlockProperties.ORE_DENSITY, 16), 3);
			HarderOres.LOGGER.log(Level.DEBUG, "Placing block at " + blockpos);
			return false;
		}

		@Override
		protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
			
		}
	}
}
