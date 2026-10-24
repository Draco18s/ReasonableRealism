package com.draco18s.harderores.recipe;

import com.draco18s.harderores.HarderOres;
import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/** What the pressure packager packs (besides storage recipes, see OreProcessingRecipes). */
public class PackingRecipe extends MachineRecipe {
	public PackingRecipe(Ingredient in, int qntIn, Ingredient out, int outputCount) {
		super(in, qntIn, out, outputCount);
	}

	@Override
	public RecipeType<?> getType() {
		return HarderOres.RecipeTypes.PACKING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return HarderOres.RecipeSerializers.PACKING;
	}

	public static class Serializer implements RecipeSerializer<PackingRecipe> {
		private static final MapCodec<PackingRecipe> CODEC = MachineRecipe.codec(PackingRecipe::new);
		private static final StreamCodec<RegistryFriendlyByteBuf, PackingRecipe> STREAM_CODEC = MachineRecipe.streamCodec(PackingRecipe::new);

		@Override
		public MapCodec<PackingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, PackingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
