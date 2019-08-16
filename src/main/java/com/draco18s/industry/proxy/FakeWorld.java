package com.draco18s.industry.proxy;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.industry.ExpandedIndustry;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.WorldInfo;

public class FakeWorld extends World {
	private static final List<PlayerEntity> EMPTY_LIST = Lists.newArrayList();

	protected FakeWorld(WorldInfo info, IProfiler profilerIn) {
		super(info, ExpandedIndustry.ModDimensionType.FILTER_DIMENSION, (worldIn, dimensionIn) -> {
			return new VoidProvider((FakeWorld)worldIn, 1, worldIn.getWorldType().createChunkGenerator(worldIn));
		}, profilerIn, true);
		
	}

	@Override
	public ITickList<Block> getPendingBlockTicks() {
		return EmptyTickList.get();
	}

	@Override
	public ITickList<Fluid> getPendingFluidTicks() {
		return EmptyTickList.get();
	}

	@Override
	public void playEvent(PlayerEntity player, int type, BlockPos pos, int data) {

	}

	@Override
	public List<? extends PlayerEntity> getPlayers() {
		return EMPTY_LIST;
	}

	@Override
	public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

	}

	@Override
	public void playSound(PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
	}

	@Override
	public void playMovingSound(PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {

	}

	@Override
	public Entity getEntityByID(int id) {
		return null;
	}

	@Override
	@Nullable
	public MapData func_217406_a(String p_217406_1_) {
		return null;
	}

	@Override
	public void func_217399_a(MapData p_217399_1_) {

	}

	@Override
	public int getNextMapId() {
		return 0;
	}

	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
		
	}

	@Override
	public Scoreboard getScoreboard() {
		return Minecraft.getInstance().world.getScoreboard();
	}

	@Override
	public RecipeManager getRecipeManager() {
		return Minecraft.getInstance().world.getRecipeManager();
	}

	@Override
	public NetworkTagManager getTags() {
		return Minecraft.getInstance().world.getTags();
	}

}
