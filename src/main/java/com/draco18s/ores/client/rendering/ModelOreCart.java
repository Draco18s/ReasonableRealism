package com.draco18s.ores.client.rendering;

import com.draco18s.ores.entities.EntityOreMinecart;
import com.draco18s.ores.entities.EntityOreMinecart.DumpDir;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ModelOreCart extends ModelBase {

	public ModelRenderer[] sideModels = new ModelRenderer[7];
	public ModelRenderer[] chuteModels = new ModelRenderer[3]; 

	public ModelOreCart() {
		this.sideModels[0] = new ModelRenderer(this, 0, 10);
		this.sideModels[1] = new ModelRenderer(this, 0, 0);
		this.sideModels[2] = new ModelRenderer(this, 0, 0);
		this.sideModels[3] = new ModelRenderer(this, 0, 0);
		this.sideModels[4] = new ModelRenderer(this, 0, 0);
		this.sideModels[5] = new ModelRenderer(this, 44, 10);
		this.sideModels[6] = new ModelRenderer(this, 44, 10);
		this.chuteModels[0] = new ModelRenderer(this, 36, 0);
		this.chuteModels[1] = new ModelRenderer(this, 44, 0);
		int i = 20;
		int j = 8;
		int k = 16;
		int l = 4;
		this.sideModels[0].addBox(-10.0F, -8.0F, -1.0F, 20, 16, 2, 0.0F);
		this.sideModels[0].setRotationPoint(0.0F, 4.0F, 0.0F);
		this.sideModels[5].addBox(-9.0F, -7.0F, -1.0F, 18, 14, 1, 0.0F);
		this.sideModels[5].setRotationPoint(0.0F, 4.0F, 0.0F);
		this.sideModels[1].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.sideModels[1].setRotationPoint(-9.0F, 4.0F, 0.0F);
		this.sideModels[2].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.sideModels[2].setRotationPoint(9.0F, 4.0F, 0.0F);
		this.sideModels[3].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.sideModels[3].setRotationPoint(0.0F, 4.0F, -7.0F);
		this.sideModels[4].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.sideModels[4].setRotationPoint(0.0F, 4.0F, 7.0F);

		this.sideModels[6].addBox(-9.0F, -7.0F, -1.0F, 18, 14, 1, 0.0F);
		this.sideModels[6].setRotationPoint(0.0F, 4.0F, 0.0F);
		
		this.chuteModels[0].addBox(-2.0F, -5.0F, -10.0F, 1, 3, 3, 0.0F);
		this.chuteModels[0].addBox( 2.0F, -5.0F, -10.0F, 1, 3, 3, 0.0F);
		this.chuteModels[1].addBox(-1.0F, -3.0F, -10.0F, 3, 1, 3, 0.0F);
		this.chuteModels[0].setRotationPoint(0.0F, 4.0F, 0.0F);

		this.chuteModels[1].addBox(-1.0F, -3.0F, -10.0F, 3, 1, 3, 0.0F);
		this.chuteModels[0].addChild(this.chuteModels[1]);

		this.sideModels[0].rotateAngleX = ((float)Math.PI / 2F);
		this.sideModels[1].rotateAngleY = ((float)Math.PI * 3F / 2F);
		this.sideModels[2].rotateAngleY = ((float)Math.PI / 2F);
		this.sideModels[3].rotateAngleY = (float)Math.PI;
		this.sideModels[5].rotateAngleX = -((float)Math.PI / 2F);
		this.sideModels[6].rotateAngleX = -((float)Math.PI / 2F);

		this.chuteModels[0].rotateAngleX = 0.25f;
		//this.sideModels[6].rotateAngleY = (float)Math.PI;
		//this.sideModels[7].rotateAngleY = (float)Math.PI;
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		EntityOreMinecart ent = (EntityOreMinecart)entityIn;
		this.sideModels[6].rotationPointY = 4.01f - ((EntityOreMinecart)entityIn).getInventoryFullness();
		
		this.sideModels[5].rotationPointY = 4.0F - ageInTicks;

		for (int i = 0; i < 7; ++i) {
			this.sideModels[i].render(scale);
		}
	}
	
	public void renderChute(Entity entityIn, float ageInTicks, float scale) {
		this.chuteModels[0].rotationPointY = 4.0F - ageInTicks;

		this.chuteModels[0].render(scale);
	}
}
