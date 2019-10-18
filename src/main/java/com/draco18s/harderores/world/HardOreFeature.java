package com.draco18s.harderores.world;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class HardOreFeature extends OreFeature {
	protected final IntegerProperty densityProp;

	public HardOreFeature(Function<Dynamic<?>, ? extends OreFeatureConfig> p_i51472_1_, IntegerProperty densityProp) {
		super(p_i51472_1_);
		this.densityProp = densityProp;
	}

	protected boolean func_207803_a(IWorld worldIn, Random random, OreFeatureConfig config, double x1, double x2, double z1, double z2, double y1, double y2, int xMinIn, int yMinIn, int zMinIn, int XZSize, int YSize) {
		int numPlaced = 0;
		BitSet alreadyPlacedFlags = new BitSet(XZSize * YSize * XZSize);
		BlockPos.MutableBlockPos placementPos = new BlockPos.MutableBlockPos();
		double[] adouble = new double[config.size * 4];
		double centerX = MathHelper.lerp(0.5, x1, x2);
		double centerY = MathHelper.lerp(0.5, y1, y2);
		double centerZ = MathHelper.lerp(0.5, z1, z2);
		for(int blockN = 0; blockN < config.size; ++blockN) {
			float f = (float)blockN / (float)config.size;
			double xf = MathHelper.lerp((double)f, x1, x2);
			double yf = MathHelper.lerp((double)f, y1, y2);
			double zf = MathHelper.lerp((double)f, z1, z2);
			double d6 = random.nextDouble() * (double)config.size / 16.0D;
			double d7 = ((double)(MathHelper.sin((float)Math.PI * f) + 1.0F) * d6 + 1.0D) / 2.0D;
			adouble[blockN * 4 + 0] = xf;//x
			adouble[blockN * 4 + 1] = yf;//y
			adouble[blockN * 4 + 2] = zf;//z
			adouble[blockN * 4 + 3] = d7;//place chance
		}

		for(int blockN = 0; blockN < config.size - 1; ++blockN) {
			if (!(adouble[blockN * 4 + 3] <= 0.0D)) {
				for(int blockO = blockN + 1; blockO < config.size; ++blockO) {
					if (!(adouble[blockO * 4 + 3] <= 0.0D)) {
						double d12 = adouble[blockN * 4 + 0] - adouble[blockO * 4 + 0];
						double d13 = adouble[blockN * 4 + 1] - adouble[blockO * 4 + 1];
						double d14 = adouble[blockN * 4 + 2] - adouble[blockO * 4 + 2];
						double d15 = adouble[blockN * 4 + 3] - adouble[blockO * 4 + 3];
						if (d15 * d15 > d12 * d12 + d13 * d13 + d14 * d14) {
							if (d15 > 0.0D) {
								adouble[blockO * 4 + 3] = -1.0D;
							} else {
								adouble[blockN * 4 + 3] = -1.0D;
							}
						}
					}
				}
			}
		}

		for(int blockN = 0; blockN < config.size; ++blockN) {
			double placementOffset = adouble[blockN * 4 + 3];
			if (placementOffset >= 0.0D) {
				double thisCentX = adouble[blockN * 4 + 0];
				double thisCentY = adouble[blockN * 4 + 1];
				double thisCentZ = adouble[blockN * 4 + 2];
				int xmin = Math.max(MathHelper.floor(thisCentX - placementOffset), xMinIn);
				int ymin = Math.max(MathHelper.floor(thisCentY - placementOffset), yMinIn);
				int zmin = Math.max(MathHelper.floor(thisCentZ - placementOffset), zMinIn);
				int xmax = Math.max(MathHelper.floor(thisCentX + placementOffset), xmin);
				int ymax = Math.max(MathHelper.floor(thisCentY + placementOffset), ymin);
				int zmax = Math.max(MathHelper.floor(thisCentZ + placementOffset), zmin);

				for(int xpos = xmin; xpos <= xmax; ++xpos) {
					double d8 = ((double)xpos + 0.5D - thisCentX) / placementOffset;
					if (d8 * d8 < 1.0D) {
						for(int ypos = ymin; ypos <= ymax; ++ypos) {
							double d9 = ((double)ypos + 0.5D - thisCentY) / placementOffset;
							if (d8 * d8 + d9 * d9 < 1.0D) {
								for(int zpos = zmin; zpos <= zmax; ++zpos) {
									double d10 = ((double)zpos + 0.5D - thisCentZ) / placementOffset;
									if (d8 * d8 + d9 * d9 + d10 * d10 < 1.0D) {
										int posHash = xpos - xMinIn + (ypos - yMinIn) * XZSize + (zpos - zMinIn) * XZSize * YSize;
										if (!alreadyPlacedFlags.get(posHash)) {
											double distFromCenter = Math.sqrt((xpos - centerX)*(xpos - centerX)+(ypos - centerY)*(ypos - centerY)+(zpos - centerZ)*(zpos - centerZ));
											double sizeMagnitude = 1 - (distFromCenter/Math.sqrt(config.size/2));
											int densityVal = (int)Math.round(sizeMagnitude * sizeMagnitude * 16);
											//clamp
											densityVal = Math.min(Math.max(densityVal, densityProp.getAllowedValues().stream().min(Integer::compare).get()), densityProp.getAllowedValues().stream().max(Integer::compare).get());
											alreadyPlacedFlags.set(posHash);
											placementPos.setPos(xpos, ypos, zpos);
											if (config.target.func_214738_b().test(worldIn.getBlockState(placementPos))) {
												worldIn.setBlockState(placementPos, config.state.with(densityProp, densityVal), 2);
												++numPlaced;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return numPlaced > 0;
	}
}
