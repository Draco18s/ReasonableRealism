package com.draco18s.hardlib;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.advancement.AdvancementEarnedTrigger;
import com.draco18s.hardlib.api.advancement.BreakBlockTrigger;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger;
import com.draco18s.hardlib.api.advancement.FoundOreTrigger;
import com.draco18s.hardlib.api.advancement.MillstoneTrigger;
import com.draco18s.hardlib.api.advancement.WorldTimeTrigger;
import com.draco18s.hardlib.api.recipe.RecipeTagOutput;
import com.draco18s.hardlib.proxy.ClientProxy;
import com.draco18s.hardlib.proxy.IProxy;
import com.draco18s.hardlib.proxy.ServerProxy;
import com.draco18s.hardlib.util.Tuple;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@Mod(HardLib.MODID)
public class HardLib {
	public static final String MODID = "hardlib";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final IProxy PROXY = FMLEnvironment.getDist().isClient() ? new ClientProxy() : new ServerProxy();


	public HardLib(IEventBus modEventBus) {
		EasyRegistry.registerEventBus(modEventBus);
		HardLibAPI.Advancements.MILL_BUILT = EasyRegistry.registerAdvancementTrigger(MillstoneTrigger.ID, new MillstoneTrigger());
		HardLibAPI.Advancements.FOUND_ORE = EasyRegistry.registerAdvancementTrigger(FoundOreTrigger.ID, new FoundOreTrigger());
		HardLibAPI.Advancements.BLOCK_BREAK = EasyRegistry.registerAdvancementTrigger(BreakBlockTrigger.ID, new BreakBlockTrigger());
		HardLibAPI.Advancements.WORLD_TIME = EasyRegistry.registerAdvancementTrigger(WorldTimeTrigger.ID, new WorldTimeTrigger());
		HardLibAPI.Advancements.DISTANCE_TRAVELED = EasyRegistry.registerAdvancementTrigger(DistanceTraveledTrigger.ID, new DistanceTraveledTrigger());
		HardLibAPI.Advancements.ADVANCEMENT_EARNED = EasyRegistry.registerAdvancementTrigger(AdvancementEarnedTrigger.ID, new AdvancementEarnedTrigger());
		modEventBus.addListener((FMLCommonSetupEvent event) -> CogHelper.unpackConfigs());
		EasyRegistry.registerOther(Registries.RECIPE_SERIALIZER, new Tuple<Identifier, Supplier<RecipeSerializer<?>>>(getRL("tag_output"),() -> RecipeTagOutput.SERIALIZER));

		IEventBus forgeBus = NeoForge.EVENT_BUS;
		forgeBus.addListener(HardLib::onServerDataLoaded);
	}

	/** The machines' recipe tables are rebuilt once a data (re)load is done (the recipe manager is complete then). */
	private static void onServerDataLoaded(final TagsUpdatedEvent.ServerDataLoad event)
	{
		if(HardLibAPI.oreMachines != null) {
			HardLibAPI.oreMachines.update(event.getServerResources().getRecipeManager());
		}
	}


	/** hardlib:name. Each mod has the same helper for its own namespace. */
	/**
	 * The enchantment's level on the stack (enchantments are data since 1.21, so they are looked up by key), or 0 if a
	 * data pack has removed it.
	 */
	public static int getEnchantmentLevel(HolderLookup.Provider registries, ResourceKey<Enchantment> enchantment, ItemStack stack) {
		return registries.lookupOrThrow(Registries.ENCHANTMENT).get(enchantment)
				.map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, stack)).orElse(0);
	}

	public static Identifier getRL(String name) {
		return getRL(MODID, name);
	}

	/**
	 * Every Identifier in these mods is made here or in {@link #parseRL(String)}: 1.21 replaces the
	 * constructors with factory methods, so only these two need to change.
	 */
	public static Identifier getRL(String namespace, String path) {
		return Identifier.fromNamespaceAndPath(namespace, path);
	}

	/** Parses "namespace:path"; a bare path is in the minecraft namespace. */
	public static Identifier parseRL(String id) {
		return Identifier.parse(id);
	}
}
