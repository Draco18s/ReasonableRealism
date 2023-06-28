package com.draco18s.harderores.client;

import java.awt.Color;

import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Block;

public class ProspectorParticleRadar extends TextureSheetParticle {
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
		super(p_107239_, p_107240_, p_107241_, p_107242_, p_107243_, p_107244_, p_107245_);
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
	protected int getLightColor(float p_107249_) {
		return 0xFFFFFF;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ALWAYS_ON_TOP;
	}

    @SuppressWarnings("deprecation")
	ParticleRenderType ALWAYS_ON_TOP = new ParticleRenderType() {
		public void begin(BufferBuilder p_107469_, TextureManager p_107470_) {
	    	  RenderSystem.disableDepthTest();
	    	  RenderSystem.depthMask(false);
	          RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
	          RenderSystem.enableBlend();
	          RenderSystem.defaultBlendFunc();
	          p_107469_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
	      }

	      public void end(Tesselator p_107472_) {
	    	  p_107472_.end();
	      }

	      public String toString() {
	         return "ALWAYS_ON_TOP";
	      }
	   };
}
