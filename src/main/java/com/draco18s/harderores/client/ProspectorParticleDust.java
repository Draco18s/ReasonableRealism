package com.draco18s.harderores.client;

import java.awt.Color;

import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Block;

public class ProspectorParticleDust extends TextureSheetParticle {
	private final SpriteSet sprites;

	public ProspectorParticleDust(BlockParticleOption options, ClientLevel p_107239_, double p_107240_, double p_107241_, double p_107242_, double p_107243_, double p_107244_, double p_107245_, SpriteSet spriteSet) {
		this(p_107239_, p_107240_, p_107241_, p_107242_, p_107243_, p_107244_, p_107245_, spriteSet);
		Block block = options.getState().getBlock();
		if(block instanceof IBlockMultiBreak blk) {
			Color c = blk.getProspectorParticleColor(p_107239_, options.getPos(), options.getState());
			this.rCol = c.getRed() / 255f;
			this.gCol = c.getGreen() / 255f;
			this.bCol = c.getBlue() / 255f;
		}
	}

	public ProspectorParticleDust(ClientLevel p_107239_, double p_107240_, double p_107241_, double p_107242_, double p_107243_, double p_107244_, double p_107245_, SpriteSet spriteSet) {
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

	/*@Override
	public void tick() {
		this.xo = this.x;
	      this.yo = this.y;
	      this.zo = this.z;
	      if (this.age++ >= this.lifetime) {
	         this.remove();
	      } else {
	         this.yd -= 0.04D * (double)this.gravity;
	         this.move(this.xd, this.yd, this.zd);
	         if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
	            this.xd *= 1.1D;
	            this.zd *= 1.1D;
	         }

	         this.xd *= (double)this.friction;
	         this.yd *= (double)this.friction;
	         this.zd *= (double)this.friction;
	         if (this.onGround) {
	            this.xd *= (double)0.7F;
	            this.zd *= (double)0.7F;
	         }

	      }
	}*/

	@Override
	protected int getLightColor(float p_107249_) {
		return 0xFFFFFF;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
}
