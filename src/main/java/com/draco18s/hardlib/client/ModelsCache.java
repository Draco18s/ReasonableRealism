package com.draco18s.hardlib.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import com.google.common.base.Function;
//import com.google.common.base.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

public enum ModelsCache implements ISelectiveResourceReloadListener {

	INSTANCE;

	public static final IModelState DEFAULTMODELSTATE = new IModelState() {
		@Override
		public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
			return Optional.empty();
		}
	};
	public static final VertexFormat DEFAULTVERTEXFORMAT = DefaultVertexFormats.BLOCK;
	public static final Function<ResourceLocation, TextureAtlasSprite> DEFAULTTEXTUREGETTER = new Function<ResourceLocation, TextureAtlasSprite>() {
		@Override
		public TextureAtlasSprite apply(ResourceLocation texture) {
			return Minecraft.getInstance().getTextureMap().getAtlasSprite(texture.toString());
			//return Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(texture.toString());
		}
	};

	private final Map<ResourceLocation, IModel<?>> cache = new HashMap<ResourceLocation, IModel<?>>();
	private final Map<ResourceLocation, IBakedModel> bakedCache = new HashMap<ResourceLocation, IBakedModel>();

	public IModel<?> getOrLoadModel(ResourceLocation location)
	{
		IModel<?> model = cache.get(location);
		if(model == null)
		{
			try
			{
				model = ModelLoaderRegistry.getModel(location);
			}
			catch(Exception e) {
				// TODO 1.10.2-R - log this in pretty way
				e.printStackTrace();
				model = ModelLoaderRegistry.getMissingModel();
			}
			cache.put(location, model);
		}
		return model;
	}

	public IBakedModel getModel(ResourceLocation key) {
		return bakedCache.get(key);
	}

	public IBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		IBakedModel model = getModel(key);
		if(model == null) {
			ISprite sprite = ModelRotation.X0_Y0;
			model = getOrLoadModel(location).bake(bakery, textureGetter, sprite, format);
			//model = getOrLoadModel(location).bake(state, format, textureGetter);
			bakedCache.put(key, model);
		}
		return model;
	}

	public IBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, IModelState state, VertexFormat format) {
		return getOrLoadModel(bakery, key, location, state, format, DEFAULTTEXTUREGETTER);
	}

	public IBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, IModelState state, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(bakery, key, location, state, DEFAULTVERTEXFORMAT, textureGetter);
	}

	public IBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, format, textureGetter);
	}

	public IBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, IModelState state) {
		return getOrLoadModel(bakery, key, location, state, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}

	public IBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, VertexFormat format) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, format, DEFAULTTEXTUREGETTER);
	}

	public IBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, textureGetter);
	}

	public IBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}

	public IBakedModel getOrLoadBakedModel(ModelBakery bakery, ResourceLocation location) {
		return getOrLoadModel(bakery, location, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		cache.clear();
		bakedCache.clear();
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		cache.clear();
		bakedCache.clear();
	}

}
