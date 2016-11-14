package com.draco18s.ores.client.rendering;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class BakedModelBasicSluice implements IBakedModel {
	IBakedModel waterFlow;
	IBakedModel basePlate;

	public BakedModelBasicSluice(IBakedModel water, IBakedModel base) {
		waterFlow = water;
		basePlate = base;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
		list.addAll(waterFlow.getQuads(state, side, rand));
		list.addAll(basePlate.getQuads(state, side, rand));
		return list;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return waterFlow.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return waterFlow.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return waterFlow.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return waterFlow.getParticleTexture();
	}

	@Override
	@Deprecated
	public ItemCameraTransforms getItemCameraTransforms() {
		return waterFlow.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return waterFlow.getOverrides();
	}
}
