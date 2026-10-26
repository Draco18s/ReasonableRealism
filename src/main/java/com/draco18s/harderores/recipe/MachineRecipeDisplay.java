package com.draco18s.harderores.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

/**
 * How a machine recipe shows in the recipe book (recipes stay on the server; clients get these): ingredientCount of
 * the ingredient make the result, in the crafting station.
 */
public record MachineRecipeDisplay(SlotDisplay ingredient, int ingredientCount, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
	public static final MapCodec<MachineRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			SlotDisplay.CODEC.fieldOf("ingredient").forGetter(MachineRecipeDisplay::ingredient),
			Codec.INT.fieldOf("ingredient_count").forGetter(MachineRecipeDisplay::ingredientCount),
			SlotDisplay.CODEC.fieldOf("result").forGetter(MachineRecipeDisplay::result),
			SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(MachineRecipeDisplay::craftingStation)
		).apply(inst, MachineRecipeDisplay::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, MachineRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
			SlotDisplay.STREAM_CODEC, MachineRecipeDisplay::ingredient,
			ByteBufCodecs.VAR_INT, MachineRecipeDisplay::ingredientCount,
			SlotDisplay.STREAM_CODEC, MachineRecipeDisplay::result,
			SlotDisplay.STREAM_CODEC, MachineRecipeDisplay::craftingStation,
			MachineRecipeDisplay::new);
	public static final RecipeDisplay.Type<MachineRecipeDisplay> TYPE = new RecipeDisplay.Type<MachineRecipeDisplay>(MAP_CODEC, STREAM_CODEC);

	@Override
	public RecipeDisplay.Type<MachineRecipeDisplay> type() {
		return TYPE;
	}

	@Override
	public boolean isEnabled(FeatureFlagSet enabledFeatures) {
		return ingredient.isEnabled(enabledFeatures) && RecipeDisplay.super.isEnabled(enabledFeatures);
	}
}
