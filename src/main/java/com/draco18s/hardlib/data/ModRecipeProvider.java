package com.draco18s.hardlib.data;

import java.util.function.Consumer;

import org.checkerframework.common.returnsreceiver.qual.This;

import com.draco18s.farming.HarderFarming;
import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.data.custom.GrindingRecipeBuilder;
import com.draco18s.hardlib.data.custom.SifterRecipeBuilder;
import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
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
		
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.COPPER_INGOT, 1)
			.requires(HarderOres.ModItems.copper_nugget, 9)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.copper_nugget).build()))
			.save(finishedRecipeConsumer);
		
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ExpandedIndustry.ModBlocks.machine_wood_hopper)
			.define('p', ItemTags.PLANKS)
			.pattern("p p")
			.pattern("p p")
			.pattern(" p ")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.DIORITE).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ExpandedIndustry.ModBlocks.machine_distributor)
			.define('h', Blocks.HOPPER)
			.define('i', Items.IRON_BARS)
			.define('p', ItemTags.PLANKS)
			.pattern(" h ")
			.pattern(" i ")
			.pattern("ppp")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.DIORITE).build()))
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
			.define('i', Items.IRON_BARS)
			.define('d', HarderOres.ModItems.orechunk_diamond)
			.define('s', Items.STICK)
			.pattern("di")
			.pattern("is")
			.pattern(" s")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_diamond).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, HarderOres.ModItems.diamond_studded_pick)
			.define('i', Items.IRON_BARS)
			.define('d', HarderOres.ModItems.orechunk_diamond)
			.define('s', Items.STICK)
			.pattern("did")
			.pattern(" s ")
			.pattern(" s ")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_diamond).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, HarderOres.ModItems.diamond_studded_shovel)
			.define('i', Items.IRON_BARS)
			.define('d', HarderOres.ModItems.orechunk_diamond)
			.define('s', Items.STICK)
			.pattern("d")
			.pattern("i")
			.pattern("s")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_diamond).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, HarderOres.ModItems.diamond_studded_hoe)
			.define('i', Items.IRON_BARS)
			.define('d', HarderOres.ModItems.orechunk_diamond)
			.define('s', Items.STICK)
			.pattern("di")
			.pattern(" s")
			.pattern(" s")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_diamond).build()))
			.save(finishedRecipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Items.BREAD)
			.define('w', HarderFarming.ModItems.largedust_flour)
			.pattern("www")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderFarming.ModItems.largedust_flour).build()))
			.save(finishedRecipeConsumer);
		/*ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,HarderOres.ModBlocks.millstone)
			.define('S', Tags.Items.STONE)
			.define('A', HarderOres.ModBlocks.axel)
			.pattern("SSS")
			.pattern("SAS")
			.pattern("SSS")
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.DIORITE).build()));*/
	}
	
	private void Grinding(Consumer<FinishedRecipe> finishedRecipeConsumer) {
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
			.requires(HarderOres.ModItems.orechunk_copper)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_copper).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(HarderOres.ModItems.tinydust_gold, 2)
			.requires(HarderOres.ModItems.orechunk_gold)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_gold).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(HarderOres.ModItems.tinydust_iron, 2)
			.requires(HarderOres.ModItems.orechunk_iron)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.orechunk_iron).build()))
			.save(finishedRecipeConsumer);	

		GrindingRecipeBuilder.grind(HarderOres.ModItems.largedust_copper, 2)
			.requires(Items.RAW_COPPER)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.RAW_COPPER).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(HarderOres.ModItems.largedust_iron, 2)
			.requires(Items.RAW_IRON)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.RAW_IRON).build()))
			.save(finishedRecipeConsumer);
		GrindingRecipeBuilder.grind(HarderOres.ModItems.largedust_gold, 2)
			.requires(Items.RAW_GOLD)
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.RAW_GOLD).build()))
			.save(finishedRecipeConsumer);
		
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(HarderOres.ModItems.orechunk_copper), RecipeCategory.MISC, HarderOres.ModItems.copper_nugget, 0.08f, 200);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(HarderOres.ModItems.orechunk_iron), RecipeCategory.MISC, Items.IRON_NUGGET, 0.08f, 200);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(HarderOres.ModItems.orechunk_gold), RecipeCategory.MISC, Items.GOLD_NUGGET, 0.08f, 200);
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
		
		SifterRecipeBuilder.sift(HarderOres.ModItems.largedust_copper, 1, 8)
			.requires(new TagKey<Item>(ForgeRegistries.Keys.ITEMS, new ResourceLocation(HarderOres.MODID, "dust/tiny/copper")))
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.tinydust_copper).build()))
			.save(finishedRecipeConsumer);
		SifterRecipeBuilder.sift(HarderOres.ModItems.largedust_gold, 1, 8)
			.requires(new TagKey<Item>(ForgeRegistries.Keys.ITEMS, new ResourceLocation(HarderOres.MODID, "dust/tiny/gold")))
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.tinydust_gold).build()))
			.save(finishedRecipeConsumer);
		SifterRecipeBuilder.sift(HarderOres.ModItems.largedust_iron, 1, 8)
			.requires(new TagKey<Item>(ForgeRegistries.Keys.ITEMS, new ResourceLocation(HarderOres.MODID, "dust/tiny/iron")))
			.unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(HarderOres.ModItems.tinydust_iron).build()))
			.save(finishedRecipeConsumer);
		
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(HarderOres.ModItems.tinydust_copper), RecipeCategory.MISC, HarderOres.ModItems.copper_nugget, 0.08f, 200);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(HarderOres.ModItems.tinydust_iron), RecipeCategory.MISC, Items.IRON_NUGGET, 0.08f, 200);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(HarderOres.ModItems.tinydust_gold), RecipeCategory.MISC, Items.GOLD_NUGGET, 0.08f, 200);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(HarderOres.ModItems.tinydust_copper), RecipeCategory.MISC, Items.COPPER_INGOT, 0.08f, 200);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(HarderOres.ModItems.tinydust_iron), RecipeCategory.MISC, Items.IRON_INGOT, 0.08f, 200);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(HarderOres.ModItems.tinydust_gold), RecipeCategory.MISC, Items.GOLD_INGOT, 0.08f, 200);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(HarderOres.ModItems.orechunk_limonite), RecipeCategory.MISC, Items.RAW_IRON, 0.08f, 200);
	}
}