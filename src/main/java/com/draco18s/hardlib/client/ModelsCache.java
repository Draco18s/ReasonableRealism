package com.draco18s.hardlib.client;

import java.util.HashMap;
import java.util.Map;
//import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public enum ModelsCache implements ResourceManagerReloadListener /*ISelectiveResourceReloadListener*/ {

	INSTANCE;

	private final Map<Identifier, UnbakedModel> cache = new HashMap<Identifier, UnbakedModel>();
	private final Map<Identifier, BlockStateModel> bakedCache = new HashMap<Identifier, BlockStateModel>();
	//private ModelState l;
	
	public static final ModelState DEFAULTMODELSTATE = new ModelState() {
		
	};
	public static final VertexFormat DEFAULTVERTEXFORMAT = DefaultVertexFormat.BLOCK;
	public static final Function<Identifier, TextureAtlasSprite> DEFAULTTEXTUREGETTER = new Function<Identifier, TextureAtlasSprite>() {
		@Override
		public TextureAtlasSprite apply(Identifier texture) {
			// (Block textures; atlases are looked up through the atlas manager now.)
			return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(TextureAtlas.LOCATION_BLOCKS).getSprite(texture);
		}
	};

	public UnbakedModel getOrLoadModel(Identifier location)
	{
		UnbakedModel model = cache.get(location);
		if(model == null)
		{
			try
			{
				//model = ModelLoaderRegistry.getModel(location);
			}
			catch(Exception e) {
				// TODO 1.10.2-R - log this in pretty way
				//e.printStackTrace();
				//model = ModelLoaderRegistry.getMissingModel();
			}
			cache.put(location, model);
		}
		return model;
	}

	public BlockStateModel getModel(Identifier key) {
		return bakedCache.get(key);
	}

	public BlockStateModel getOrLoadModel(ModelBakery bakery, Identifier key, Identifier location, ModelState state, VertexFormat format, Function<Identifier, TextureAtlasSprite> textureGetter) {
		BlockStateModel model = getModel(key);
		if(model == null) {
			//TextureAtlasSprite sprite = null;//ModelRotation.X0_Y0;
			//model = getOrLoadModel(location).bake(null, null, state, location); //.bake(bakery, textureGetter, sprite, format);
			//model = getOrLoadModel(location).bake(state, format, textureGetter);
			bakedCache.put(key, model);
		}
		return model;
	}

	/*public BlockStateModel getOrLoadModel(ModelBakery bakery, Identifier key, Identifier location, IModelState state, VertexFormat format) {
		return getOrLoadModel(bakery, key, location, state, format, DEFAULTTEXTUREGETTER);
	}

	public BlockStateModel getOrLoadModel(ModelBakery bakery, Identifier key, Identifier location, IModelState state, Function<Identifier, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(bakery, key, location, state, DEFAULTVERTEXFORMAT, textureGetter);
	}

	public BlockStateModel getOrLoadModel(ModelBakery bakery, Identifier key, Identifier location, VertexFormat format, Function<Identifier, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, format, textureGetter);
	}

	public BlockStateModel getOrLoadModel(ModelBakery bakery, Identifier key, Identifier location, IModelState state) {
		return getOrLoadModel(bakery, key, location, state, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}

	public BlockStateModel getOrLoadModel(ModelBakery bakery, Identifier key, Identifier location, VertexFormat format) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, format, DEFAULTTEXTUREGETTER);
	}

	public BlockStateModel getOrLoadModel(ModelBakery bakery, Identifier key, Identifier location, Function<Identifier, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, textureGetter);
	}

	public BlockStateModel getOrLoadModel(ModelBakery bakery, Identifier key, Identifier location) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}

	public BlockStateModel getOrLoadBakedModel(ModelBakery bakery, Identifier location) {
		return getOrLoadModel(bakery, location, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}*/

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		cache.clear();
		bakedCache.clear();
	}

	//@Override
	public void onResourceManagerReload(ResourceManager resourceManager, Predicate<PackType> resourcePredicate) {
		cache.clear();
		bakedCache.clear();
	}

}
