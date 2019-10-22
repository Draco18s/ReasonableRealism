package com.draco18s.harderores.world.feature;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.HarderOres.ModStructurePieceTypes;
import com.draco18s.hardlib.api.block.state.BlockProperties;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OreVeinPieces {
	public abstract static class Piece extends TemplateStructurePiece {
		public Piece(IStructurePieceType p_i50452_1_, int p_i50452_2_) {
			super(p_i50452_1_, p_i50452_2_);
		}

		public Piece(IStructurePieceType p_i50453_1_, CompoundNBT p_i50453_2_) {
			super(p_i50453_1_, p_i50453_2_);
		}

		private static double distance(BlockPos A, BlockPos B) {
			int dx = A.getX() - B.getX();
			int dy = A.getY() - B.getY();
			int dz = A.getZ() - B.getZ();
			return Math.sqrt(dx*dx+dy*dy+dz*dz);
		}
	}

	public static class Motherload extends OreVeinPieces.Piece {
		public Motherload(TemplateManager manager, CompoundNBT nbt) {
			super(ModStructurePieceTypes.OVMotherLoad,nbt);
		}
		public Motherload(IStructurePieceType typ, int p_i50452_2_) {
			super(typ, p_i50452_2_);
		}

		public Motherload(int p_i47137_1_, Random random, int posX, int posZ) {
			super(ModStructurePieceTypes.OVMotherLoad, p_i47137_1_);
			this.boundingBox = new MutableBoundingBox(posX, 50, posZ, posX + 7 + random.nextInt(6), 57 + random.nextInt(6), posZ + 7 + random.nextInt(6));
			this.templatePosition = new BlockPos(boundingBox.minX,boundingBox.minY,boundingBox.minZ);
		}

		public Motherload(IStructurePieceType p_i50453_1_, CompoundNBT p_i50453_2_) {
			super(p_i50453_1_, p_i50453_2_);
		}

		@Override
		public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
			for(int i = 0; i < 4; i++) {
				Direction dir = Direction.byHorizontalIndex(i);
				if(rand.nextBoolean() || rand.nextBoolean()) {
					int cX =(boundingBox.maxX-boundingBox.minX)/2+boundingBox.minX;
					int cY =(boundingBox.maxY-boundingBox.minY)/2+boundingBox.minY;
					int cZ =(boundingBox.maxZ-boundingBox.minZ)/2+boundingBox.minZ;
					MutableBoundingBox newBox = MutableBoundingBox.createProper(cX, cY, cZ, cX, cY, cZ);
					newBox.offset(dir.getXOffset()*boundingBox.getXSize()/2, 0, dir.getZOffset()*boundingBox.getZSize()/2);
					newBox.minX -= 2*dir.getZOffset();
					newBox.maxX += 2*dir.getZOffset();
					newBox.minZ -= 2*dir.getXOffset();
					newBox.maxZ += 2*dir.getXOffset();
					newBox.minX += dir.getXOffset() >= 0?0:8*dir.getXOffset();
					newBox.maxX += dir.getXOffset() <= 0?0:8*dir.getXOffset();
					newBox.minZ += dir.getZOffset() >= 0?0:8*dir.getZOffset();
					newBox.maxZ += dir.getZOffset() <= 0?0:8*dir.getZOffset();
					newBox.minY -= 2;
					newBox.maxY += 2;
					if(newBox.minX > newBox.maxX) {
						newBox.minX^=newBox.maxX;
						newBox.maxX^=newBox.minX;
						newBox.minX^=newBox.maxX;
					}
					if(newBox.minZ > newBox.maxZ) {
						newBox.minZ^=newBox.maxZ;
						newBox.maxZ^=newBox.minZ;
						newBox.minZ^=newBox.maxZ;
					}
					Vein veinPart = new Vein(ModStructurePieceTypes.OreVein, this.getComponentType()+i+1, newBox, dir);
					listIn.add(veinPart);
					veinPart.buildComponent(veinPart, listIn, rand);
				}
			}
		}

		@Override
		public boolean addComponentParts(IWorld world, Random randomIn, MutableBoundingBox boundingBoxIn, ChunkPos pos) {
			int centerX = (boundingBox.maxX-boundingBox.minX)/2;
			int centerY = (boundingBox.maxY-boundingBox.minY)/2;
			int centerZ = (boundingBox.maxZ-boundingBox.minZ)/2;
			int size = Math.min(Math.min(centerX, centerY), centerZ);
			centerX += boundingBox.minX;
			centerY += boundingBox.minY;
			centerZ += boundingBox.minZ;
			BlockPos center = new BlockPos(centerX, centerY, centerZ);
			Stream<BlockPos> stream = BlockPos.getAllInBox(boundingBox.minX-1,boundingBox.minY-1,boundingBox.minZ-1,boundingBox.maxX+1,boundingBox.maxY+1,boundingBox.maxZ+1);
			Iterator<BlockPos> it = stream.iterator();
			int minv = BlockProperties.ORE_DENSITY.getAllowedValues().stream().min(Integer::compare).get();
			int maxv = BlockProperties.ORE_DENSITY.getAllowedValues().stream().max(Integer::compare).get();
			while(it.hasNext()) {
				BlockPos p = it.next();
				double distFromCenter = Piece.distance(p, center);
				if(distFromCenter <= size) {
					double sizeMagnitude = 1-(distFromCenter/size);
					int densityVal = (int)Math.round(sizeMagnitude * 16);
					//clamp
					densityVal = Math.min(Math.max(densityVal, minv), maxv);
					
					world.setBlockState(p, HarderOres.ModBlocks.ore_harddiamond.getDefaultState().with(BlockProperties.ORE_DENSITY, densityVal), 2);
				}
				else {
					//world.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
				}
			}
			HarderOres.LOGGER.log(Level.DEBUG, "Motherload center at " + center);

			return false;
		}
		@Override
		protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {

		}
	}

	public static class Vein extends OreVeinPieces.Piece {
		private final Direction direction;

		public Vein(IStructurePieceType p_i50453_1_, CompoundNBT nbt) {
			super(ModStructurePieceTypes.OreVein, nbt);
			direction = Direction.NORTH;//from nbt
		}

		public Vein(IStructurePieceType typ, int p_i50452_2_) {
			super(typ, p_i50452_2_);
			direction = Direction.NORTH;
		}

		public Vein(IStructurePieceType typ, int p_i50452_2_, MutableBoundingBox boundsIn, Direction dir) {
			super(typ, p_i50452_2_);
			this.boundingBox = boundsIn;
			this.templatePosition = new BlockPos(boundingBox.minX,boundingBox.minY,boundingBox.minZ);
			this.direction = dir;
		}

		@Override
		protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {

		}
		
		@Override
		public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
			super.buildComponent(componentIn, listIn, rand);
			if(rand.nextBoolean()) {
				Direction dir = this.direction;
				int cX =(boundingBox.maxX-boundingBox.minX)/2+boundingBox.minX;
				int cY =(boundingBox.maxY-boundingBox.minY)/2+boundingBox.minY;
				int cZ =(boundingBox.maxZ-boundingBox.minZ)/2+boundingBox.minZ;
				MutableBoundingBox newBox = MutableBoundingBox.createProper(cX, cY, cZ, cX, cY, cZ);
				newBox.offset(dir.getXOffset()*boundingBox.getXSize()/2, 0, dir.getZOffset()*boundingBox.getZSize()/2);
				newBox.minX -= 2*dir.getZOffset();
				newBox.maxX += 2*dir.getZOffset();
				newBox.minZ -= 2*dir.getXOffset();
				newBox.maxZ += 2*dir.getXOffset();
				newBox.minX += dir.getXOffset() >= 0?0:8*dir.getXOffset();
				newBox.maxX += dir.getXOffset() <= 0?0:8*dir.getXOffset();
				newBox.minZ += dir.getZOffset() >= 0?0:8*dir.getZOffset();
				newBox.maxZ += dir.getZOffset() <= 0?0:8*dir.getZOffset();
				newBox.minY -= 2;
				newBox.maxY += 2;
				if(newBox.minX > newBox.maxX) {
					newBox.minX^=newBox.maxX;
					newBox.maxX^=newBox.minX;
					newBox.minX^=newBox.maxX;
				}
				if(newBox.minZ > newBox.maxZ) {
					newBox.minZ^=newBox.maxZ;
					newBox.maxZ^=newBox.minZ;
					newBox.minZ^=newBox.maxZ;
				}
				Vein veinPart = new Vein(ModStructurePieceTypes.OreVein, this.getComponentType()+1, newBox, dir);
				listIn.add(veinPart);
				veinPart.buildComponent(veinPart, listIn, rand);
			}
		}

		@Override
		public boolean addComponentParts(IWorld world, Random randomIn, MutableBoundingBox boundingBoxIn, ChunkPos pos) {
			int centerX = (boundingBox.maxX-boundingBox.minX)/2;
			int centerY = (boundingBox.maxY-boundingBox.minY)/2+boundingBox.minY;
			int centerZ = (boundingBox.maxZ-boundingBox.minZ)/2;
			BlockPos center;
			centerX = centerX*Math.abs(direction.getZOffset()) + boundingBox.minX;
			centerZ = centerZ*Math.abs(direction.getXOffset()) + boundingBox.minZ;
			
			center = new BlockPos(centerX,centerY,centerZ);
			int size = Math.min(Math.min(boundingBox.getXSize(), boundingBox.getYSize()), boundingBox.getZSize())/2;
			
			Stream<BlockPos> stream = BlockPos.getAllInBox(boundingBox.minX-2,boundingBox.minY-2,boundingBox.minZ-2,boundingBox.maxX+2,boundingBox.maxY+2,boundingBox.maxZ+2);
			Iterator<BlockPos> it = stream.iterator();
			int minv = BlockProperties.ORE_DENSITY.getAllowedValues().stream().min(Integer::compare).get();
			int maxv = BlockProperties.ORE_DENSITY.getAllowedValues().stream().max(Integer::compare).get();
			int num = 0;
			while(it.hasNext()) {
				num++;
				BlockPos p = it.next();
				BlockPos c2 = new BlockPos(center.getX()*Math.abs(direction.getZOffset())+p.getX()*Math.abs(direction.getXOffset()),center.getY(),center.getZ()*Math.abs(direction.getXOffset())+p.getZ()*Math.abs(direction.getZOffset()));
				double distFromCenter = Piece.distance(p, c2);
				if(distFromCenter <= size) {
					double sizeMagnitude = 1-(distFromCenter/size);
					int densityVal = (int)Math.round(sizeMagnitude * 16);
					//clamp
					densityVal = Math.min(Math.max(densityVal, minv), maxv);
					if(world.getBlockState(p).getBlock() == Blocks.STONE)
						world.setBlockState(p, HarderOres.ModBlocks.ore_harddiamond.getDefaultState().with(BlockProperties.ORE_DENSITY, densityVal), 2);
				}
				else {
					world.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
				}
			}
			HarderOres.LOGGER.log(Level.DEBUG, "Vein at " + center + ";" + num);
			return false;
		}
	}
}
