package com.draco18s.flowers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.interfaces.IFlowerData;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;
import com.draco18s.hardlib.math.MathUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class FlowerDataHandler implements IFlowerData {
	private final Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> flowerList = new HashMap<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>>();
	private final Map<Block, BlockWrapper> wrappers = new HashMap<Block, BlockWrapper>();

	@Override
	public void trySpawnFlowerCluster(LevelAccessor world, @Nonnull BlockPos pos, BlockWrapper ore) {
		Tuple<OreFlowerDictator, List<OreFlowerData>> entry = flowerList.get(ore);
		if(entry == null) return;
		OreFlowerDictator dictator = entry.getA();
		// Worldgen heightmap: the terrain surface, ignoring trees. Every ore block under the same column rolls the
		// same chance, so richer columns spawn more clusters.
		BlockPos surface = world.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, pos);
		RandomSource r = RandomSource.create(surface.asLong());
		if(r.nextInt(dictator.spawnChance) != 0) return;
		float[] u = MathUtils.RandomInUnitCircle(r);
		BlockPos clusterPos = surface.offset(Math.round(u[0] * dictator.spawnDistance), 0, Math.round(u[1] * dictator.spawnDistance));
		if(!canWrite(world, clusterPos)) return;
		clusterPos = world.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, clusterPos);
		for(OreFlowerData dat : entry.getB()) {
			doSpawnFlowerCluster(world, clusterPos, dat.flower, r, dat.clusterNum, dat.clusterSize);
		}
	}

	@Override
	public void doSpawnFlowerCluster(LevelAccessor world, BlockPos pos, BlockState flowerState, RandomSource r, int num, int clusterRadius) {
		if(world.isClientSide()) return;
		int flags = world instanceof WorldGenRegion ? Block.UPDATE_CLIENTS : Block.UPDATE_ALL;
		for(int tries = 0; num > 0 && tries < 20; ++tries) {
			BlockPos base = pos.offset(r.nextInt(clusterRadius) - clusterRadius / 2, -5, r.nextInt(clusterRadius) - clusterRadius / 2);
			if(!canWrite(world, base)) continue;
			for(int dy = 0; dy <= 10; dy++) {
				BlockPos p = base.above(dy);
				if(canPlace(world, p, flowerState)) {
					BlockState state = flowerState;
					if(state.hasProperty(BlockProperties.BLOOM_PHASE)) {
						state = state.setValue(BlockProperties.BLOOM_PHASE, r.nextBoolean());
					}
					if(state.getBlock() instanceof DoublePlantBlock) {
						DoublePlantBlock.placeAt(world, state, p, flags);
					}
					else {
						world.setBlock(p, state, flags);
					}
					num--;
					break;
				}
			}
		}
	}

	private static boolean canPlace(LevelAccessor world, BlockPos p, BlockState flowerState) {
		if(world.isOutsideBuildHeight(p) || !isOpen(world, p)) return false;
		if(world.getBlockState(p.below()).is(flowerState.getBlock()) || !flowerState.canSurvive(world, p)) return false;
		return !(flowerState.getBlock() instanceof DoublePlantBlock) || (!world.isOutsideBuildHeight(p.above()) && isOpen(world, p.above()));
	}

	/** Air or a replaceable plant (not water, and not half of a tall plant). */
	private static boolean isOpen(LevelAccessor world, BlockPos p) {
		BlockState state = world.getBlockState(p);
		return state.isAir() || (state.canBeReplaced() && state.getFluidState().isEmpty() && !(state.getBlock() instanceof DoublePlantBlock));
	}

	/** During worldgen only the generating chunk and its neighbors may be written. */
	private static boolean canWrite(LevelAccessor world, BlockPos pos) {
		if(world instanceof WorldGenRegion region) {
			ChunkPos center = region.getCenter();
			return Math.abs(SectionPos.blockToSectionCoord(pos.getX()) - center.x) <= 1 && Math.abs(SectionPos.blockToSectionCoord(pos.getZ()) - center.z) <= 1;
		}
		return world.hasChunkAt(pos);
	}

	@Override
	public void addOreFlowerData(@Nonnull BlockWrapper ore, @Nonnull OreFlowerDictator dictator, @Nonnull OreFlowerData data) {
		Tuple<OreFlowerDictator, List<OreFlowerData>> entry = flowerList.get(ore);
		if(entry == null) {
			flowerList.put(ore, new Tuple<OreFlowerDictator, List<OreFlowerData>>(dictator, new ArrayList<OreFlowerData>(List.of(data))));
			wrappers.put(ore.block, ore);
		}
		else if(entry.getA() != dictator) {
			throw new RuntimeException("Non-matching dictators for " + ore + "! Use getDictatorForOre(BlockWrapper)");
		}
		else {
			entry.getB().add(data);
		}
	}

	@Override
	public Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> getOreList() {
		return Collections.unmodifiableMap(flowerList);
	}

	@Override
	@Nullable
	public BlockWrapper getOreWrapper(Block block) {
		return wrappers.get(block);
	}

	@Override
	@Nullable
	public OreFlowerDictator getDictatorForOre(@Nonnull BlockWrapper ore) {
		Tuple<OreFlowerDictator, List<OreFlowerData>> entry = flowerList.get(ore);
		return entry == null ? null : entry.getA();
	}

	@Override
	@Nullable
	public Iterable<OreFlowerData> getDataForOre(@Nonnull BlockWrapper ore) {
		Tuple<OreFlowerDictator, List<OreFlowerData>> entry = flowerList.get(ore);
		return entry == null ? null : entry.getB();
	}
}
