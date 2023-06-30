package com.draco18s.hardlib.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import com.draco18s.farming.HarderFarming;
import com.draco18s.harderores.HarderOres;
import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagProvider extends BlockTagsProvider {
	public ModBlockTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		tag(BlockTags.RAILS).add(ExpandedIndustry.ModBlocks.rail_bridge);
		tag(BlockTags.RAILS).add(ExpandedIndustry.ModBlocks.powered_rail_bridge);

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(HarderOres.ModBlocks.ore_hardcopper);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(HarderOres.ModBlocks.ore_harddiamond);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(HarderOres.ModBlocks.ore_hardgold);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(HarderOres.ModBlocks.ore_hardiron);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(HarderOres.ModBlocks.ore_harddeepslate_copper);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(HarderOres.ModBlocks.ore_harddeepslate_diamond);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(HarderOres.ModBlocks.ore_harddeepslate_gold);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(HarderOres.ModBlocks.ore_harddeepslate_iron);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(HarderOres.ModBlocks.machine_millstone);

		tag(BlockTags.MINEABLE_WITH_AXE).add(ExpandedIndustry.ModBlocks.machine_distributor);
		tag(BlockTags.MINEABLE_WITH_AXE).add(ExpandedIndustry.ModBlocks.machine_wood_hopper);
		tag(BlockTags.MINEABLE_WITH_AXE).add(ExpandedIndustry.ModBlocks.rail_bridge);
		tag(BlockTags.MINEABLE_WITH_AXE).add(ExpandedIndustry.ModBlocks.powered_rail_bridge);
		tag(BlockTags.MINEABLE_WITH_AXE).add(HarderOres.ModBlocks.machine_sifter);

		tag(BlockTags.MINEABLE_WITH_SHOVEL).add(HarderOres.ModBlocks.ore_limonite);

		tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(ExpandedIndustry.ModBlocks.machine_wood_hopper);
		tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(ExpandedIndustry.ModBlocks.machine_distributor);
		tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(HarderOres.ModBlocks.ore_limonite);
		tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(HarderOres.ModBlocks.machine_millstone);

		tag(BlockTags.NEEDS_STONE_TOOL).add(HarderOres.ModBlocks.ore_hardcopper);
		tag(BlockTags.NEEDS_STONE_TOOL).add(HarderOres.ModBlocks.ore_hardiron);
		tag(BlockTags.NEEDS_STONE_TOOL).add(HarderOres.ModBlocks.ore_harddeepslate_copper);
		tag(BlockTags.NEEDS_STONE_TOOL).add(HarderOres.ModBlocks.ore_harddeepslate_iron);

		tag(BlockTags.NEEDS_IRON_TOOL).add(HarderOres.ModBlocks.ore_hardgold);
		tag(BlockTags.NEEDS_IRON_TOOL).add(HarderOres.ModBlocks.ore_harddiamond);
		tag(BlockTags.NEEDS_IRON_TOOL).add(HarderOres.ModBlocks.ore_harddeepslate_gold);
		tag(BlockTags.NEEDS_IRON_TOOL).add(HarderOres.ModBlocks.ore_harddeepslate_diamond);

		tag(BlockTags.COPPER_ORES).add(HarderOres.ModBlocks.ore_hardcopper);
		tag(BlockTags.DIAMOND_ORES).add(HarderOres.ModBlocks.ore_harddiamond);
		tag(BlockTags.GOLD_ORES).add(HarderOres.ModBlocks.ore_hardgold);
		tag(BlockTags.IRON_ORES).add(HarderOres.ModBlocks.ore_hardiron);
		tag(BlockTags.COPPER_ORES).add(HarderOres.ModBlocks.ore_harddeepslate_copper);
		tag(BlockTags.DIAMOND_ORES).add(HarderOres.ModBlocks.ore_harddeepslate_diamond);
		tag(BlockTags.GOLD_ORES).add(HarderOres.ModBlocks.ore_harddeepslate_gold);
		tag(BlockTags.IRON_ORES).add(HarderOres.ModBlocks.ore_harddeepslate_iron);

		tag(HarderFarming.ModTags.MINABLE_WITH_KNIFE).add(Blocks.SLIME_BLOCK);
		tag(HarderFarming.ModTags.MINABLE_WITH_KNIFE).add(Blocks.SPONGE);
		tag(HarderFarming.ModTags.MINABLE_WITH_KNIFE).add(Blocks.PUMPKIN);
		tag(HarderFarming.ModTags.MINABLE_WITH_KNIFE).add(Blocks.CARVED_PUMPKIN);
		tag(HarderFarming.ModTags.MINABLE_WITH_KNIFE).add(Blocks.MELON);
	}
}
