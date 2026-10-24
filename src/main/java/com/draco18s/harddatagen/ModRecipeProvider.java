package com.draco18s.harddatagen;

import java.util.function.Consumer;

import com.draco18s.farming.HarderFarming;
import com.draco18s.flowers.OreFlowers;
import com.draco18s.harddatagen.custom.GrindingRecipeBuilder;
import com.draco18s.harddatagen.custom.SifterRecipeBuilder;
import com.draco18s.harderores.HarderOres;
import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

	public ModRecipeProvider(PackOutput p_248933_) {
		super(p_248933_);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		Grinding(finishedRecipeConsumer);
		Dusts(finishedRecipeConsumer);
		Farming(finishedRecipeConsumer);
		Machines(finishedRecipeConsumer);
		
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.COPPER_INGOT, 1)
			.requires(HarderOres.ModItems.copper_nugget, 9)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.copper_nugget).build()))
			.save(finishedRecipeConsumer);
		
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ExpandedIndustry.ModBlocks.machine_wood_hopper)
			.define('p', ItemTags.PLANKS)
			.pattern("p p")
			.pattern("p p")
			.pattern(" p ")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(ItemTags.PLANKS).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ExpandedIndustry.ModBlocks.machine_distributor)
			.define('h', Blocks.HOPPER)
			.define('i', Items.IRON_INGOT)
			.define('p', ItemTags.PLANKS)
			.pattern(" h ")
			.pattern(" i ")
			.pattern("ppp")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.HOPPER).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ExpandedIndustry.ModBlocks.rail_bridge)
			.define('r', Blocks.RAIL)
			.define('s', Blocks.SCAFFOLDING)
			.pattern("r")
			.pattern("s")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.RAIL).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ExpandedIndustry.ModBlocks.powered_rail_bridge)
			.define('r', Blocks.POWERED_RAIL)
			.define('s', Blocks.SCAFFOLDING)
			.pattern("r")
			.pattern("s")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.RAIL).build()))
			.save(finishedRecipeConsumer);
		
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, HarderOres.ModItems.diamond_studded_axe)
			.define('i', Items.IRON_INGOT)
			.define('d', HarderOres.ModItems.orechunk_diamond)
			.define('s', Tags.Items.RODS_WOODEN)
			.pattern("di")
			.pattern("is")
			.pattern(" s")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_diamond).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, HarderOres.ModItems.diamond_studded_pick)
			.define('i', Items.IRON_INGOT)
			.define('d', HarderOres.ModItems.orechunk_diamond)
			.define('s', Tags.Items.RODS_WOODEN)
			.pattern("did")
			.pattern(" s ")
			.pattern(" s ")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_diamond).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, HarderOres.ModItems.diamond_studded_shovel)
			.define('i', Items.IRON_INGOT)
			.define('d', HarderOres.ModItems.orechunk_diamond)
			.define('s', Tags.Items.RODS_WOODEN)
			.pattern("d")
			.pattern("i")
			.pattern("s")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_diamond).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, HarderOres.ModItems.diamond_studded_hoe)
			.define('i', Items.IRON_INGOT)
			.define('d', HarderOres.ModItems.orechunk_diamond)
			.define('s', Tags.Items.RODS_WOODEN)
			.pattern("di")
			.pattern(" s")
			.pattern(" s")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_diamond).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Items.BREAD)
			.define('w', HarderFarming.ModItems.largedust_flour)
			.pattern("www")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.WHEAT).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, HarderOres.ModBlocks.machine_millstone)
			.define('S', Tags.Items.STONE)
			.define('A', HarderOres.ModBlocks.machine_axel)
			.pattern("SSS")
			.pattern("SAS")
			.pattern("SSS")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Tags.Items.RAW_MATERIALS).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, HarderOres.ModBlocks.machine_axel, 2)
			.define('S', ItemTags.LOGS)
			.pattern("SSS")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Tags.Items.RAW_MATERIALS).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, HarderOres.ModBlocks.machine_windvane, 2)
			.define('S', Tags.Items.RODS_WOODEN)
			.define('W', ItemTags.WOOL)
			.pattern("SW")
			.pattern("SW")
			.pattern("SW")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Tags.Items.RAW_MATERIALS).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, HarderFarming.ModItems.butcher_knife)
			.define('i', Items.IRON_INGOT)
			.define('s', Tags.Items.RODS_WOODEN)
			.pattern(" s")
			.pattern("i ")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.IRON_INGOT).build()))
			.save(finishedRecipeConsumer);
	}
	
	private void Machines(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, HarderOres.ModBlocks.packager)
			.define('s', Tags.Items.STONE)
			.define('p', Items.PISTON)
			.pattern("sps")
			.pattern("s s")
			.pattern("sss")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.PISTON).build()))
			.save(finishedRecipeConsumer);
		// The sifter and sluice recipes are 1.14's (whose sluice recipe made a sifter by mistake).
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HarderOres.ModBlocks.machine_sifter)
			.define('P', ItemTags.PLANKS)
			.define('B', Items.BUCKET)
			.define('b', Items.IRON_BARS)
			.pattern("PBP")
			.pattern("PbP")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.tinydust_iron, HarderOres.ModItems.tinydust_gold, HarderOres.ModItems.tinydust_copper).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HarderOres.ModItems.sluice)
			.define('P', ItemTags.PLANKS)
			.define('s', Tags.Items.RODS_WOODEN)
			.pattern("sss")
			.pattern("PPP")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.GRAVEL).build()))
			.save(finishedRecipeConsumer);
	}

	private void Farming(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, HarderFarming.ModBlocks.tanner)
			.define('s', Tags.Items.RODS_WOODEN)
			.define('t', Items.STRING)
			.pattern("sss")
			.pattern("sts")
			.pattern("s s")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderFarming.ModItems.raw_leather).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, HarderFarming.ModItems.thermometer)
			.define('g', Items.GLASS_PANE)
			.define('i', Tags.Items.NUGGETS_IRON)
			.pattern("ggg")
			.pattern("gig")
			.pattern("ggg")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.GLASS_PANE).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, HarderFarming.ModItems.hydrometer)
			.define('g', Items.GLASS_PANE)
			.define('i', Tags.Items.NUGGETS_GOLD)
			.pattern("ggg")
			.pattern("gig")
			.pattern("ggg")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.GLASS_PANE).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(Items.BONE_MEAL, 2)
			.requires(Items.BONE)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.BONE).build()))
			.save(finishedRecipeConsumer);
	}

	private void Grinding(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		GrindingRecipeBuilder.grind(OreFlowers.ModItems.sticky_goo, 1)
			.requires(OreFlowers.ModItems.aveloz)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(OreFlowers.ModItems.aveloz).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(HarderFarming.ModItems.salt, 3)
			.requires(HarderFarming.ModItems.salt_chunk)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderFarming.ModBlocks.ore_salt).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(HarderFarming.ModItems.tinydust_sugar, 3)
			.requires(Items.SUGAR_CANE)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.SUGAR_CANE).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(HarderFarming.ModItems.tinydust_flour, 2)
			.requires(Items.WHEAT_SEEDS)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.WHEAT).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(HarderFarming.ModItems.tinydust_flour, 6)
			.requires(Items.WHEAT)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.WHEAT).build()))
			.save(finishedRecipeConsumer);
		

		GrindingRecipeBuilder.grind(HarderOres.ModItems.tinydust_copper, 2)
			.requires(Items.RAW_COPPER)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.RAW_COPPER).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(HarderOres.ModItems.tinydust_iron, 2)
			.requires(Items.RAW_IRON)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.RAW_IRON).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(HarderOres.ModItems.tinydust_gold, 2)
			.requires(Items.RAW_GOLD)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.RAW_GOLD).build()))
			.save(finishedRecipeConsumer);
		
		// Raw ore is a hard ore's small yield (1.14's ore chunk), so it smelts to a nugget; the vanilla raw ore to
		// ingot recipes are disabled (data/minecraft/recipes/*_from_*_raw_*.json).
		cook(finishedRecipeConsumer, Items.RAW_COPPER, HarderOres.ModItems.copper_nugget, 0.08f);
		cook(finishedRecipeConsumer, Items.RAW_GOLD, Items.GOLD_NUGGET, 0.08f);
		cook(finishedRecipeConsumer, Items.RAW_IRON, Items.IRON_NUGGET, 0.08f);
		cook(finishedRecipeConsumer, HarderOres.ModItems.orechunk_limonite, Items.RAW_IRON, 0.08f);
	}

	/**
	 * Smelting and blasting recipes for one input, like vanilla's ores. Ids are named for both items,
	 * since several inputs cook to the same output (ore chunks and tiny dusts both make nuggets).
	 */
	private void cook(Consumer<FinishedRecipe> finishedRecipeConsumer, Item input, Item output, float experience) {
		String name = ForgeRegistries.ITEMS.getKey(output).getPath() + "_from_%s_" + ForgeRegistries.ITEMS.getKey(input).getPath();
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, output, experience, 200)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(input).build()))
			.save(finishedRecipeConsumer, HarderOres.getRL(String.format(name, "smelting")));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), RecipeCategory.MISC, output, experience, 100)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(input).build()))
			.save(finishedRecipeConsumer, HarderOres.getRL(String.format(name, "blasting")));
	}

	private void Dusts(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SUGAR, 1)
			.requires(HarderFarming.ModItems.tinydust_sugar, 9)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderFarming.ModItems.tinydust_sugar).build()))
			.save(finishedRecipeConsumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, HarderFarming.ModItems.largedust_flour, 1)
			.requires(HarderFarming.ModItems.tinydust_flour, 9)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderFarming.ModItems.tinydust_flour).build()))
			.save(finishedRecipeConsumer);
		
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, HarderOres.ModItems.largedust_copper, 1)
			.requires(HarderOres.ModItems.tinydust_copper, 9)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.tinydust_copper).build()))
			.save(finishedRecipeConsumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.DIAMOND, 1)
			.requires(HarderOres.ModItems.orechunk_diamond, 9)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_diamond).build()))
			.save(finishedRecipeConsumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, HarderOres.ModItems.largedust_gold, 1)
			.requires(HarderOres.ModItems.tinydust_gold, 9)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.tinydust_gold).build()))
			.save(finishedRecipeConsumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, HarderOres.ModItems.largedust_iron, 1)
			.requires(HarderOres.ModItems.tinydust_iron, 9)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.tinydust_iron).build()))
			.save(finishedRecipeConsumer);
		
		SifterRecipeBuilder.sift(HarderFarming.ModItems.largedust_flour, 1, 8)
			.requires(HarderFarming.ModItems.tinydust_flour)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderFarming.ModItems.tinydust_flour).build()))
			.save(finishedRecipeConsumer);
		SifterRecipeBuilder.sift(Items.SUGAR, 1, 8)
			.requires(HarderFarming.ModItems.tinydust_sugar)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderFarming.ModItems.tinydust_sugar).build()))
			.save(finishedRecipeConsumer);
		SifterRecipeBuilder.sift(HarderOres.ModItems.largedust_copper, 1, 8)
			.requires(HarderOres.Tags.Items.TINY_COPPER_DUST)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.tinydust_copper).build()))
			.save(finishedRecipeConsumer);
		SifterRecipeBuilder.sift(HarderOres.ModItems.largedust_gold, 1, 8)
			.requires(HarderOres.Tags.Items.TINY_GOLD_DUST)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.tinydust_gold).build()))
			.save(finishedRecipeConsumer);
		SifterRecipeBuilder.sift(HarderOres.ModItems.largedust_iron, 1, 8)
			.requires(HarderOres.Tags.Items.TINY_IRON_DUST)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.tinydust_iron).build()))
			.save(finishedRecipeConsumer);
		
		cook(finishedRecipeConsumer, HarderOres.ModItems.tinydust_copper, HarderOres.ModItems.copper_nugget, 0.08f);
		cook(finishedRecipeConsumer, HarderOres.ModItems.tinydust_iron, Items.IRON_NUGGET, 0.08f);
		cook(finishedRecipeConsumer, HarderOres.ModItems.tinydust_gold, Items.GOLD_NUGGET, 0.08f);
		cook(finishedRecipeConsumer, HarderOres.ModItems.largedust_copper, Items.COPPER_INGOT, 0.1f);
		cook(finishedRecipeConsumer, HarderOres.ModItems.largedust_iron, Items.IRON_INGOT, 0.1f);
		cook(finishedRecipeConsumer, HarderOres.ModItems.largedust_gold, Items.GOLD_INGOT, 0.1f);
	}
}