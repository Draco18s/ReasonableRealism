package com.draco18s.harderores.client;

import com.draco18s.harderores.HarderOres;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * The ore cart (1.12's ModelOreCart): a minecart's box with a floor and an ore pile inside that rises as the cart
 * fills. The chute is a model of its own, since it turns toward the side the cart empties onto.
 */
public class OreCartModel extends EntityModel<OreCartRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(HarderOres.getRL("ore_cart"), "main");
	public static final ModelLayerLocation CHUTE_LAYER = new ModelLayerLocation(HarderOres.getRL("ore_cart"), "chute");
	private static final float PILE_Y = 4.01F;

	private final ModelPart pile;

	public OreCartModel(ModelPart root) {
		super(root);
		pile = root.getChild("pile");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 10).addBox(-10.0F, -8.0F, -1.0F, 20.0F, 16.0F, 2.0F),
				PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, (float)(Math.PI / 2), 0.0F, 0.0F));
		root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F),
				PartPose.offsetAndRotation(-9.0F, 4.0F, 0.0F, 0.0F, (float)(Math.PI * 3.0 / 2.0), 0.0F));
		root.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F),
				PartPose.offsetAndRotation(9.0F, 4.0F, 0.0F, 0.0F, (float)(Math.PI / 2), 0.0F));
		root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F),
				PartPose.offsetAndRotation(0.0F, 4.0F, -7.0F, 0.0F, (float)Math.PI, 0.0F));
		root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F),
				PartPose.offset(0.0F, 4.0F, 7.0F));
		root.addOrReplaceChild("floor", CubeListBuilder.create().texOffs(44, 10).addBox(-9.0F, -7.0F, -1.0F, 18.0F, 14.0F, 1.0F),
				PartPose.offsetAndRotation(0.0F, 4.1F, 0.0F, (float)(-Math.PI / 2), 0.0F, 0.0F));
		root.addOrReplaceChild("pile", CubeListBuilder.create().texOffs(44, 10).addBox(-9.0F, -7.0F, -1.0F, 18.0F, 14.0F, 1.0F),
				PartPose.offsetAndRotation(0.0F, PILE_Y, 0.0F, (float)(-Math.PI / 2), 0.0F, 0.0F));
		return LayerDefinition.create(mesh, 64, 32);
	}

	public static LayerDefinition createChuteLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition chute = mesh.getRoot().addOrReplaceChild("chute", CubeListBuilder.create().texOffs(36, 0)
				.addBox(-2.0F, -5.0F, -10.0F, 1.0F, 3.0F, 3.0F)
				.addBox(2.0F, -5.0F, -10.0F, 1.0F, 3.0F, 3.0F),
				PartPose.offsetAndRotation(0.0F, 4.1F, 0.0F, 0.25F, 0.0F, 0.0F));
		chute.addOrReplaceChild("chute_bottom", CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, -3.0F, -10.0F, 3.0F, 1.0F, 3.0F), PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, 32);
	}

	/** Half a pixel per point of comparator signal. */
	@Override
	public void setupAnim(OreCartRenderState state) {
		super.setupAnim(state);
		pile.y = PILE_Y - state.fullness * 0.5F;
	}

	public static class Chute extends EntityModel<OreCartRenderState> {
		public Chute(ModelPart root) {
			super(root);
		}
	}
}
