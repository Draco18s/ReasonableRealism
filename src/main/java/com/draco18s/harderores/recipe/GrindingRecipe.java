package com.draco18s.harderores.recipe;

import com.draco18s.harderores.HarderOres;
import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/** What the millstone grinds. */
public class GrindingRecipe extends MachineRecipe {
	public GrindingRecipe(Ingredient in, int qntIn, Ingredient out, int outputCount) {
		super(in, qntIn, out, outputCount);
	}

	@Override
	public RecipeType<?> getType() {
		return HarderOres.RecipeTypes.GRINDING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return HarderOres.RecipeSerializers.GRINDING;
	}

	public static class Serializer implements RecipeSerializer<GrindingRecipe> {
		private static final MapCodec<GrindingRecipe> CODEC = MachineRecipe.codec(GrindingRecipe::new);
		private static final StreamCodec<RegistryFriendlyByteBuf, GrindingRecipe> STREAM_CODEC = MachineRecipe.streamCodec(GrindingRecipe::new);

		@Override
		public MapCodec<GrindingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, GrindingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
