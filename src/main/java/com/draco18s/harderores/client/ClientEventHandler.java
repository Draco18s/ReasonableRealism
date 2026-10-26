package com.draco18s.harderores.client;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.block.ore.HardOreBlock;
import com.draco18s.harderores.client.gui.PackagerScreen;
import com.draco18s.harderores.client.gui.SifterScreen;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.client.AdvancementLinks;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.List;

@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = HarderOres.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
	/** The hard ore textures, made from whatever stone textures are loaded (see HardOreTexturePack). */
	@SubscribeEvent
	public static void addPacks(AddPackFindersEvent event) {
		if(event.getPackType() == PackType.CLIENT_RESOURCES) {
			event.addRepositorySource(packs -> packs.accept(HardOreTexturePack.create()));
		}
	}

	/**
	 * Each machine's recipes get their own recipe book category, so the client book files (and tracks the unlocking
	 * of) sifting, grinding and packing recipes instead of warning that it doesn't know them.
	 */
	@SubscribeEvent
	public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
		RecipeBookCategories sifting = RecipeBookEnums.SIFTING.getValue();
		RecipeBookCategories grinding = RecipeBookEnums.GRINDING.getValue();
		RecipeBookCategories packing = RecipeBookEnums.PACKING.getValue();
		event.registerRecipeCategoryFinder(HarderOres.RecipeTypes.SIFTING, recipe -> sifting);
		event.registerRecipeCategoryFinder(HarderOres.RecipeTypes.GRINDING, recipe -> grinding);
		event.registerRecipeCategoryFinder(HarderOres.RecipeTypes.PACKING, recipe -> packing);
		// The sifter and packager screens' books (the millstone has no screen).
		event.registerBookCategories(HarderOres.RecipeBookTypes.SIFTER, List.of(sifting));
		event.registerBookCategories(HarderOres.RecipeBookTypes.PACKAGER, List.of(packing));
	}

	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		// Prospector: the dust shows at the broken stone (one per ore type found), the radar marks each ore block.
		event.registerSpriteSet(HarderOres.ModParticleTypes.prospector_dust, ProspectorDustProvider::new);
		event.registerSpriteSet(HarderOres.ModParticleTypes.prospector_radar, ProspectorRadarProvider::new);
	}

	record ProspectorDustProvider(SpriteSet sprites) implements ParticleProvider<BlockParticleOption> {
		@Override
		public Particle createParticle(BlockParticleOption opt, ClientLevel world, double x, double y, double z, double dx, double dy, double dz) {
			return new ProspectorParticleDust(opt, world, x, y, z, dx, dy, dz, sprites);
		}
	}

	record ProspectorRadarProvider(SpriteSet sprites) implements ParticleProvider<BlockParticleOption> {
		@Override
		public Particle createParticle(BlockParticleOption opt, ClientLevel world, double x, double y, double z, double dx, double dy, double dz) {
			return new ProspectorParticleRadar(opt, world, x, y, z, dx, dy, dz, sprites);
		}
	}

	//@SubscribeEvent
	public static void registerModels(ModelEvent event) {
		//ObjLoader.INSTANCE.loadModel(new ModelSettings(new ResourceLocation(HarderOres.MODID, "millstone_corner.obj"), true, true, false, false, null));
	}

	@SubscribeEvent
	public static void registerScreens(final RegisterMenuScreensEvent event) {
		event.register(HarderOres.ModContainerTypes.machine_sifter, SifterScreen::new);
		event.register(HarderOres.ModContainerTypes.packager, PackagerScreen::new);
	}

	@SubscribeEvent
	public static void registerClientGuiFactories(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			// Acquire Hardware also needs Just a Nugget (see data/minecraft/advancement/story/smelt_iron.json).
			AdvancementLinks.add(HarderOres.getRL("iron_nuggets"), HardLib.parseRL("minecraft:story/smelt_iron"));
			ItemPropertyFunction f = (stack, world, entity, seed) -> HardOreBlock.getDensity(stack);
			
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
				ItemProperties.register(blk.asItem(), HarderOres.getRL(BlockProperties.ORE_DENSITY.getName()), f);
			}
		});
	}
}
