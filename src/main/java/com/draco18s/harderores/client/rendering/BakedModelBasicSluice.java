package com.draco18s.harderores.client.rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.draco18s.harderores.block.SluiceBlock;
import com.draco18s.harderores.entity.SluiceTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Direction.Axis;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

public class BakedModelBasicSluice implements IDynamicBakedModel {
	IBakedModel waterFlow;
	IBakedModel basePlate;
	private final TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];
	private final TextureAtlasSprite atlasSpritePlanks_1;
	private final TextureAtlasSprite atlasSpritePlanks_2;

	public BakedModelBasicSluice(IBakedModel water, IBakedModel base) {
		waterFlow = water;
		basePlate = base;
		AtlasTexture atlastexture = Minecraft.getInstance().getTextureMap();
		this.atlasSpritesWater[0] = atlastexture.getSprite(ModelBakery.LOCATION_WATER_FLOW);
		this.atlasSpritesWater[1] = atlastexture.getSprite(ModelBakery.LOCATION_WATER_OVERLAY);
		atlasSpritePlanks_1 = atlastexture.getSprite(new ResourceLocation("harderores:block/oak_planks"));
		atlasSpritePlanks_2 = atlastexture.getSprite(new ResourceLocation("harderores:block/oak_planks_rotated"));
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
		BlockFaceUV uvs;
		boolean diffuseLight = true;
		BakedQuad q0;
		int[] data;
		Vector3f[] verts;
		if(extraData == null || SluiceTileEntity.LEVEL_CORNERS_0 == null || !extraData.hasProperty(SluiceTileEntity.LEVEL_CORNERS_0)) return list;
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
			Direction facingDir = state.get(BlockStateProperties.HORIZONTAL_FACING);
			color = (0xC0000000) | ((blue) << 16) | ((green) << 8) | (red);
			//FaceBakery faceBakery = new FaceBakery();
			data = new int[28];
			if(side == null || side == Direction.UP) {
				verts = getTopVerts(corners, facingDir);
				switch(facingDir) {
					case NORTH:
					case EAST:
						uvs = new BlockFaceUV(new float[] {8, 8, 0, 0}, 0);
						break;
					default:
						uvs = new BlockFaceUV(new float[] {0, 0, 8, 8}, 0);
						break;
				}
				bakeQuad(data, verts, atlasSpritesWater[0], uvs, color);
				q0 = new BakedQuad(data, 0, Direction.UP, atlasSpritesWater[0], diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
				data = new int[28];
				verts = new Vector3f[] {verts[3],verts[2],verts[1],verts[0]};
				bakeQuad(data, verts, atlasSpritesWater[0], uvs, color);
				q0 = new BakedQuad(data, 0, Direction.UP, atlasSpritesWater[0], diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
				
				data = new int[28];
				verts = getTopVerts(corners, facingDir);
				verts[0].add(0, -2f/16, 0);
				verts[1].add(0, -2f/16, 0);
				verts[2].add(0, -2f/16, 0);
				verts[3].add(0, -2f/16, 0);
				uvs = new BlockFaceUV(new float[] {0, 0, 16, 16}, 0);
				bakeQuad(data, verts, atlasSpritePlanks_1, uvs, 0xFFFFFFFF);
				q0 = new BakedQuad(data, 0, Direction.UP, atlasSpritePlanks_1, diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
			}
			Direction sideFace = facingDir.rotateAround(Axis.Y);
			data = new int[28];
			if(side == null || side == sideFace) {
				verts = getSideVerts(corners, sideFace);
				verts[0].add(0, verts[1].getY()-2f/16, 0);
				verts[3].add(0, verts[2].getY()-2f/16, 0);
				uvs = new BlockFaceUV(new float[] {0, 0, 8, 8}, 0);
				bakeQuad(data, verts, atlasSpritesWater[1], uvs, color);
				q0 = new BakedQuad(data, 0, sideFace, atlasSpritesWater[1], diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
				
				data = new int[28];
				verts = getSideVerts(corners, sideFace);
				verts[1].add(0, -2f/16, 0);
				verts[2].add(0, -2f/16, 0);
				
				float min = Math.min(verts[1].getY(), verts[2].getY());
				float max = Math.max(verts[1].getY(), verts[2].getY());

				Vector3f[] verts2 = new Vector3f[4];
				verts2[1] = new Vector3f(verts[0].getX(),min,			 verts[0].getZ());
				verts2[2] = new Vector3f(verts[1].getX(),verts[1].getY(),verts[1].getZ());
				verts2[3] = new Vector3f(verts[2].getX(),verts[2].getY(),verts[2].getZ());
				verts2[0] = new Vector3f(verts[3].getX(),min,			 verts[3].getZ());
				
				uvs = new BlockFaceUV(new float[] {min*16, 0, min*16+(max-min)*16, 16}, 0);
				bakeQuad(data, verts2, atlasSpritePlanks_1, uvs, 0xFFFFFFFF);
				q0 = new BakedQuad(data, 0, sideFace, atlasSpritePlanks_1, diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
				
				data = new int[28];
				verts[1] = new Vector3f(verts[1].getX(),min,verts[1].getZ());
				verts[2] = new Vector3f(verts[2].getX(),min,verts[2].getZ());
				verts2[0] = verts[3];
				verts2[1] = verts[0];
				verts2[2] = verts[1];
				verts2[3] = verts[2];
				
				uvs = new BlockFaceUV(new float[] {0, 0, min*16, 16}, 0);
				bakeQuad(data, verts2, atlasSpritePlanks_1, uvs, 0xFFFFFFFF);
				q0 = new BakedQuad(data, 0, sideFace, atlasSpritePlanks_1, diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
			}
			sideFace = sideFace.getOpposite();
			data = new int[28];
			if(side == null || side == sideFace) {
				verts = getSideVerts(corners, sideFace);
				verts[0].add(0, verts[1].getY()-2f/16, 0);
				verts[3].add(0, verts[2].getY()-2f/16, 0);
				uvs = new BlockFaceUV(new float[] {0, 0, 8, 8}, 0);
				bakeQuad(data, verts, atlasSpritesWater[1], uvs, color);
				q0 = new BakedQuad(data, 0, sideFace, atlasSpritesWater[1], diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
				
				data = new int[28];
				verts = getSideVerts(corners, sideFace);
				verts[1].add(0, -2f/16, 0);
				verts[2].add(0, -2f/16, 0);
				float min = Math.min(verts[1].getY(), verts[2].getY());
				float max = Math.max(verts[1].getY(), verts[2].getY());

				Vector3f[] verts2 = new Vector3f[4];
				verts2[0] = new Vector3f(verts[0].getX(),min,			 verts[0].getZ());
				verts2[1] = new Vector3f(verts[1].getX(),verts[1].getY(),verts[1].getZ());
				verts2[2] = new Vector3f(verts[2].getX(),verts[2].getY(),verts[2].getZ());
				verts2[3] = new Vector3f(verts[3].getX(),min,			 verts[3].getZ());
				
				uvs = new BlockFaceUV(new float[] {0, min*16, 16, min*16+(max-min)*16}, 0);
				bakeQuad(data, verts2, atlasSpritePlanks_2, uvs, 0xFFFFFFFF);
				q0 = new BakedQuad(data, 0, sideFace, atlasSpritePlanks_2, diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
				
				data = new int[28];
				verts[1] = new Vector3f(verts[1].getX(),min,verts[1].getZ());
				verts[2] = new Vector3f(verts[2].getX(),min,verts[2].getZ());
				verts2[0] = verts[3];
				verts2[1] = verts[0];
				verts2[2] = verts[1];
				verts2[3] = verts[2];
				
				uvs = new BlockFaceUV(new float[] {0, 0, min*16, 16}, 0);
				bakeQuad(data, verts2, atlasSpritePlanks_1, uvs, 0xFFFFFFFF);
				q0 = new BakedQuad(data, 0, sideFace, atlasSpritePlanks_1, diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
			}
			if(side == null || side == Direction.DOWN) {
				data = new int[28];
				verts = new Vector3f[] {
					new Vector3f(1,0,0),
					new Vector3f(1,0,1),
					new Vector3f(0,0,1),
					new Vector3f(0,0,0)
				};
				uvs = new BlockFaceUV(new float[] {0, 0, 16, 16}, 0);
				bakeQuad(data, verts, atlasSpritePlanks_1, uvs, 0xFFFFFFFF);
				q0 = new BakedQuad(data, 0, Direction.DOWN, atlasSpritePlanks_1, diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
			}
			if(side == null || side == facingDir.getOpposite()) {
				verts = getSideVerts(corners, facingDir.getOpposite());	
				float min = Math.min(verts[1].getY(), verts[2].getY());
				if(min <= 2f/16) {
					data = new int[28];			
					//float max = Math.max(verts[1].getY(), verts[2].getY());
					verts = new Vector3f[] {
						new Vector3f(verts[0].getX(),0,verts[0].getZ()),
						new Vector3f(verts[1].getX(),min,verts[1].getZ()),
						new Vector3f(verts[2].getX(),min,verts[2].getZ()),
						new Vector3f(verts[3].getX(),0,verts[3].getZ())
					};
					uvs = new BlockFaceUV(new float[] {0, 0, 16, min*16}, 0);
					bakeQuad(data, verts, atlasSpritesWater[1], uvs, color);
					q0 = new BakedQuad(data, 0, Direction.DOWN, atlasSpritesWater[1], diffuseLight, DefaultVertexFormats.BLOCK);
					list.add(q0);
					
					data = new int[28];
					verts = new Vector3f[] {
						new Vector3f(verts[3].getX(),0,verts[3].getZ()),
						new Vector3f(verts[2].getX(),min,verts[2].getZ()),
						new Vector3f(verts[1].getX(),min,verts[1].getZ()),
						new Vector3f(verts[0].getX(),0,verts[0].getZ())
					};
					uvs = new BlockFaceUV(new float[] {0, 0, 16, min*16}, 0);
					bakeQuad(data, verts, atlasSpritesWater[1], uvs, color);
					q0 = new BakedQuad(data, 0, Direction.DOWN, atlasSpritesWater[1], diffuseLight, DefaultVertexFormats.BLOCK);
					list.add(q0);
				}
			}
			if(side == null || side == facingDir) {
				data = new int[28];
				verts = getSideVerts(corners, facingDir);				
				//float min = Math.min(verts[1].getY(), verts[2].getY());
				float max = Math.max(verts[1].getY(), verts[2].getY());
				verts = new Vector3f[] {
					new Vector3f(verts[0].getX(),0,verts[0].getZ()),
					new Vector3f(verts[1].getX(),max-2f/16,verts[1].getZ()),
					new Vector3f(verts[2].getX(),max-2f/16,verts[2].getZ()),
					new Vector3f(verts[3].getX(),0,verts[3].getZ())
				};
				uvs = new BlockFaceUV(new float[] {0, 0, 16, max*16}, 0);
				bakeQuad(data, verts, atlasSpritePlanks_1, uvs, 0xFFFFFFFF);
				q0 = new BakedQuad(data, 0, Direction.DOWN, atlasSpritePlanks_1, diffuseLight, DefaultVertexFormats.BLOCK);
				list.add(q0);
			}
		}
		else {
			list.addAll(basePlate.getQuads(state, side, rand, null));
		}
		return list;
	}
	
	private Vector3f[] getSideVerts(float[] corners, Direction dir) {
		Vector3f[] verts = new Vector3f[] {
				new Vector3f(0,0,0),
				new Vector3f(0,0,0),
				new Vector3f(0,0,0),
				new Vector3f(0,0,0)};
		switch(dir) {
			case NORTH:
				verts = new Vector3f[] { //[0],[2]
						new Vector3f(1,0,     1),//bottom
						new Vector3f(1,corners[2],1),//NW
						new Vector3f(0,corners[0],1),//NE
						new Vector3f(0,0,     1)};//bottom
				break;
			case SOUTH:
				verts = new Vector3f[] { //[0],[2]
						new Vector3f(0,0,     0),//bottom
						new Vector3f(0,corners[1],0),//NE
						new Vector3f(1,corners[3],0),//NW
						new Vector3f(1,0,     0)};//bottom
				break;
			case EAST:
				verts = new Vector3f[] { //[0],[2]
						new Vector3f(0,0,     1),//bottom
						new Vector3f(0,corners[2],1),//NW
						new Vector3f(0,corners[3],0),//NE
						new Vector3f(0,0,     0)};//bottom
				break;
			case WEST:
				verts = new Vector3f[] { //[0],[2]
						new Vector3f(1,0,     0),//bottom
						new Vector3f(1,corners[3],0),//NE
						new Vector3f(1,corners[2],1),//NW
						new Vector3f(1,0,     1)};//bottom
				break;
			default:
				verts = new Vector3f[] {
						new Vector3f(0,0,0),
						new Vector3f(0,0,0),
						new Vector3f(0,0,0),
						new Vector3f(0,0,0)};
				break;
		}
		return verts;
	}
	
	private Vector3f[] getTopVerts(float[] corners, Direction dir) {
		Vector3f[] verts = new Vector3f[] {
						new Vector3f(0,0,0),
						new Vector3f(0,0,0),
						new Vector3f(0,0,0),
						new Vector3f(0,0,0)};
		switch(dir) {
			case NORTH:verts = new Vector3f[] {
					new Vector3f(0,corners[0],0),
					new Vector3f(0,corners[1],1),
					new Vector3f(1,corners[2],1),
					new Vector3f(1,corners[3],0)};
				break;
			case SOUTH:verts = new Vector3f[] {
					new Vector3f(0,corners[0],0),
					new Vector3f(0,corners[1],1),
					new Vector3f(1,corners[2],1),
					new Vector3f(1,corners[3],0)};
				break;
			case EAST:
				verts = new Vector3f[] {
						new Vector3f(1,corners[3],0),
						new Vector3f(0,corners[0],0),
						new Vector3f(0,corners[1],1),
						new Vector3f(1,corners[2],1)};
				break;
			case WEST:
				verts = new Vector3f[] {
						new Vector3f(1,corners[3],0),
						new Vector3f(0,corners[0],0),
						new Vector3f(0,corners[1],1),
						new Vector3f(1,corners[2],1)};
				break;
			default:
				break;
		}
		return verts;
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
		faceData[i + 6] = 0xFF00;
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