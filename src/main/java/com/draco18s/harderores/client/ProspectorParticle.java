package com.draco18s.harderores.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ProspectorParticle extends Particle {
	private static final ResourceLocation PARTICLES_TEXTURE = new ResourceLocation("harderores:textures/entity/particles.png");
	private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);

	public ProspectorParticle(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.particleRed = 0.25f;
		this.particleGreen = 0.25f;
		this.particleBlue = 1;

		this.maxAge = 16*3;
		this.age = 0;
	}

	public ProspectorParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.particleRed = 0.25f;
		this.particleGreen = 0.25f;
		this.particleBlue = 1;

		this.maxAge = 16*3;
		this.age = 0;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		GL11.glPushMatrix();
		GL11.glDepthFunc(GL11.GL_ALWAYS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, this.particleAlpha);
		GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();

		Minecraft.getInstance().getTextureManager().bindTexture(PARTICLES_TEXTURE);
		int i = (int)(((float)this.age + partialTicks) * 15.0F / (float)this.maxAge);

		if (i <= 15)
		{
			float f = (float)(i % 16) / 16.0F;
			float f1 = f + 0.0625f;
			float f2 = (float)(i / 16) / 16.0F;
			float f3 = f2 + 0.0625f;
			float f4 = .3f;//2.0F * this.size;
			float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
			float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
			float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			RenderHelper.disableStandardItemLighting();
			buffer.begin(7, VERTEX_FORMAT);
			buffer.pos(
					(double)(f5 - rotationX * f4 - rotationXY * f4),
					(double)(f6 - rotationZ * f4),
					(double)(f7 - rotationYZ * f4 - rotationXZ * f4))
					.tex((double)f1, (double)f3)
					.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
					.lightmap(0, 240)
					.normal(0.0F, 1.0F, 0.0F)
					.endVertex();
			buffer.pos((double)(f5 - rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 - rotationYZ * f4 + rotationXZ * f4)).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
			buffer.pos((double)(f5 + rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 + rotationYZ * f4 + rotationXZ * f4)).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
			buffer.pos((double)(f5 + rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 + rotationYZ * f4 - rotationXZ * f4)).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
			Tessellator.getInstance().draw();
			GlStateManager.enableLighting();
		}

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glPopMatrix();
		GlStateManager.enableLighting();
	}

	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.age++ >= this.maxAge)
		{
			this.setExpired();
		}

		this.move(0,0,0);
	}

	/*public int getFXLayer() {
		return 3;
	}*/

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.CUSTOM;
	}
}