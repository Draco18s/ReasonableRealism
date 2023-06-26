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

	public ModItemsTagProvider(PackOutput p_275204_, CompletableFuture<Provider> p_275194_,
			CompletableFuture<TagLookup<Block>> p_275634_, String modId,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(p_275204_, p_275194_, p_275634_, modId, existingFileHelper);
	}

	@Override
	protected void addTags(Provider p_256380_) {
		tag(ItemTags.COPPER_ORES).add(HarderOres.ModItems.orechunk_copper);
		tag(ItemTags.IRON_ORES).add(HarderOres.ModItems.orechunk_iron);
		tag(ItemTags.COPPER_ORES).add(HarderOres.ModItems.largedust_copper);
		tag(ItemTags.IRON_ORES).add(HarderOres.ModItems.largedust_iron);

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
