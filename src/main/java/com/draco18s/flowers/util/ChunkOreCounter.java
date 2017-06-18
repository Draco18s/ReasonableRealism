package com.draco18s.flowers.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChunkOreCounter {

	public void generate(int cx, int cz, World world) {
		Map<BlockWrapper, OreCounter> blockList = new HashMap();
		Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> list = HardLibAPI.oreFlowers.getOreList();
		for(BlockWrapper b : list.keySet()) {
			blockList.put(b, new OreCounter(b));
		}
		
		for(int i = 0; i < 32; i++) {
			BlockPos chunkPos = new BlockPos(cx*16, i*8, cz*16); 
			Iterable<BlockPos> blockposlist = BlockPos.getAllInBox(chunkPos,new BlockPos(cx*16+15, i*8+7, cz*16+15));
			Iterator<BlockPos> it = blockposlist.iterator();
			while(it.hasNext()) {
				BlockPos pos = it.next();
				IBlockState state = world.getBlockState(pos);
				BlockWrapper key = new BlockWrapper(state);
				OreCounter count = blockList.get(key);
				//OreFlowersBase.logger.log(Level.WARN, "");
				if(count != null) {
					/*if(cx == 3 && cz == 20 && count.b.block == OresBase.oreDiamond) {
						OreFlowersBase.logger.log(Level.INFO, "Doing flowers for " + (i*8) + ": " + count.countA);
					}*/
					count.increment(state);
					HardLibAPI.oreFlowers.trySpawnFlowerCluster(world, pos, key);
				}
			}
			for(BlockWrapper ore : blockList.keySet()) {
				OreCounter c = blockList.get(ore);
				/*if(cx == 3 && cz == 20 && c.b.block == OresBase.oreDiamond) {
					OreFlowersBase.logger.log(Level.INFO, "Diamond @" + (i*8) + ": " + c.countA);
					OreFlowersBase.logger.log(Level.INFO, "    " + Math.round(c.countA*(2f/3f) + c.countB*(1f/3f)));
				}*/
				HardLibAPI.oreData.putOreData(world, new BlockPos(cx,i*8,cz), ore, Math.round(c.countA*(2f/3f) + c.countB*(1f/3f)));
				c.cycleCounts();
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
