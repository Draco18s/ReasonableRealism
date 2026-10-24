package com.draco18s.harddatagen;

import java.util.concurrent.CompletableFuture;

import com.draco18s.farming.HarderFarming;
import com.draco18s.flowers.OreFlowers;
import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.internal.OreNameHelper;
import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModDataGenerator {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		
		if(event.includeClient()) {
			generator.addProvider(true, new ModLootTableProvider(packOutput, lookupProvider));
			for(String modid : new String[] {ExpandedIndustry.MODID, HarderOres.MODID, HarderFarming.MODID, OreFlowers.MODID}) {
				generator.addProvider(true, new ModBlockStateProvider(packOutput, modid, fileHelper));
				generator.addProvider(true, new ModItemModelProvider(packOutput, modid, fileHelper));
			}
		}
		if(event.includeServer()) {
			BlockTagsProvider blockTagProvider = new ModBlockTagProvider(packOutput, lookupProvider, HarderOres.MODID, fileHelper);
			generator.addProvider(true, blockTagProvider);
			generator.addProvider(true, new ModItemsTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), HarderOres.MODID, fileHelper));
			generator.addProvider(true, new ModRecipeProvider(packOutput, lookupProvider));
			generator.addProvider(true, new LootModifierProvider(packOutput, lookupProvider));
		}
		addVirtualPackContents(fileHelper);
	}

	private static void addVirtualPackContents(ExistingFileHelper fileHelper) {
	    final String PATH_PREFIX = "textures/block";
	    final String PATH_SUFFIX = ".png";
	    
	    OreNameHelper.DoForTextureNames((vanillaStoneTexture, vanillaOriginalOreTexture, harderOreBlockName)->{
	    	for(int i=1; i<=16; i++)
				fileHelper.trackGenerated(harderOreBlockName.withSuffix("_"+i), PackType.CLIENT_RESOURCES, PATH_SUFFIX, PATH_PREFIX);
	    });
	}
}
