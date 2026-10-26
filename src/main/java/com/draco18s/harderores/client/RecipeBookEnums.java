package com.draco18s.harderores.client;

import java.util.List;
import java.util.function.Supplier;

import com.draco18s.harderores.HarderOres;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

/**
 * The machines' recipe book categories, added to the enum at load by NeoForge (META-INF/enumextensions.json names
 * these fields). Their icons are suppliers: the blocks aren't registered when the enum is extended.
 */
public class RecipeBookEnums {
	public static final EnumProxy<RecipeBookCategories> SIFTING = new EnumProxy<RecipeBookCategories>(RecipeBookCategories.class,
			(Supplier<List<ItemStack>>)() -> List.of(new ItemStack(HarderOres.ModBlocks.machine_sifter)));
	public static final EnumProxy<RecipeBookCategories> GRINDING = new EnumProxy<RecipeBookCategories>(RecipeBookCategories.class,
			(Supplier<List<ItemStack>>)() -> List.of(new ItemStack(HarderOres.ModBlocks.machine_millstone)));
	public static final EnumProxy<RecipeBookCategories> PACKING = new EnumProxy<RecipeBookCategories>(RecipeBookCategories.class,
			(Supplier<List<ItemStack>>)() -> List.of(new ItemStack(HarderOres.ModBlocks.packager)));
}
