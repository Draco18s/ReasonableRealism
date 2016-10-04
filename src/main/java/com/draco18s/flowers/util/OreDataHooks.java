package com.draco18s.flowers.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.internal.BlockWrapper;
import com.draco18s.hardlib.internal.OreFlowerData;
import com.draco18s.hardlib.internal.OreFlowerDictator;
import com.draco18s.hardlib.math.HashUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreDataHooks {

	public void generate(int cx, int cz, World world) {
		
		
		
		
		
		Map<BlockWrapper, OreCounter> blockList = new HashMap();
		Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> list = HardLibAPI.oreFlowers.getOreList();
		for(BlockWrapper b : list.keySet()) {
			blockList.put(b, new OreCounter(b));
		}
		
		for(int i = 0; i < 32; i++) {
			Iterable<BlockPos> blockposlist = BlockPos.getAllInBox(new BlockPos(cx*16, i*8, cz*16),new BlockPos(cx*16+15, i*8+7, cz*16+15));
			Iterator<BlockPos> it = blockposlist.iterator();
			while(it.hasNext()) {
				BlockPos pos = it.next();
				IBlockState state = world.getBlockState(pos);
				BlockWrapper key = new BlockWrapper(state);
				OreCounter count = blockList.get(key);
				if(count != null) {
					count.increment(state);
					HardLibAPI.oreFlowers.trySpawnFlowerCluster(world, pos, key);
				}
			}
		}
	}
	
	private class OreCounter extends Object{
		public BlockWrapper b;
		//public int ID;
		//public int meta;
		private int countA;
		private int countB;
		private int countC;
		private int fHashCode;
		
		public OreCounter(BlockWrapper block) {
			b = block;
			//ID = Block.getIdFromBlock(b.block);
			//meta = b.meta;
			countA = 0;
			countB = 0;
			countC = 0;
		}
		
		public void cycleCounts() {
			countC = countB;
			countB = countA;
			countA = 0;
		}
		
		@Override
		public int hashCode() {
			/*if (fHashCode == 0) {
			      int result = HashUtils.SEED;
			      result = HashUtils.hash(result, b.hashCode());
			      fHashCode = result;
			}*/
			return b.hashCode();
	    }
		
		@Override
		public boolean equals(Object aThat) {
			OreCounter that = (OreCounter) aThat;
			return that.b == b;
		}

		public int getCountC() {
			return countC;
		}

		public int getCountB() {
			return countB;
		}

		public int getCountA() {
			return countA;
		}

		public void increment(int val) {
			countA += val;
		}

		public void increment(IBlockState state) {
			countA += b.getOreValue(state);
		}
	}
}
