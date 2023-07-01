package com.draco18s.hardlib;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.advancement.BreakBlockTrigger;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger;
import com.draco18s.hardlib.api.advancement.FoundOreTrigger;
import com.draco18s.hardlib.api.advancement.MillstoneTrigger;
import com.draco18s.hardlib.api.advancement.WorldTimeTrigger;
import com.draco18s.hardlib.api.recipe.RecipeTagOutput;
import com.draco18s.hardlib.proxy.ClientProxy;
import com.draco18s.hardlib.proxy.IProxy;
import com.draco18s.hardlib.proxy.ServerProxy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(HardLib.MODID)
public class HardLib {
	public static final String MODID = "hardlib";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final IProxy PROXY = DistExecutor.safeRunForDist(()->ClientProxy::new, ()->ServerProxy::new);


	public HardLib() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		EasyRegistry.registerEventBus(modEventBus);
		modEventBus.addListener((FMLCommonSetupEvent event) -> {
			HardLibAPI.Advancements.MILL_BUILT = (MillstoneTrigger) EasyRegistry.registerAdvancementTrigger(new MillstoneTrigger());
			HardLibAPI.Advancements.FOUND_ORE = (FoundOreTrigger) EasyRegistry.registerAdvancementTrigger(new FoundOreTrigger());
			HardLibAPI.Advancements.BLOCK_BREAK = (BreakBlockTrigger) EasyRegistry.registerAdvancementTrigger(new BreakBlockTrigger());
			HardLibAPI.Advancements.WORLD_TIME = (WorldTimeTrigger) EasyRegistry.registerAdvancementTrigger(new WorldTimeTrigger());
			HardLibAPI.Advancements.DISTANCE_TRAVELED = (DistanceTraveledTrigger) EasyRegistry.registerAdvancementTrigger(new DistanceTraveledTrigger());
		});
		EasyRegistry.registerOther(ForgeRegistries.Keys.RECIPE_SERIALIZERS, new Tuple<ResourceLocation, Supplier<RecipeSerializer<?>>>(getRL("tag_output"),() -> new RecipeTagOutput.Serializer()));

		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.addListener(HardLib::onAddDebugReloadListener);
	}

	private static void onAddDebugReloadListener(final AddReloadListenerEvent event)
	{
		event.addListener(new ResourceReloader(event.getServerResources()));
	}

	private record ResourceReloader(ReloadableServerResources serverResources) implements ResourceManagerReloadListener {
		@Override
		public void onResourceManagerReload(ResourceManager man) {
			if(HardLibAPI.oreMachines != null) {
				HardLibAPI.oreMachines.update(serverResources.getRecipeManager());
			}
		}
	}


	private ResourceLocation getRL(String string) {
		return new ResourceLocation(MODID, string);
	}
}
