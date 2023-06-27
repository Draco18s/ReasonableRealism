package com.draco18s.harderores.client;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.block.ore.HardOreBlock;
import com.draco18s.harderores.client.gui.SifterScreen;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.internal.OreNameHelper;

import dev.lukebemish.dynamicassetgenerator.api.ResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.client.AssetResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.TextureGenerator;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.TextureReader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
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
		event.enqueueWork(() -> {
			@SuppressWarnings("deprecation")
			ItemPropertyFunction f = (stack, world, entity, seed) -> {
				CompoundTag compoundtag = stack.getTag();
				if(compoundtag == null) {
					compoundtag = stack.getOrCreateTag();
					HardOreBlock.setNbtOnStack(stack, BlockProperties.ORE_DENSITY, 16);
				}
				CompoundTag compoundtag1 = compoundtag.getCompound("BlockStateTag");
				String s1 = compoundtag1.getString(BlockProperties.ORE_DENSITY.getName());
				System.out.println("I see a nbt of " + s1);
				return Integer.parseInt(s1);
			};
			
			Block[] oreBlocks = {
				HarderOres.ModBlocks.ore_hardcopper,
				HarderOres.ModBlocks.ore_harddiamond,
				HarderOres.ModBlocks.ore_hardgold,
				HarderOres.ModBlocks.ore_hardiron,
				HarderOres.ModBlocks.ore_harddeepslate_copper,
				HarderOres.ModBlocks.ore_harddeepslate_diamond,
				HarderOres.ModBlocks.ore_harddeepslate_gold,
				HarderOres.ModBlocks.ore_harddeepslate_iron
			};
			for(Block blk : oreBlocks) {
				ItemProperties.register(blk.asItem(), new ResourceLocation(HarderOres.MODID,BlockProperties.ORE_DENSITY.getName()), f);
			}
		});
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
