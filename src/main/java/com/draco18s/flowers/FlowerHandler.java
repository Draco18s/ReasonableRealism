package com.draco18s.flowers;

import java.util.Iterator;
import java.util.Random;

import com.draco18s.hardlib.interfaces.IFlowerHandling;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class FlowerHandler implements IFlowerHandling {

	@Override
	public void spawnFlowerCluster(World world, BlockPos pos, IBlockState state, int radius, int num, int clusterRadius, boolean canBeTallPlant) {
		Random r = new Random();
		float[] u = RandomInUnitCircle(r);
		int fails = 0;
		//int j, k, l;
		//Block ba;
		BlockPos newPos;// = pos.up(0);
		boolean replaceLeaves = num > 1;
		while(num > 0 && fails < 20) {
			newPos = pos.add(r.nextInt(clusterRadius) - (clusterRadius/2) + Math.round(u[0]*radius), -5, r.nextInt(clusterRadius) - (clusterRadius/2) + Math.round(u[1]*radius));
			Iterable<BlockPos> list = BlockPos.getAllInBox(pos, pos.up(10));
			Iterator<BlockPos> it = list.iterator();
			while(it.hasNext()) {
				BlockPos p = it.next();
				IBlockState wb = world.getBlockState(p);
				if(state.getBlock().canPlaceBlockAt(world, p) && (wb.getBlock().isReplaceable(world, p) || wb.getMaterial() == Material.LEAVES) && !(wb.getBlock() instanceof BlockLiquid || wb.getBlock() instanceof IFluidBlock)) {
					if(canBeTallPlant && r.nextInt(3) == 0 && world.getBlockState(p.up()).getMaterial() == Material.AIR) {
						//TODO
						world.setBlockState(p, state, 3);
					}
					else {
						world.setBlockState(p, state, 3);
					}
					break;
				}
			}
			/*for(int f=0; f+k <= y+5; f++) {
				Block wb = world.getBlockState(j, f+k+1, l);
				if(b.canBlockStay(world, j, f+k+1, l) && (wb.isReplaceable(world, j, f+k+1, l) || wb.getMaterial() == Material.leaves) && !(wb instanceof BlockLiquid || wb instanceof IFluidBlock)) {
					world.setBlock(j, f+k+1, l, b, meta, 3);
					if(meta == EnumOreType.GOLD.value && r.nextInt(8) == 0) {
						world.setBlock(j, f+k+1, l, b, meta|8, 3);
					}
					if(meta == EnumOreType.REDSTONE.value && r.nextInt(3) == 0 && world.getBlockState(j, f+k+2, l) == Blocks.AIR) {
						world.setBlock(j, f+k+1, l, b, meta|8, 3);
						world.setBlock(j, f+k+2, l, b, meta,   3);
					}
					if(meta == EnumOreType.TIN.value && r.nextInt(3) == 0 && world.getBlockState(j, f+k+2, l) == Blocks.AIR) {
						world.setBlock(j, f+k+1, l, b, meta|8, 3);
						world.setBlock(j, f+k+2, l, b, meta,   3);
					}
					--num;
					k = 100;
				}
			}*/
			++fails;
		}
	}

	public static float[] RandomInUnitCircle(Random rn) {
		float t = (float)Math.PI * (2*rn.nextFloat());
		float u = rn.nextFloat()+rn.nextFloat();
		float r = (u>1)?2-u:u;

		return new float[] {r*(float)Math.cos(t), r*(float)Math.sin(t)};
	}
}
