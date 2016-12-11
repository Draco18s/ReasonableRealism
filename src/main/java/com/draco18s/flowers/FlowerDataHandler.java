package com.draco18s.flowers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.interfaces.IFlowerData;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;
import com.draco18s.hardlib.math.MathUtils;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class FlowerDataHandler implements IFlowerData {
	private Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> flowerList = new HashMap();
	
	@Override
	public void trySpawnFlowerCluster(World world, @Nonnull BlockPos pos, BlockWrapper ore) {
		OreFlowerDictator dictator = flowerList.get(ore).getFirst();
		Iterable<OreFlowerData> data = getDataForOre(ore);
		if(dictator == null || data == null) return;
		do {
			pos = pos.up();
		} while(world.getBlockState(pos).getMaterial() != Material.AIR && world.getLightFor(EnumSkyBlock.SKY, pos) < 8);
		if(pos.getY() >= 256) return;
		
		Random r = new Random(pos.toLong());
		if(r.nextInt(dictator.spawnChance) != 0) return;
		
		float[] u = MathUtils.RandomInUnitCircle(r);
		
		Iterator<OreFlowerData> it = data.iterator();
		while(it.hasNext()) {
			OreFlowerData dat = it.next();
			int radius = dictator.spawnDistance;
			BlockPos clusterPos = pos.add(Math.round(u[0]*radius), 0, Math.round(u[1]*radius));
			doSpawnFlowerCluster(world, clusterPos, dat.flower.withProperty(Props.FLOWER_STALK, false), r, dat.clusterNum, dat.clusterSize, dat.flower.getValue(Props.FLOWER_STALK), dat.twoBlockChance);
		}
	}

	@Override
	public void doSpawnFlowerCluster(World world, BlockPos pos, IBlockState flowerState, Random r, int num, int clusterRadius, boolean canBeTallPlant, int tallChance) {
		if(world.isRemote) {
			return;
		}
		int fails = 0;
		BlockPos newPos;
		boolean replaceLeaves = num > 1;
		while(num > 0 && fails < 20) {
			newPos = pos.add(r.nextInt(clusterRadius) - (clusterRadius/2), -5, r.nextInt(clusterRadius) - (clusterRadius/2));
			Iterable<BlockPos> list = BlockPos.getAllInBox(newPos, newPos.up(10));
			Iterator<BlockPos> it = list.iterator();
			while(it.hasNext()) {
				BlockPos p = it.next();
				IBlockState wb = world.getBlockState(p);
				IBlockState pDown = world.getBlockState(p.down());
				if(pDown.isFullCube() && flowerState.getBlock().canPlaceBlockAt(world, p) && (wb.getBlock().isReplaceable(world, p) || wb.getMaterial() == Material.LEAVES) && !(wb.getBlock() instanceof BlockLiquid || wb.getBlock() instanceof IFluidBlock)) {
					int ra = 1;
					if(canBeTallPlant) {
						ra = r.nextInt(tallChance);
					}
					if(canBeTallPlant && ra == 0 && world.getBlockState(p.up()).getMaterial() == Material.AIR) {
						world.setBlockState(p.up(), flowerState, 3);
						world.setBlockState(p, flowerState.withProperty(Props.FLOWER_STALK, true), 3);
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


	@Override
	public void addOreFlowerData(@Nonnull BlockWrapper ore, @Nonnull OreFlowerDictator dictator, @Nonnull OreFlowerData data) {
		if(flowerList.containsKey(ore)) {
			if(flowerList.get(ore).getFirst() != dictator) {
				throw new RuntimeException("Non-matching dictators for " + ore.block.getRegistryName() + "! Use getDictatorForOre(BlockWrapper)");
			}
			List<OreFlowerData> val = flowerList.get(ore).getSecond();
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
			return flowerList.get(ore).getFirst();
		}
		return null;
	}
	
	@Override
	@Nullable
	public Iterable<OreFlowerData> getDataForOre(@Nonnull BlockWrapper ore) {
		if(flowerList.containsKey(ore)) {
			return flowerList.get(ore).getSecond();
		}
		return null;
	}

	@Override
	@Nullable
	public IBlockState getDefaultFlower(@Nonnull IProperty prop) {
		if(OreFlowersBase.oreFlowers1.getBlockState().getProperties().contains(prop))
			return OreFlowersBase.oreFlowers1.getDefaultState();
		if(OreFlowersBase.oreFlowersDesert1.getBlockState().getProperties().contains(prop))
			return OreFlowersBase.oreFlowersDesert1.getDefaultState();
		
		if(OreFlowersBase.oreFlowers2.getBlockState().getProperties().contains(prop))
			return OreFlowersBase.oreFlowers2.getDefaultState();
		if(OreFlowersBase.oreFlowersDesert2.getBlockState().getProperties().contains(prop))
			return OreFlowersBase.oreFlowersDesert2.getDefaultState();
		
		if(OreFlowersBase.oreFlowers3.getBlockState().getProperties().contains(prop))
			return OreFlowersBase.oreFlowers3.getDefaultState();
		if(OreFlowersBase.oreFlowersDesert3.getBlockState().getProperties().contains(prop))
			return OreFlowersBase.oreFlowersDesert3.getDefaultState();
		return null;
	}
}
