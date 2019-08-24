package com.draco18s.harderores.client.rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.draco18s.harderores.block.SluiceBlock;
import com.draco18s.harderores.entity.SluiceTileEntity;
import com.draco18s.hardlib.client.ModelsCache;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

public class BakedModelBasicSluice implements IDynamicBakedModel {
	IBakedModel waterFlow;
	IBakedModel basePlate;
	private final TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];

	public BakedModelBasicSluice(IBakedModel water, IBakedModel base) {
		waterFlow = water;
		basePlate = base;
		AtlasTexture atlastexture = Minecraft.getInstance().getTextureMap();
		this.atlasSpritesWater[0] = Minecraft.getInstance().getModelManager().getBlockModelShapes().getModel(Blocks.WATER.getDefaultState()).getParticleTexture();
		this.atlasSpritesWater[1] = atlastexture.getSprite(ModelBakery.LOCATION_WATER_FLOW);
	}

	/*@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
		list.addAll(waterFlow.getQuads(state, side, rand, null));
		list.addAll(basePlate.getQuads(state, side, rand, null));
		return list;
	}*/

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
		list.addAll(basePlate.getQuads(state, side, rand, null));
		if(state.get(SluiceBlock.FLOWING)) {
			float[] corners = new float[4];
			corners[0] = Math.max(extraData.getData(SluiceTileEntity.LEVEL_CORNERS_0), 1f/16);//NW
			corners[1] = Math.max(extraData.getData(SluiceTileEntity.LEVEL_CORNERS_1), 1f/16);//SW
			corners[2] = Math.max(extraData.getData(SluiceTileEntity.LEVEL_CORNERS_2), 1f/16);//NE
			corners[3] = Math.max(extraData.getData(SluiceTileEntity.LEVEL_CORNERS_3), 1f/16);//SE
			int color = (0xFF000000) | extraData.getData(SluiceTileEntity.WATER_COLOR);
			int red = (color & 0xFF0000) >> 16;
			int green = (color & 0x00FF00) >> 8;
			int blue = color & 0x0000FF;
			Direction dir = state.get(BlockStateProperties.HORIZONTAL_FACING);
			color = (0xC0000000) | ((blue) << 16) | ((green) << 8) | (red);
			FaceBakery faceBakery = new FaceBakery();
			BlockFaceUV uvs;// = new BlockFaceUV(new float[] {0, 0, 8, 8}, 0);
			Vector3f[] verts = new Vector3f[] {
					new Vector3f(0,corners[0],0),
					new Vector3f(0,corners[1],1),
					new Vector3f(1,corners[2],1),
					new Vector3f(1,corners[3],0)};
			switch(dir) {
				case NORTH:
					uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
					break;
				case SOUTH:
					uvs = new BlockFaceUV(new float[] {0, 0, 8, 8}, 0);
					break;
				case EAST:
					verts = new Vector3f[] {
							new Vector3f(1,corners[3],0),
							new Vector3f(0,corners[0],0),
							new Vector3f(0,corners[1],1),
							new Vector3f(1,corners[2],1)};
					uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
					break;
				case WEST:
					verts = new Vector3f[] {
							new Vector3f(1,corners[3],0),
							new Vector3f(0,corners[0],0),
							new Vector3f(0,corners[1],1),
							new Vector3f(1,corners[2],1)};
					uvs = new BlockFaceUV(new float[] {0, 0, 8, 8}, 0);
					break;
				default:
					verts = new Vector3f[] {
							new Vector3f(0,0,0),
							new Vector3f(0,0,0),
							new Vector3f(0,0,0),
							new Vector3f(0,0,0)};
					uvs = new BlockFaceUV(new float[] {0, 0, 8, 8}, 0);
					break;
			}
			BlockPartFace partFace = new BlockPartFace(Direction.UP,0,"",uvs);
			ISprite isprite = new BasicState(ModelsCache.DEFAULTMODELSTATE,true);

			BlockPartRotation rotation = new BlockPartRotation(new Vector3f(0.5f,0,0.5f), Direction.Axis.Y, 0, false);
			boolean diffuseLight = false;
			BakedQuad q0 = faceBakery.makeBakedQuad(new Vector3f(0,corners[1]*16,0), new Vector3f(16,corners[1]*16,16), partFace, 
					atlasSpritesWater[1], Direction.UP, isprite, rotation, diffuseLight);
			//list.addAll(waterFlow.getQuads(state, side, rand, null));
			int[] data = new int[28];
			bakeQuad(data, verts, atlasSpritesWater[1], uvs, color);
			/*int[] data = new int[]{
			0, 1063423836, 0, -1, 1023434916, 960579630, 32512, 
			0, 1063423836, 1065353216, -1, 1023434916, 1031749304, 32512,
			1065353216, 1063423836, 1065353216, -1, 1031774044, 1031749304, 32512,
			1065353216, 1063423836, 0, -1, 1031774044, 960579630, 32512};*/
			q0 = new BakedQuad(data, 0, Direction.UP, atlasSpritesWater[1], diffuseLight, DefaultVertexFormats.BLOCK);
			list.add(q0);
			
			Direction sideFace = dir.rotateAround(Axis.Y);
			data = new int[28];
			switch(sideFace) {
				case NORTH:
					verts = new Vector3f[] { //[0],[2]
							new Vector3f(1,2f/16,     1),//bottom
							new Vector3f(1,corners[2],1),//NW
							new Vector3f(0,corners[0],1),//NE
							new Vector3f(0,2f/16,     1)};//bottom
					uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
					break;
				case SOUTH:
					verts = new Vector3f[] { //[0],[2]
							new Vector3f(0,2f/16,     0),//bottom
							new Vector3f(0,corners[1],0),//NE
							new Vector3f(1,corners[3],0),//NW
							new Vector3f(1,2f/16,     0)};//bottom
					uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
					break;
				case EAST:
					verts = new Vector3f[] { //[0],[2]
							new Vector3f(0,2f/16,     1),//bottom
							new Vector3f(0,corners[2],1),//NW
							new Vector3f(0,corners[3],0),//NE
							new Vector3f(0,2f/16,     0)};//bottom
					uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
					break;
				case WEST:
					verts = new Vector3f[] { //[0],[2]
							new Vector3f(1,2f/16,     0),//bottom
							new Vector3f(1,corners[3],0),//NE
							new Vector3f(1,corners[2],1),//NW
							new Vector3f(1,2f/16,     1)};//bottom
					uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
					break;
				default:
					verts = new Vector3f[] {
							new Vector3f(0,0,0),
							new Vector3f(0,0,0),
							new Vector3f(0,0,0),
							new Vector3f(0,0,0)};
					uvs = new BlockFaceUV(new float[] {0, 0, 8, 8}, 0);
					break;
			}
			bakeQuad(data, verts, atlasSpritesWater[1], uvs, color);
			q0 = new BakedQuad(data, 0, sideFace, atlasSpritesWater[1], diffuseLight, DefaultVertexFormats.BLOCK);
			list.add(q0);
			
			sideFace = sideFace.getOpposite();
			data = new int[28];
			switch(sideFace) {
				case NORTH:
					verts = new Vector3f[] { //[0],[2]
							new Vector3f(1,2f/16,     1),//bottom
							new Vector3f(1,corners[2],1),//NW
							new Vector3f(0,corners[0],1),//NE
							new Vector3f(0,2f/16,     1)};//bottom
					uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
					break;
				case SOUTH:
					verts = new Vector3f[] { //[0],[2]
							new Vector3f(0,2f/16,     0),//bottom
							new Vector3f(0,corners[1],0),//NE
							new Vector3f(1,corners[3],0),//NW
							new Vector3f(1,2f/16,     0)};//bottom
					uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
					break;
				case EAST:
					verts = new Vector3f[] { //[0],[2]
							new Vector3f(0,2f/16,     1),//bottom
							new Vector3f(0,corners[2],1),//NW
							new Vector3f(0,corners[3],0),//NE
							new Vector3f(0,2f/16,     0)};//bottom
					uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
					break;
				case WEST:
					verts = new Vector3f[] { //[0],[2]
							new Vector3f(1,2f/16,     0),//bottom
							new Vector3f(1,corners[3],0),//NE
							new Vector3f(1,corners[2],1),//NW
							new Vector3f(1,2f/16,     1)};//bottom
					uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
					break;
				default:
					verts = new Vector3f[] {
							new Vector3f(0,0,0),
							new Vector3f(0,0,0),
							new Vector3f(0,0,0),
							new Vector3f(0,0,0)};
					uvs = new BlockFaceUV(new float[] {0, 0, 8, 8}, 0);
					break;
			}
			bakeQuad(data, verts, atlasSpritesWater[1], uvs, color);
			q0 = new BakedQuad(data, 0, sideFace, atlasSpritesWater[1], diffuseLight, DefaultVertexFormats.BLOCK);
			list.add(q0);
		}
		return list;
	}

	private void bakeQuad(int[] dataOut, Vector3f[] verts, TextureAtlasSprite sprite, BlockFaceUV faceUV, int shadeColor) {
		for(int i = 0; i < 4; i++) {
			storeVertexData(dataOut, i, i, verts[i], shadeColor, sprite, faceUV);
		}
	}

	private void storeVertexData(int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV) {
		int i = storeIndex * 7;
		faceData[i] = Float.floatToRawIntBits(position.getX());
		faceData[i + 1] = Float.floatToRawIntBits(position.getY());
		faceData[i + 2] = Float.floatToRawIntBits(position.getZ());
		faceData[i + 3] = shadeColor;
		faceData[i + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU((double)faceUV.getVertexU(vertexIndex) * .999 + faceUV.getVertexU((vertexIndex + 2) % 4) * .001));
		faceData[i + 5] = Float.floatToRawIntBits(sprite.getInterpolatedV((double)faceUV.getVertexV(vertexIndex) * .999 + faceUV.getVertexV((vertexIndex + 2) % 4) * .001));
		faceData[i + 6] = 0x7F00;
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
		return waterFlow.getParticleTexture(null);
	}

	@Override
	public ItemOverrideList getOverrides() {
		return waterFlow.getOverrides();
	}
}