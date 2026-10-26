package com.draco18s.harddatagen;

import java.util.concurrent.CompletableFuture;

import com.draco18s.farming.HarderFarming;
import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.HardLib;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;

/** Item tags (some copied from block tags, which this waits for). */
public class ModItemsTagProvider extends BlockTagCopyingItemTagProvider {

	public ModItemsTagProvider(PackOutput pack, CompletableFuture<Provider> provider, CompletableFuture<TagLookup<Block>> blockTags) {
		super(pack, provider, blockTags, HarderOres.MODID);
	}

	/** Tags hold registry keys now. */
	private static ResourceKey<Item> key(ItemLike item) {
		return item.asItem().builtInRegistryHolder().key();
	}

	@Override
	protected void addTags(Provider p_256380_) {
		copy(BlockItemTags.SMALL_FLOWERS);
		// (minecraft:tall_flowers is gone; c:flowers/tall is the tag for them.)
		copy(Tags.BlockItems.FLOWERS_TALL);
		tag(ItemTags.COPPER_ORES).add(key(HarderOres.ModItems.largedust_copper))
			.add(key(HarderOres.ModBlocks.ore_hardcopper.asItem())).add(key(HarderOres.ModBlocks.ore_harddeepslate_copper.asItem()));
		tag(ItemTags.GOLD_ORES).add(key(HarderOres.ModItems.largedust_gold))
			.add(key(HarderOres.ModBlocks.ore_hardgold.asItem())).add(key(HarderOres.ModBlocks.ore_harddeepslate_gold.asItem()));
		tag(ItemTags.IRON_ORES).add(key(HarderOres.ModItems.largedust_iron))
			.add(key(HarderOres.ModBlocks.ore_hardiron.asItem())).add(key(HarderOres.ModBlocks.ore_harddeepslate_iron.asItem()));

		TagKey<Item> copDust = ItemTags.create(HardLib.getRL("c", "dust/copper"));
		TagKey<Item> goldDust = ItemTags.create(HardLib.getRL("c", "dust/gold"));
		TagKey<Item> ironDust = ItemTags.create(HardLib.getRL("c", "dust/iron"));
		
		tag(copDust).add(key(HarderOres.ModItems.largedust_copper));
		tag(goldDust).add(key(HarderOres.ModItems.largedust_gold));
		tag(ironDust).add(key(HarderOres.ModItems.largedust_iron));
		
		tag(Tags.Items.DUSTS).addTag(copDust);
		tag(Tags.Items.DUSTS).addTag(goldDust);
		tag(Tags.Items.DUSTS).addTag(ironDust);
		tag(Tags.Items.DUSTS).add(key(HarderFarming.ModItems.largedust_flour));
		tag(Tags.Items.DUSTS).add(key(HarderFarming.ModItems.salt));
		tag(HarderFarming.Tags.Items.SALT).add(key(HarderFarming.ModItems.salt));
		
		tag(HarderOres.Tags.Items.TINY_COPPER_DUST).add(key(HarderOres.ModItems.tinydust_copper));
		tag(HarderOres.Tags.Items.TINY_GOLD_DUST).add(key(HarderOres.ModItems.tinydust_gold));
		tag(HarderOres.Tags.Items.TINY_IRON_DUST).add(key(HarderOres.ModItems.tinydust_iron));

		tag(HarderOres.Tags.Items.TINY_ORE_DUSTS).addTag(HarderOres.Tags.Items.TINY_COPPER_DUST);
		tag(HarderOres.Tags.Items.TINY_ORE_DUSTS).addTag(HarderOres.Tags.Items.TINY_GOLD_DUST);
		tag(HarderOres.Tags.Items.TINY_ORE_DUSTS).addTag(HarderOres.Tags.Items.TINY_IRON_DUST);
		
		tag(Tags.Items.SEEDS).add(key(HarderFarming.ModItems.winter_wheat_seeds));
		tag(Tags.Items.SEEDS).add(key(HarderFarming.ModItems.weed_seeds));
		TagKey<Item> copperNuggets = ItemTags.create(HardLib.getRL("c", "nuggets/copper"));
		tag(copperNuggets).add(key(HarderOres.ModItems.copper_nugget));
		tag(Tags.Items.NUGGETS).addTag(copperNuggets);//raw_materials/copper

		TagKey<Item> rawCopperNug = (ItemTags.create(HardLib.getRL("c", "raw_materials/nuggets/copper")));
		TagKey<Item> rawDiamondNug = (ItemTags.create(HardLib.getRL("c", "raw_materials/nuggets/diamond")));
		TagKey<Item> rawGoldNug = (ItemTags.create(HardLib.getRL("c", "raw_materials/nuggets/gold")));
		TagKey<Item> rawIronNug = (ItemTags.create(HardLib.getRL("c", "raw_materials/nuggets/iron")));
		
		tag(HarderOres.Tags.Items.ORE_CHUNKS).addTag(rawCopperNug);
		tag(HarderOres.Tags.Items.ORE_CHUNKS).addTag(rawDiamondNug);
		tag(HarderOres.Tags.Items.ORE_CHUNKS).addTag(rawGoldNug);
		tag(HarderOres.Tags.Items.ORE_CHUNKS).addTag(rawIronNug);
		
		tag(rawDiamondNug).add(key(HarderOres.ModItems.orechunk_diamond));

		tag(rawCopperNug).addTag(HarderOres.Tags.Items.TINY_COPPER_DUST);
		tag(rawGoldNug).addTag(HarderOres.Tags.Items.TINY_GOLD_DUST);
		tag(rawIronNug).addTag(HarderOres.Tags.Items.TINY_IRON_DUST);
		
		tag(Tags.Items.RAW_MATERIALS_COPPER).add(key(HarderOres.ModItems.largedust_copper));
		tag(Tags.Items.RAW_MATERIALS_GOLD).add(key(HarderOres.ModItems.largedust_gold));
		tag(Tags.Items.RAW_MATERIALS_IRON).add(key(HarderOres.ModItems.largedust_iron));
		
		tag(Tags.Items.RAW_MATERIALS).addTag(HarderOres.Tags.Items.ORE_CHUNKS);
		
		tag(HarderOres.Tags.Items.GRANULAR).add(key(Blocks.SAND.asItem()));
		tag(HarderOres.Tags.Items.GRANULAR).add(key(Blocks.RED_SAND.asItem()));
		tag(HarderOres.Tags.Items.GRANULAR).add(key(Blocks.GRAVEL.asItem()));
		tag(HarderOres.Tags.Items.GRANULAR).add(key(Blocks.COARSE_DIRT.asItem()));

		// What repairs diamond studded tools.
		tag(HarderOres.ModItemTiers.stud_repair_items).add(key(HarderOres.ModItems.orechunk_diamond));
	}
}
