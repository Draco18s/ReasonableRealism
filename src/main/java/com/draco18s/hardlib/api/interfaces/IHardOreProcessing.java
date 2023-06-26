package com.draco18s.hardlib.api.interfaces;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.NotImplementedException;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;

public interface IHardOreProcessing {
	
	/**
	 * The sifter "autocrafts" tiny dust piles to large dust piles.  Typically the crafting table recipe will be a
	 * full grid (9 tiny dusts) and craft into 1 large dust and the sifter will sift 8 to 1.
	 * @param input - Supplier for an Item {@link net.minecraft.tags.Tag}, all items in this tag will be registered as inputs. See {@link IHardOreProcessing#addSiftRecipe(ItemStack, ItemStack, boolean)}
	 * @param count - how many of the input is used
	 * @param output ItemStack including size
	 * @param registerOutput (optional) Pass true to register the output stack as a 1:1 sift (this prevents some items which
	 *  can be created normally from clogging the sifter, such as bonemeal).
	 */
	@Deprecated
	default void addSiftRecipe(Supplier<TagKey<Item>> input, int count, ItemStack output) {
		throw new NotImplementedException("Use recipe json assets!");
	}
	/**
	 * The sifter "autocrafts" tiny dust piles to large dust piles.  Typically the crafting table recipe will be a
	 * full grid (9 tiny dusts) and craft into 1 large dust and the sifter will sift 8 to 1.
	 * @param input ItemStack including size
	 * @param output ItemStack including size
	 * @param registerOutput (optional) Pass true to register the output stack as a 1:1 sift (this prevents some items which
	 *  can be created normally from clogging the sifter, such as bonemeal).
	 */
	@Deprecated
	default void addSiftRecipe(ItemStack input, ItemStack output, boolean registerOutput) {
		throw new NotImplementedException("Use recipe json assets!");
	}
	
	/**
	 * See {@link IHardOreProcessing#addSiftRecipe(ItemStack, ItemStack, boolean)}
	 * @param input
	 * @param output
	 */
	//public void addSiftRecipe(String input, int stackSize, ItemStack output, boolean registerOutput);
	
	/**
	 * See {@link IHardOreProcessing#addSiftRecipe(ItemStack, ItemStack, boolean)}
	 * @param input
	 * @param output
	 */
	//public void addSiftRecipe(String input, int stackSize, ItemStack output);
	
	/**
	 * The millstone will grind "raw" materials down into "dust" materials, typically tiny dust piles.
	 * @param input - Supplier for an Item {@link net.minecraft.tags.Tag}, all items in this tag will be registered as inputs. See {@link IHardOreProcessing#addMillRecipe(ItemStack, ItemStack)}
	 * @param output ItemStack including size
	 */
	@Deprecated
	default void addMillRecipe(Supplier<TagKey<Item>> input, ItemStack output) {
		throw new NotImplementedException("Use recipe json assets!");
	}
	
	/**
	 * The millstone will grind "raw" materials down into "dust" materials, typically tiny dust piles.
	 * @param input ItemStack to be ground
	 * @param output ItemStack including size
	 */
	@Deprecated
	default void addMillRecipe(ItemStack input, ItemStack output) {
		throw new NotImplementedException("Use recipe json assets!");
	}
	
	/**
	 * Returns the number items needed for the sifter recipes (minimum input stack size).
	 * @param itemStack
	 * @return minimum stack size for input in order to sift.
	 */
	public int getSiftAmount(ItemStack stack);
	
	/**
	 * Gets the item stack output for a given item stack input.
	 * @param itemstack1
	 * @param checkStackSize - whether or not we care if we have enough input or we just need to know if the input stack is valid
	 * @return an ItemStack sifting result. <b>Must</b> call {@link ItemSack#copy}.
	 */
	@Nonnull
	public ItemStack getSiftResult(ItemStack stack, boolean checkStackSize);

	/**
	 * Returns the item stack result from milling.
	 * @param itemStack
	 * @return an ItemStack milling result. <b>Must</b> call {@link ItemSack#copy}.
	 */
	@Nonnull
	public ItemStack getMillResult(ItemStack stack);
	
	/**
	 * Get a random ore based on sluice input
	 * @param rand
	 * @param item - the processed block: Sand, Gravel, or Dirt
	 * @return
	 */
	public Block getRandomSluiceResult(Random rand, Item item);
	
	/**
	 * Add an ore block to the sluice result list. Its drops will be what the sluice produces. 
	 * @param output
	 */
	@Deprecated
	default void addSluiceRecipe(Block output) {
		throw new NotImplementedException("Use recipe json assets!");
	}
	
	/**
	 * Add a recipe to the pressure packager
	 * @param input
	 * @param output
	 */
	@Deprecated
	default void addPressurePackRecipe(ItemStack input, ItemStack output) {
		throw new NotImplementedException("Use recipe json assets!");
	}
	
	/**
	 * Get pressure packager result
	 * @param key
	 * @param checkSize
	 * @return
	 */
	@Nonnull
	public ItemStack getPressurePackResult(ItemStack stack, boolean checkStackSize);
	
	/**
	 * Returns the number items needed for the pressure packing recipes (minimum input stack size).
	 * @param itemStack
	 * @return minimum stack size for input in order to sift.
	 */
	@Nonnull
	public int getPressurePackAmount(ItemStack stack);

	public List<Block> getRandomSluiceResults(Random rand, Item item);
	
	public void update(RecipeManager recipeManager);
}