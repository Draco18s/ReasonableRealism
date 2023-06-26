package com.draco18s.harderores.client;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.client.gui.SifterScreen;
import com.draco18s.hardlib.api.internal.OreNameHelper;

import dev.lukebemish.dynamicassetgenerator.api.ResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.client.AssetResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.TextureGenerator;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.TextureReader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = HarderOres.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
	public static final AssetResourceCache ASSET_CACHE = ResourceCache.register(
			new AssetResourceCache(new ResourceLocation(HarderOres.MODID, "assets")));
	static
	{
		initializeClient();
	}

	//@SubscribeEvent
	public static void registerModels(ModelEvent event) {
		//ObjLoader.INSTANCE.loadModel(new ModelSettings(new ResourceLocation(HarderOres.MODID, "millstone_corner.obj"), true, true, false, false, null));
	}

	@SubscribeEvent
	public static void registerClientGuiFactories(final FMLClientSetupEvent event) {
		MenuScreens.register(HarderOres.ModContainerTypes.machine_sifter, SifterScreen::new);
	}

	public static void initializeClient() {
		OreNameHelper.DoForTextureNames((vanillaStoneTexture, vanillaOriginalOreTexture, harderOreBlockName)->{
			genOverlayTextures(vanillaStoneTexture, vanillaOriginalOreTexture, harderOreBlockName);
		});
	}

	public static void genOverlayTextures(ResourceLocation base, ResourceLocation name, ResourceLocation outLoc) {
		ResourceLocation overlayTex = new ResourceLocation(HarderOres.MODID, "block/ore/overlay");
		for(int i=1;i<=16;i++) {
			String sfx = "_" + i;
			genOverlayTexture(base, overlayTex.withSuffix(sfx), outLoc.withSuffix(sfx).withPrefix("block/"));
		}
	}
	
	public static void genOverlayTexture(ResourceLocation oreTex, ResourceLocation overlayTex, ResourceLocation outputLocation) {
		System.out.println("Creating texture " + outputLocation.toString());
		// This method would be run during client initialization from the appropriate location.
		// ResourceLocation oreTex = new ResourceLocation("minecraft", "block/deepslate_gold_ore");
		ASSET_CACHE.planSource(
				new TextureGenerator(
						outputLocation,
						new MaskOverlayGenerator(
								new TextureReader(oreTex),
								new TextureReader(overlayTex)
								)
						)
				);
	}
}
