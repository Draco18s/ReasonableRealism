package com.draco18s.hardlib.api.interfaces;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IHardOreProcessing {
	/**
	 * The sifter "autocrafts" tiny dust piles to large dust piles.  Typically the crafting table recipe will be a
	 * full grid (9 tiny dusts) and craft into 1 large dust and the sifter will sift 8 to 1.
	 * @param input ItemStack including metadata and size
	 * @param output ItemStack including metadata and size
	 * @param registerOutput (optional) Pass true to register the output stack as a 1:1 sift (this prevents some items which
	 *  can be created normally from clogging the sifter, such as bonemeal).
	 */
	public void addSiftRecipe(ItemStack input, ItemStack output, boolean registerOutput);
	
	/**
	 * See {@link IHardOreProcessing#addSiftRecipe(ItemStack, ItemStack, boolean)}
	 * @param input
	 * @param output
	 */
	public void addSiftRecipe(ItemStack input, ItemStack output);
	
	/**
	 * See {@link IHardOreProcessing#addSiftRecipe(ItemStack, ItemStack, boolean)}
	 * @param input
	 * @param output
	 */
	public void addSiftRecipe(String input, int stackSize, ItemStack output, boolean registerOutput);
	
	/**
	 * See {@link IHardOreProcessing#addSiftRecipe(ItemStack, ItemStack, boolean)}
	 * @param input
	 * @param output
	 */
	public void addSiftRecipe(String input, int stackSize, ItemStack output);
	
	/**
	 * The millstone will grind "raw" materials down into "dust" materials, typically tiny dust piles.
	 * @param input ItemStack to be ground
	 */
	public void addMillRecipe(ItemStack input, ItemStack output);
	
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
	void addSluiceRecipe(Block output);
	
	/**
	 * Add a recipe to the pressure packager
	 * @param input
	 * @param output
	 */
	public void addPressurePackRecipe(ItemStack input, ItemStack output);
	
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
}