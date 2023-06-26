package com.draco18s.hardlib.client;

//import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;

public enum ModelsCache implements ResourceManagerReloadListener /*ISelectiveResourceReloadListener*/ {

	INSTANCE;

	//private final Map<ResourceLocation, IModel<?>> cache = new HashMap<ResourceLocation, IModel<?>>();
	//private final Map<ResourceLocation, IForgeBakedModel> bakedCache = new HashMap<ResourceLocation, IForgeBakedModel>();
	
	/*public static final IModelState DEFAULTMODELSTATE = new IModelState() {
		@Override
		public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
			return Optional.empty();
		}
	};
	public static final VertexFormat DEFAULTVERTEXFORMAT = DefaultVertexFormat.BLOCK;
	public static final Function<ResourceLocation, TextureAtlasSprite> DEFAULTTEXTUREGETTER = new Function<ResourceLocation, TextureAtlasSprite>() {
		@Override
		public TextureAtlasSprite apply(ResourceLocation texture) {
			return Minecraft.getInstance().getTextureAtlas(texture).apply(texture); //.getAtlasSprite(texture.toString());
			//return Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(texture.toString());
		}
	};

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

	public IForgeBakedModel getModel(ResourceLocation key) {
		return bakedCache.get(key);
	}

	public IForgeBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		IForgeBakedModel model = getModel(key);
		if(model == null) {
			ISprite sprite = ModelRotation.X0_Y0;
			model = getOrLoadModel(location).bake(bakery, textureGetter, sprite, format);
			//model = getOrLoadModel(location).bake(state, format, textureGetter);
			bakedCache.put(key, model);
		}
		return model;
	}

	public IForgeBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, IModelState state, VertexFormat format) {
		return getOrLoadModel(bakery, key, location, state, format, DEFAULTTEXTUREGETTER);
	}

	public IForgeBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, IModelState state, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(bakery, key, location, state, DEFAULTVERTEXFORMAT, textureGetter);
	}

	public IForgeBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, format, textureGetter);
	}

	public IForgeBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, IModelState state) {
		return getOrLoadModel(bakery, key, location, state, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}

	public IForgeBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, VertexFormat format) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, format, DEFAULTTEXTUREGETTER);
	}

	public IForgeBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, textureGetter);
	}

	public IForgeBakedModel getOrLoadModel(ModelBakery bakery, ResourceLocation key, ResourceLocation location) {
		return getOrLoadModel(bakery, key, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}

	public IForgeBakedModel getOrLoadBakedModel(ModelBakery bakery, ResourceLocation location) {
		return getOrLoadModel(bakery, location, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}*/

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		//cache.clear();
		//bakedCache.clear();
	}

	//@Override
	public void onResourceManagerReload(ResourceManager resourceManager, Predicate<ResourceType> resourcePredicate) {
		//cache.clear();
		//bakedCache.clear();
	}

}
