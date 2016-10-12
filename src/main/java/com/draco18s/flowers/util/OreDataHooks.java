package com.draco18s.flowers.util;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Level;

import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.hardlib.interfaces.IOreData;
import com.draco18s.hardlib.internal.BlockWrapper;
import com.draco18s.hardlib.internal.ChunkCoords;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class OreDataHooks implements IOreData {
	private static final int VERSION = 3;
	private static ConcurrentHashMap<ChunkCoords, HashMap<BlockWrapper,Integer>> graphs = new ConcurrentHashMap<ChunkCoords, HashMap<BlockWrapper,Integer>>();
	
	@Override
	public void putOreData(World world, BlockPos pos, BlockWrapper ore, int count) {
		ChunkCoords key = new ChunkCoords(world.provider.getDimension(),pos.getX(),pos.getY(),pos.getZ());
    	HashMap<BlockWrapper,Integer> value = graphs.get(key);
    	if(value == null) {
    		value = new HashMap<BlockWrapper,Integer>();
    	}
        value.put(ore, count);
    	graphs.put(key, value);
	}

	@Override
	public int getOreData(World world, BlockPos pos, BlockWrapper ore) {
		Chunk c = world.getChunkFromBlockCoords(pos);
		int y = pos.getY();
		y -= y%8;
		ChunkCoords key = new ChunkCoords(world.provider.getDimension(), c.xPosition,y,c.zPosition);
		HashMap<BlockWrapper,Integer> map = graphs.get(key);
		if(map == null || !map.containsKey(ore)) {
			return 0;
		}
		return map.get(ore);
	}

	@Override
	public void adjustOreData(World world, BlockPos pos, BlockWrapper ore, int amount) {
		Chunk c = world.getChunkFromBlockCoords(pos);
		int y = pos.getY();
		y -= y%8;
		int k = 0;
    	do {
			ChunkCoords key = new ChunkCoords(world.provider.getDimension(), c.xPosition,y-8*k,c.zPosition);
			HashMap<BlockWrapper,Integer> map = graphs.get(key);
			if(map == null) {
				map = new HashMap();
				graphs.put(key, map);
			}
			if(!map.containsKey(ore) && amount > 0) {
				map.put(ore, 0);
			}
			int mm = Math.min(map.get(ore), amount);
			int n = Math.max(map.get(ore) + amount, 0);
			if(n == 0) n = -1;
	    	map.put(ore, n);
	    	amount += mm;
    	}while((y-8*(++k)) >= 0 && amount > 0);
	}
	
	public static void readData(World world, int chunkx, int chunkz, NBTTagCompound nbt) {
		if(nbt.hasKey("HardOreData")) {
    		NBTTagCompound honbt = nbt.getCompoundTag("HardOreData");
    		if(honbt.getInteger("version") != VERSION) {
    			//TODO
    			return;
    		}
    		for(int y=0; y < 256; y+=8) {
	    		if(honbt.hasKey("slice_"+y)) {
	    			ChunkCoords key = new ChunkCoords(world.provider.getDimension(), chunkx, y, chunkz);
    				HashMap<BlockWrapper,Integer> value = new HashMap<BlockWrapper,Integer>();
    				NBTTagCompound snbt = (NBTTagCompound) honbt.getTag("slice_"+y);
    				boolean flag = true;
    				String n;
    				for(int i= 0; flag; ++i) {
    					if(snbt.hasKey("name_"+i)) {
    						n = snbt.getString("name_"+i);
    						String reg = n.split("[|]")[0];
    						BlockWrapper k = new BlockWrapper(Block.REGISTRY.getObject(new ResourceLocation(reg)),Integer.parseInt(n.split("[|]")[1]));
    						value.put(k,snbt.getInteger(n));
    					}
    					else {
    						flag = false;
    					}
    				}
    				graphs.put(key, value);
    			}
    		}
    	}
    	else {
    		//ChunkCoords key = new ChunkCoords(world.provider.getDimension(), chunkx, 0, chunkz);
			//OreFlowersBase.logger.log(Level.INFO, "Chunk " + key + " is missing ore data, it will be rescanned.  Chunks way out on the edge of the world may not save and cause this message to repeat next launch; do not be alarmed.");
    		//OreFlowersBase.oreCounter.generate(chunkx, chunkz, world);
    	}
	}
	
	public static void saveData(World world, int chunkx, int chunkz, NBTTagCompound nbt) {
		NBTTagCompound honbt = new NBTTagCompound();
    	for(int y=0; y < 256; y+=8) {
    		ChunkCoords key = new ChunkCoords(world.provider.getDimension(), chunkx, y, chunkz);
	    	HashMap<BlockWrapper, Integer> value = graphs.get(key);
	    	if(value != null) {
		    	NBTTagCompound snbt = new NBTTagCompound();
		    	int i = 0;
		    	for(BlockWrapper k : value.keySet()) {
		    		String kstr = k.block.getRegistryName() + "|" + k.meta;
			    	snbt.setString("name_"+i, kstr);
			    	snbt.setInteger(kstr,value.get(k));
			    	++i;
		    	}
		    	honbt.setTag("slice_"+y, snbt);
	    	}
    	}
    	honbt.setInteger("version", VERSION);
    	nbt.setTag("HardOreData", honbt);
	}

	public void clearData(World world, int cx, int cz) {
		for(int y=0; y < 256; y+=8) {
			ChunkCoords key = new ChunkCoords(world.provider.getDimension(), cx,y,cz);
			graphs.remove(key);
		}
	}
}
