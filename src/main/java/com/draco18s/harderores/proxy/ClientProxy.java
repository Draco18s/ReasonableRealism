package com.draco18s.harderores.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.network.ToClientMessageOreParticles;
import com.draco18s.hardlib.proxy.IProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientProxy implements IProxy {
	/*private enum ParticleTypes {
		DUST,RADAR
	}*/

	@Override
	public <MSG> void spawnParticles(MSG message, Supplier<Context> ctx) {
		@SuppressWarnings("resource")
		LocalPlayer p = Minecraft.getInstance().player;
		List<Block> states = new ArrayList<Block>();
		ToClientMessageOreParticles msg= ((ToClientMessageOreParticles)message);
		for(BlockPos pos : msg.oresAt) {
			BlockState state = p.level.getBlockState(pos);
			BlockParticleOption opts = new BlockParticleOption(HarderOres.ModParticleTypes.prospector_radar, state);
			opts.setPos(pos);
			spawnParticlesAtCenter(p.level, pos, opts, UniformInt.of(0, 2));//.spawnParticleBelow(p.level, pos, p.level.random, opts);
			if(!states.contains(state.getBlock())) {
				states.add(state.getBlock());
				BlockParticleOption dustOpts = new BlockParticleOption(HarderOres.ModParticleTypes.prospector_dust, state);
				ParticleUtils.spawnParticleOnFace(p.level, msg.eventAt.above(), Direction.DOWN, dustOpts, new Vec3(0,0,0), 0.55D);//.spawnParticleBelow(p.level, msg.eventAt.above(), p.level.random, dustOpts);
			}
		}
	}

	private void spawnParticlesAtCenter(Level world, BlockPos pos, ParticleOptions opts, UniformInt randQnt) {
		Vec3 vec3 = Vec3.atCenterOf(pos);
		int i = randQnt.sample(world.random);
		for(int j = 0; j < 1 && j < i; ++j) {
			double d0 = vec3.x;
			double d1 = vec3.y;
			double d2 = vec3.z;
			world.addParticle(opts, d0, d1, d2, 0, 0, 0);
		}
	}
}
