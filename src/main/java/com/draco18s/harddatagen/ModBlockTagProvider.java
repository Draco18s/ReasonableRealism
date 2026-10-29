package com.draco18s.harddatagen;

import java.util.concurrent.CompletableFuture;

import com.draco18s.farming.HarderFarming;
import com.draco18s.flowers.OreFlowers;
import com.draco18s.harderores.HarderOres;
import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockTagProvider extends BlockTagsProvider {
	public ModBlockTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider) {
		super(output, lookupProvider, HarderOres.MODID);
	}

	/** Tags hold registry keys now. */
	private static ResourceKey<Block> key(Block block) {
		return block.builtInRegistryHolder().key();
	}

	@Override
	protected void addTags(Provider provider) {
		tag(BlockTags.RAILS).add(key(ExpandedIndustry.ModBlocks.rail_bridge));
		for(DeferredHolder<Block, Block> flower : OreFlowers.FLOWERS.values()) {
			tag(flower.get() instanceof DoublePlantBlock ? Tags.BlockItems.FLOWERS_TALL.block() : BlockTags.SMALL_FLOWERS).add(key(flower.get()));
		}
		tag(BlockTags.RAILS).add(key(ExpandedIndustry.ModBlocks.powered_rail_bridge));

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderOres.ModBlocks.ore_hardcopper));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderOres.ModBlocks.packager));
		tag(BlockTags.NEEDS_STONE_TOOL).add(key(HarderOres.ModBlocks.packager));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderOres.ModBlocks.ore_harddiamond));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderOres.ModBlocks.ore_hardgold));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderOres.ModBlocks.ore_hardiron));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderOres.ModBlocks.ore_harddeepslate_copper));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderOres.ModBlocks.ore_harddeepslate_diamond));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderOres.ModBlocks.ore_harddeepslate_gold));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderOres.ModBlocks.ore_harddeepslate_iron));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderOres.ModBlocks.machine_millstone));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(key(HarderFarming.ModBlocks.ore_salt));
		tag(BlockTags.MINEABLE_WITH_AXE).add(key(HarderFarming.ModBlocks.tanner));
		// Weedy farmland is farmland to everything but the weeds.
		tag(BlockTags.MINEABLE_WITH_SHOVEL).add(key(HarderFarming.ModBlocks.weedy_farmland));
		tag(BlockTags.SUPPORTS_CROPS).add(key(HarderFarming.ModBlocks.weedy_farmland));
		tag(BlockTags.SUPPORTS_VEGETATION).add(key(HarderFarming.ModBlocks.weedy_farmland));
		tag(BlockTags.GROWS_CROPS).add(key(HarderFarming.ModBlocks.weedy_farmland));
		tag(BlockTags.MINEABLE_WITH_SHOVEL).add(key(HarderFarming.ModBlocks.mulched_farmland));
		tag(BlockTags.SUPPORTS_CROPS).add(key(HarderFarming.ModBlocks.mulched_farmland));
		tag(BlockTags.SUPPORTS_VEGETATION).add(key(HarderFarming.ModBlocks.mulched_farmland));
		tag(BlockTags.GROWS_CROPS).add(key(HarderFarming.ModBlocks.mulched_farmland));

		tag(BlockTags.MINEABLE_WITH_AXE).add(key(ExpandedIndustry.ModBlocks.machine_distributor));
		tag(BlockTags.MINEABLE_WITH_AXE).add(key(ExpandedIndustry.ModBlocks.machine_wood_hopper));
		tag(BlockTags.MINEABLE_WITH_AXE).add(key(ExpandedIndustry.ModBlocks.rail_bridge));
		tag(BlockTags.MINEABLE_WITH_AXE).add(key(ExpandedIndustry.ModBlocks.powered_rail_bridge));
		tag(BlockTags.MINEABLE_WITH_AXE).add(key(HarderOres.ModBlocks.machine_sifter));

		tag(BlockTags.MINEABLE_WITH_SHOVEL).add(key(HarderOres.ModBlocks.ore_limonite));

		tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(key(ExpandedIndustry.ModBlocks.machine_wood_hopper));
		tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(key(ExpandedIndustry.ModBlocks.machine_distributor));
		tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(key(HarderOres.ModBlocks.ore_limonite));
		tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(key(HarderOres.ModBlocks.machine_millstone));
		tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(key(HarderFarming.ModBlocks.ore_salt));

		tag(BlockTags.NEEDS_STONE_TOOL).add(key(HarderOres.ModBlocks.ore_hardcopper));
		tag(BlockTags.NEEDS_STONE_TOOL).add(key(HarderOres.ModBlocks.ore_hardiron));
		tag(BlockTags.NEEDS_STONE_TOOL).add(key(HarderOres.ModBlocks.ore_harddeepslate_copper));
		tag(BlockTags.NEEDS_STONE_TOOL).add(key(HarderOres.ModBlocks.ore_harddeepslate_iron));

		tag(BlockTags.NEEDS_IRON_TOOL).add(key(HarderOres.ModBlocks.ore_hardgold));
		tag(BlockTags.NEEDS_IRON_TOOL).add(key(HarderOres.ModBlocks.ore_harddiamond));
		tag(BlockTags.NEEDS_IRON_TOOL).add(key(HarderOres.ModBlocks.ore_harddeepslate_gold));
		tag(BlockTags.NEEDS_IRON_TOOL).add(key(HarderOres.ModBlocks.ore_harddeepslate_diamond));

		tag(BlockTags.COPPER_ORES).add(key(HarderOres.ModBlocks.ore_hardcopper));
		tag(BlockItemTags.DIAMOND_ORES.block()).add(key(HarderOres.ModBlocks.ore_harddiamond));
		tag(BlockTags.GOLD_ORES).add(key(HarderOres.ModBlocks.ore_hardgold));
		tag(BlockTags.IRON_ORES).add(key(HarderOres.ModBlocks.ore_hardiron));
		tag(BlockTags.COPPER_ORES).add(key(HarderOres.ModBlocks.ore_harddeepslate_copper));
		tag(BlockItemTags.DIAMOND_ORES.block()).add(key(HarderOres.ModBlocks.ore_harddeepslate_diamond));
		tag(BlockTags.GOLD_ORES).add(key(HarderOres.ModBlocks.ore_harddeepslate_gold));
		tag(BlockTags.IRON_ORES).add(key(HarderOres.ModBlocks.ore_harddeepslate_iron));

		tag(HarderFarming.Tags.Blocks.MINABLE_WITH_KNIFE).add(key(Blocks.SLIME_BLOCK));
		tag(HarderFarming.Tags.Blocks.MINABLE_WITH_KNIFE).add(key(Blocks.SPONGE));
		tag(HarderFarming.Tags.Blocks.MINABLE_WITH_KNIFE).add(key(Blocks.PUMPKIN));
		tag(HarderFarming.Tags.Blocks.MINABLE_WITH_KNIFE).add(key(Blocks.CARVED_PUMPKIN));
		tag(HarderFarming.Tags.Blocks.MINABLE_WITH_KNIFE).add(key(Blocks.MELON));

		tag(HarderFarming.Tags.Blocks.COW_FORAGE).add(key(Blocks.GRASS_BLOCK), key(Blocks.SHORT_GRASS), key(Blocks.TALL_GRASS),
				key(Blocks.FERN), key(Blocks.LARGE_FERN), key(Blocks.HAY_BLOCK)).addTag(BlockTags.LEAVES);
		tag(HarderFarming.Tags.Blocks.MOOSHROOM_FORAGE).addTag(HarderFarming.Tags.Blocks.COW_FORAGE).add(key(Blocks.MYCELIUM));
	}
}
