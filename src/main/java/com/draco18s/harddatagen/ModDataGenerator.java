package com.draco18s.harddatagen;

import com.draco18s.farming.HarderFarming;
import com.draco18s.flowers.OreFlowers;
import com.draco18s.harderores.HarderOres;
import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Data for all of the mods. The client data run makes everything (models need the client); the server run makes
 * the rest. Loot tables and recipes (with their advancements) are registry entries made here, written out by a
 * datapack entries provider.
 */
@EventBusSubscriber(modid = HarderOres.MODID)
public class ModDataGenerator {
	@SubscribeEvent
	public static void gatherClientData(GatherDataEvent.Client event) {
		for(String modid : new String[] {ExpandedIndustry.MODID, HarderOres.MODID, HarderFarming.MODID, OreFlowers.MODID}) {
			event.createProvider(output -> new ModModelProvider(output, modid));
		}
		gatherServer(event);
	}

	@SubscribeEvent
	public static void gatherServerData(GatherDataEvent.Server event) {
		gatherServer(event);
	}

	private static void gatherServer(GatherDataEvent event) {
		RegistrySetBuilder reloadable = new RegistrySetBuilder()
				.add(Registries.LOOT_TABLE, ModLootTableProvider.create())
				.add(RecipeProvider.asBootstrap(ModRecipeProvider::new));
		event.createProvider(output -> DatapackBuiltinEntriesProvider.forReloadableLayer(output, "Loot tables and recipes",
				event.getWorldLookupProvider(), event.getReloadableLookupProvider(), reloadable, null));
		event.createBlockAndItemTags(ModBlockTagProvider::new, ModItemsTagProvider::new);
		event.createProvider(LootModifierProvider::new);
	}
}
