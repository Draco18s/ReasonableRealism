package com.draco18s.harderores.client;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.OreMinecart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Draws the ore cart in place of the minecart model (positioned as AbstractMinecartRenderer positions a minecart),
 * with its chute turned toward the side of the track it empties onto.
 */
public class OreCartRenderer extends AbstractMinecartRenderer<OreMinecart, OreCartRenderState> {
	private static final Identifier TEXTURE = HarderOres.getRL("textures/entity/ore_cart.png");
	private final OreCartModel cartModel;
	private final OreCartModel.Chute chuteModel;

	public OreCartRenderer(EntityRendererProvider.Context context) {
		super(context, ModelLayers.MINECART);
		cartModel = new OreCartModel(context.bakeLayer(OreCartModel.LAYER));
		chuteModel = new OreCartModel.Chute(context.bakeLayer(OreCartModel.CHUTE_LAYER));
	}

	@Override
	public OreCartRenderState createRenderState() {
		return new OreCartRenderState();
	}

	@Override
	public void extractRenderState(OreMinecart entity, OreCartRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.fullness = entity.getFullness();
		state.dumpSide = entity.getDumpSide();
	}

	/** As AbstractMinecartRenderer.submit (and EntityRenderer's leashes and name), but with the ore cart's models. */
	@Override
	public void submit(OreCartRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if(state.leashStates != null) {
			for(EntityRenderState.LeashState leashState : state.leashStates) {
				collector.submitLeash(poseStack, leashState);
			}
		}
		this.submitNameDisplay(state, poseStack, collector, camera);

		poseStack.pushPose();
		long seed = state.offsetSeed;
		float offsetX = (((float)(seed >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float offsetY = (((float)(seed >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float offsetZ = (((float)(seed >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		poseStack.translate(offsetX, offsetY, offsetZ);
		float yaw = state.isNewRender ? newRender(state, poseStack) : oldRender(state, poseStack);

		float hurt = state.hurtTime;
		if(hurt > 0.0F) {
			poseStack.rotateDegrees(Axis.XP, Mth.sin(hurt) * hurt * state.damageTime / 10.0F * state.hurtDir);
		}

		// The chute points along -Z; turn it from the cart's yaw to face the dump side.
		float chuteYaw = (float)Math.toDegrees(Math.atan2(-state.dumpSide.getStepX(), -state.dumpSide.getStepZ()));
		poseStack.pushPose();
		poseStack.rotateDegrees(Axis.YP, chuteYaw - yaw);
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		collector.submitModel(chuteModel, state, poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();

		poseStack.scale(-1.0F, -1.0F, 1.0F);
		collector.submitModel(cartModel, state, poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();
	}

	/** AbstractMinecartRenderer.newRender; returns the yaw it turned the cart to. */
	private static float newRender(OreCartRenderState state, PoseStack poseStack) {
		poseStack.rotateDegrees(Axis.YP, state.yRot);
		poseStack.rotateDegrees(Axis.ZP, -state.xRot);
		poseStack.translate(0.0F, 0.375F, 0.0F);
		return state.yRot;
	}

	/** AbstractMinecartRenderer.oldRender; returns the yaw it turned the cart to. */
	private static float oldRender(OreCartRenderState state, PoseStack poseStack) {
		float xRot = state.xRot;
		float rotation = state.yRot;
		if(state.posOnRail != null && state.frontPos != null && state.backPos != null) {
			Vec3 frontPos = state.frontPos;
			Vec3 backPos = state.backPos;
			poseStack.translate(state.posOnRail.x - state.x, (frontPos.y + backPos.y) / 2.0 - state.y, state.posOnRail.z - state.z);
			Vec3 direction = backPos.add(-frontPos.x, -frontPos.y, -frontPos.z);
			if(direction.length() != 0.0) {
				direction = direction.normalize();
				rotation = (float)(Math.atan2(direction.z, direction.x) * 180.0 / Math.PI);
				xRot = (float)(Math.atan(direction.y) * 73.0);
			}
		}
		poseStack.translate(0.0F, 0.375F, 0.0F);
		poseStack.rotateDegrees(Axis.YP, 180.0F - rotation);
		poseStack.rotateDegrees(Axis.ZP, -xRot);
		return 180.0F - rotation;
	}
}
