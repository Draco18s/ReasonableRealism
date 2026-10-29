package com.draco18s.harderores.client;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.client.gui.OreCartScreen;
import com.draco18s.harderores.client.gui.PackagerScreen;
import com.draco18s.harderores.client.gui.SifterScreen;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.client.AdvancementLinks;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = HarderOres.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
	/** The hard ore textures, made from whatever stone textures are loaded (see HardOreTexturePack). */
	@SubscribeEvent
	public static void addPacks(AddPackFindersEvent event) {
		if(event.getPackType() == PackType.CLIENT_RESOURCES) {
			event.addRepositorySource(packs -> packs.accept(HardOreTexturePack.create()));
		}
	}

	/** The hard ore item models show the item's density (see their item models). */
	@SubscribeEvent
	public static void registerItemProperties(RegisterRangeSelectItemModelPropertyEvent event) {
		event.register(HarderOres.getRL(BlockProperties.ORE_DENSITY.getName()), OreDensityProperty.MAP_CODEC);
	}

	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		// Prospector: the dust shows at the broken stone (one per ore type found), the radar marks each ore block.
		event.registerSpriteSet(HarderOres.ModParticleTypes.prospector_dust, ProspectorDustProvider::new);
		event.registerSpriteSet(HarderOres.ModParticleTypes.prospector_radar, ProspectorRadarProvider::new);
	}

	@SubscribeEvent
	public static void registerPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(ProspectorParticleRadar.ALWAYS_ON_TOP_PIPELINE);
	}

	record ProspectorDustProvider(SpriteSet sprites) implements ParticleProvider<BlockParticleOption> {
		@Override
		public Particle createParticle(BlockParticleOption opt, ClientLevel world, double x, double y, double z, double dx, double dy, double dz, RandomSource random) {
			return new ProspectorParticleDust(opt, world, x, y, z, dx, dy, dz, sprites);
		}
	}

	record ProspectorRadarProvider(SpriteSet sprites) implements ParticleProvider<BlockParticleOption> {
		@Override
		public Particle createParticle(BlockParticleOption opt, ClientLevel world, double x, double y, double z, double dx, double dy, double dz, RandomSource random) {
			return new ProspectorParticleRadar(opt, world, x, y, z, dx, dy, dz, sprites);
		}
	}

	//@SubscribeEvent
	public static void registerModels(ModelEvent event) {
		//ObjLoader.INSTANCE.loadModel(new ModelSettings(new Identifier(HarderOres.MODID, "millstone_corner.obj"), true, true, false, false, null));
	}

	@SubscribeEvent
	public static void registerScreens(final RegisterMenuScreensEvent event) {
		event.register(HarderOres.ModContainerTypes.machine_sifter, SifterScreen::new);
		event.register(HarderOres.ModContainerTypes.packager, PackagerScreen::new);
		event.register(HarderOres.ModContainerTypes.ore_cart, OreCartScreen::new);
	}

	@SubscribeEvent
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(OreCartModel.LAYER, OreCartModel::createBodyLayer);
		event.registerLayerDefinition(OreCartModel.CHUTE_LAYER, OreCartModel::createChuteLayer);
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(HarderOres.ModEntities.ore_cart, OreCartRenderer::new);
	}

	@SubscribeEvent
	public static void registerClientGuiFactories(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			// Acquire Hardware also needs Just a Nugget (see data/minecraft/advancement/story/smelt_iron.json).
			AdvancementLinks.add(HarderOres.getRL("iron_nuggets"), HardLib.parseRL("minecraft:story/smelt_iron"));
		});
	}
}
