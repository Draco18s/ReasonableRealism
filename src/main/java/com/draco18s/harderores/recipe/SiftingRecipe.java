package com.draco18s.harderores.recipe;

import com.draco18s.harderores.HarderOres;
import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/** What the sifter sifts. */
public class SiftingRecipe extends MachineRecipe {
	public SiftingRecipe(Ingredient in, int qntIn, Ingredient out, int outputCount) {
		super(in, qntIn, out, outputCount);
	}

	@Override
	public RecipeType<?> getType() {
		return HarderOres.RecipeTypes.SIFTING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return HarderOres.RecipeSerializers.SIFTING;
	}

	public static class Serializer implements RecipeSerializer<SiftingRecipe> {
		private static final MapCodec<SiftingRecipe> CODEC = MachineRecipe.codec(SiftingRecipe::new);
		private static final StreamCodec<RegistryFriendlyByteBuf, SiftingRecipe> STREAM_CODEC = MachineRecipe.streamCodec(SiftingRecipe::new);

		@Override
		public MapCodec<SiftingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SiftingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
