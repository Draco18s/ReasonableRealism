package com.draco18s.flowers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.draco18s.hardlib.api.interfaces.IFlowerData;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;
import com.draco18s.hardlib.math.MathUtils;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class FlowerDataHandler implements IFlowerData {
	private Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> flowerList = new HashMap<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>>();
	
	@Override
	public void trySpawnFlowerCluster(World world, @Nonnull BlockPos pos, BlockWrapper ore) {
		OreFlowerDictator dictator = flowerList.get(ore).getA();
		Iterable<OreFlowerData> data = getDataForOre(ore);
		if(dictator == null || data == null) return;
		do {
			pos = pos.up();
		} while(!checkMaterial(world, pos) && world.getLightFor(LightType.SKY, pos) < 8);
		if(pos.getY() >= 256) return;
		
		//MathHelper.getPositionRandom
		Random r = new Random(pos.toLong());
		if(r.nextInt(dictator.spawnChance) != 0) return;
		
		float[] u = MathUtils.RandomInUnitCircle(r);
		
		Iterator<OreFlowerData> it = data.iterator();
		while(it.hasNext()) {
			OreFlowerData dat = it.next();
			int radius = dictator.spawnDistance;
			BlockPos clusterPos = pos.add(Math.round(u[0]*radius), 0, Math.round(u[1]*radius));
			doSpawnFlowerCluster(world, clusterPos, dat.flower, r, dat.clusterNum, dat.clusterSize, dat.flower.has(BlockStateProperties.DOUBLE_BLOCK_HALF), dat.twoBlockChance);
		}
	}

	@Override
	public void doSpawnFlowerCluster(World world, BlockPos pos, BlockState flowerState, Random r, int num, int clusterRadius, boolean canBeTallPlant, int tallChance) {
		if(world.isRemote) {
			return;
		}
		int fails = 0;
		BlockPos newPos;
		//boolean replaceLeaves = num > 1;
		while(num > 0 && fails < 20) {
			newPos = pos.add(r.nextInt(clusterRadius) - (clusterRadius/2), -5, r.nextInt(clusterRadius) - (clusterRadius/2));
			Stream<BlockPos> list = BlockPos.getAllInBox(newPos, newPos.up(10));
			Iterator<BlockPos> it = list.iterator();
			while(it.hasNext()) {
				BlockPos p = it.next();
				BlockState wb = world.getBlockState(p);
				BlockState pDown = world.getBlockState(p.down());
				if(pDown.getBlock() != flowerState.getBlock() && flowerState.isValidPosition(world, p) && (wb.getMaterial().isReplaceable() || checkMaterial(world, p)) && !(wb.getBlock() instanceof FlowingFluidBlock || wb.getBlock() instanceof IFluidBlock)) {
					int ra = 1;
					if(canBeTallPlant) {
						ra = r.nextInt(tallChance);
					}
					if(canBeTallPlant && ra == 0 && checkMaterial(world, p.up())) {
						world.setBlockState(p.up(), flowerState.with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 3);
						world.setBlockState(p, flowerState.with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), 3);
					}
					else {
						world.setBlockState(p, flowerState, 3);
					}
					num--;
					break;
				}
			}
			++fails;
		}
	}

	private boolean checkMaterial(World world, BlockPos p) {
		Material mat = world.getBlockState(p).getMaterial();
		return mat == Material.AIR || mat == Material.LEAVES;
	}


	@Override
	public void addOreFlowerData(@Nonnull BlockWrapper ore, @Nonnull OreFlowerDictator dictator, @Nonnull OreFlowerData data) {
		if(flowerList.containsKey(ore)) {
			if(flowerList.get(ore).getA() != dictator) {
				throw new RuntimeException("Non-matching dictators for " + ore.block.getRegistryName() + "! Use getDictatorForOre(BlockWrapper)");
			}
			List<OreFlowerData> val = flowerList.get(ore).getB();
			val.add(data);
		}
		else {
			List<OreFlowerData> val = new ArrayList<OreFlowerData>();
			val.add(data);
			flowerList.put(ore, new Tuple<OreFlowerDictator, List<OreFlowerData>>(dictator, val));
		}
	}
	

	@Override
	public Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> getOreList() {
		return Collections.unmodifiableMap(flowerList);
	}
	
	@Override
	@Nullable
	public OreFlowerDictator getDictatorForOre(@Nonnull BlockWrapper ore) {
		if(flowerList.containsKey(ore)) {
			return flowerList.get(ore).getA();
		}
		return null;
	}
	
	@Override
	@Nullable
	public Iterable<OreFlowerData> getDataForOre(@Nonnull BlockWrapper ore) {
		if(flowerList.containsKey(ore)) {
			return flowerList.get(ore).getB();
		}
		return null;
	}
}