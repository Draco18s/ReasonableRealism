package com.draco18s.hardlib.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import com.draco18s.harderores.HarderOres;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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

		tag(ItemTags.create(new ResourceLocation("forge", "dust/copper"))).add(HarderOres.ModItems.largedust_copper);
		tag(ItemTags.create(new ResourceLocation("forge", "dust/gold"))).add(HarderOres.ModItems.largedust_gold);
		tag(ItemTags.create(new ResourceLocation("forge", "dust/iron"))).add(HarderOres.ModItems.largedust_iron);
		tag(HarderOres.ModItemTags.TINY_COPPER_DUST).add(HarderOres.ModItems.tinydust_copper);
		tag(HarderOres.ModItemTags.TINY_GOLD_DUST).add(HarderOres.ModItems.tinydust_gold);
		tag(HarderOres.ModItemTags.TINY_IRON_DUST).add(HarderOres.ModItems.tinydust_iron);

		TagKey<Item> copperNuggets = ItemTags.create(new ResourceLocation("forge", "nuggets/copper"));
		tag(copperNuggets).add(HarderOres.ModItems.copper_nugget);
		tag(Tags.Items.NUGGETS).addTag(copperNuggets);
	}
}
