package com.draco18s.harddatagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import com.draco18s.farming.HarderFarming;
import com.draco18s.harderores.HarderOres;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemsTagProvider extends ItemTagsProvider {

	public ModItemsTagProvider(PackOutput pack, CompletableFuture<Provider> provider,
			CompletableFuture<TagLookup<Block>> lookupProvider, String modId,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(pack, provider, lookupProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(Provider p_256380_) {
		tag(ItemTags.COPPER_ORES).add(HarderOres.ModItems.largedust_copper)
			.add(HarderOres.ModBlocks.ore_hardcopper.asItem()).add(HarderOres.ModBlocks.ore_harddeepslate_copper.asItem());
		tag(ItemTags.GOLD_ORES).add(HarderOres.ModItems.largedust_gold)
			.add(HarderOres.ModBlocks.ore_hardgold.asItem()).add(HarderOres.ModBlocks.ore_harddeepslate_gold.asItem());
		tag(ItemTags.IRON_ORES).add(HarderOres.ModItems.largedust_iron)
			.add(HarderOres.ModBlocks.ore_hardiron.asItem()).add(HarderOres.ModBlocks.ore_harddeepslate_iron.asItem());

		TagKey<Item> copDust = ItemTags.create(new ResourceLocation("forge", "dust/copper"));
		TagKey<Item> goldDust = ItemTags.create(new ResourceLocation("forge", "dust/gold"));
		TagKey<Item> ironDust = ItemTags.create(new ResourceLocation("forge", "dust/iron"));
		
		tag(copDust).add(HarderOres.ModItems.largedust_copper);
		tag(goldDust).add(HarderOres.ModItems.largedust_gold);
		tag(ironDust).add(HarderOres.ModItems.largedust_iron);
		
		tag(Tags.Items.DUSTS).addTag(copDust);
		tag(Tags.Items.DUSTS).addTag(goldDust);
		tag(Tags.Items.DUSTS).addTag(ironDust);
		tag(Tags.Items.DUSTS).add(HarderFarming.ModItems.largedust_flour);
		tag(Tags.Items.DUSTS).add(HarderFarming.ModItems.salt);
		
		tag(HarderOres.Tags.Items.TINY_COPPER_DUST).add(HarderOres.ModItems.tinydust_copper);
		tag(HarderOres.Tags.Items.TINY_GOLD_DUST).add(HarderOres.ModItems.tinydust_gold);
		tag(HarderOres.Tags.Items.TINY_IRON_DUST).add(HarderOres.ModItems.tinydust_iron);

		tag(HarderOres.Tags.Items.TINY_ORE_DUSTS).addTag(HarderOres.Tags.Items.TINY_COPPER_DUST);
		tag(HarderOres.Tags.Items.TINY_ORE_DUSTS).addTag(HarderOres.Tags.Items.TINY_GOLD_DUST);
		tag(HarderOres.Tags.Items.TINY_ORE_DUSTS).addTag(HarderOres.Tags.Items.TINY_IRON_DUST);
		
		tag(Tags.Items.SEEDS).add(HarderFarming.ModItems.winter_wheat_seeds);
		TagKey<Item> copperNuggets = ItemTags.create(new ResourceLocation("forge", "nuggets/copper"));
		tag(copperNuggets).add(HarderOres.ModItems.copper_nugget);
		tag(Tags.Items.NUGGETS).addTag(copperNuggets);//raw_materials/copper

		TagKey<Item> rawCopperNug = (ItemTags.create(new ResourceLocation("forge", "raw_materials/nuggets/copper")));
		TagKey<Item> rawDiamondNug = (ItemTags.create(new ResourceLocation("forge", "raw_materials/nuggets/diamond")));
		TagKey<Item> rawGoldNug = (ItemTags.create(new ResourceLocation("forge", "raw_materials/nuggets/gold")));
		TagKey<Item> rawIronNug = (ItemTags.create(new ResourceLocation("forge", "raw_materials/nuggets/iron")));
		
		tag(HarderOres.Tags.Items.ORE_CHUNKS).addTag(rawCopperNug);
		tag(HarderOres.Tags.Items.ORE_CHUNKS).addTag(rawDiamondNug);
		tag(HarderOres.Tags.Items.ORE_CHUNKS).addTag(rawGoldNug);
		tag(HarderOres.Tags.Items.ORE_CHUNKS).addTag(rawIronNug);
		
		tag(rawCopperNug).add(HarderOres.ModItems.orechunk_copper);
		tag(rawDiamondNug).add(HarderOres.ModItems.orechunk_diamond);
		tag(rawGoldNug).add(HarderOres.ModItems.orechunk_gold);
		tag(rawIronNug).add(HarderOres.ModItems.orechunk_iron);

		tag(rawCopperNug).addTag(HarderOres.Tags.Items.TINY_COPPER_DUST);
		tag(rawGoldNug).addTag(HarderOres.Tags.Items.TINY_GOLD_DUST);
		tag(rawIronNug).addTag(HarderOres.Tags.Items.TINY_IRON_DUST);
		
		tag(Tags.Items.RAW_MATERIALS_COPPER).add(HarderOres.ModItems.largedust_copper);
		tag(Tags.Items.RAW_MATERIALS_GOLD).add(HarderOres.ModItems.largedust_gold);
		tag(Tags.Items.RAW_MATERIALS_IRON).add(HarderOres.ModItems.largedust_iron);
		
		tag(Tags.Items.RAW_MATERIALS).addTag(HarderOres.Tags.Items.ORE_CHUNKS);
		
		tag(HarderOres.Tags.Items.GRANULAR).add(Blocks.SAND.asItem());
		tag(HarderOres.Tags.Items.GRANULAR).add(Blocks.RED_SAND.asItem());
		tag(HarderOres.Tags.Items.GRANULAR).add(Blocks.GRAVEL.asItem());
		tag(HarderOres.Tags.Items.GRANULAR).add(Blocks.COARSE_DIRT.asItem());
	}
}
