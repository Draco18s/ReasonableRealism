package com.draco18s.harderores.client;

import java.awt.Color;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Block;

public class ProspectorParticleRadar extends SingleQuadParticle {
	private final SpriteSet sprites;

	public ProspectorParticleRadar(BlockParticleOption options, ClientLevel p_107239_, double p_107240_, double p_107241_, double p_107242_, double p_107243_, double p_107244_, double p_107245_, SpriteSet spriteSet) {
		this(p_107239_, p_107240_, p_107241_, p_107242_, p_107243_, p_107244_, p_107245_, spriteSet);
		Block block = options.getState().getBlock();
		if(block instanceof IBlockMultiBreak blk) {
			Color c = blk.getProspectorParticleColor(p_107239_, options.getPos(), options.getState());
			this.rCol = c.getRed() / 255f;
			this.gCol = c.getGreen() / 255f;
			this.bCol = c.getBlue() / 255f;
		}
	}

	public ProspectorParticleRadar(ClientLevel p_107239_, double p_107240_, double p_107241_, double p_107242_, double p_107243_, double p_107244_, double p_107245_, SpriteSet spriteSet) {
		super(p_107239_, p_107240_, p_107241_, p_107242_, p_107243_, p_107244_, p_107245_, spriteSet.first());
		this.rCol = 0.25f;
		this.gCol = 0.25f;
		this.bCol = 1;

		this.lifetime = 16*3;
		this.age = 0;
		sprites = spriteSet;
		this.setSpriteFromAge(sprites);
		this.quadSize = 0.3f;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
		} else {
			this.move(0, 0, 0);
			this.setSpriteFromAge(this.sprites);
		}
	}

	@Override
	protected int getLightCoords(float p_107249_) {
		return 0xFFFFFF;
	}

	@Override
	protected SingleQuadParticle.Layer getLayer() {
		return ALWAYS_ON_TOP;
	}

	/** Translucent particles seen through blocks: no depth test, no depth writes (see ClientEventHandler, which registers it). */
	public static final RenderPipeline ALWAYS_ON_TOP_PIPELINE = RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
			.withLocation(HarderOres.getRL("pipeline/prospector_radar"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			.build();
	private static final SingleQuadParticle.Layer ALWAYS_ON_TOP = new SingleQuadParticle.Layer(true, TextureAtlas.LOCATION_PARTICLES, ALWAYS_ON_TOP_PIPELINE);
}
