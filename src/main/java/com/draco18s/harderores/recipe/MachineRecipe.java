package com.draco18s.harderores.recipe;

import java.util.Arrays;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * A one-input machine recipe (sifting, grinding, packing): ingredientQuantity of the ingredient make count of the
 * result. The result is an ingredient so it can name a tag; the first matching item is made.
 * <pre>{"ingredients": ingredient, "ingredientQuantity": 1, "result": ingredient, "count": 1}</pre>
 */
public abstract class MachineRecipe implements Recipe<SingleRecipeInput> {
	protected final Ingredient input;
	protected final int ingredientQuantity;
	protected final Ingredient result;
	protected final int resultCount;

	protected MachineRecipe(Ingredient in, int qntIn, Ingredient out, int outputCount) {
		input = in;
		ingredientQuantity = qntIn;
		result = out;
		resultCount = outputCount;
	}

	public int getIngredientQuantity() {
		return ingredientQuantity;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(input);
		return list;
	}

	@Override
	public boolean matches(SingleRecipeInput in, Level world) {
		return input.test(in.item());
	}

	@Override
	public ItemStack assemble(SingleRecipeInput in, HolderLookup.Provider registries) {
		return getResultItem(registries).copy();
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return w == 1 && h == 1;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return Arrays.stream(result.getItems()).findFirst().map(stack -> stack.copyWithCount(resultCount)).orElse(ItemStack.EMPTY);
	}

	@FunctionalInterface
	public interface Factory<R extends MachineRecipe> {
		R create(Ingredient in, int qntIn, Ingredient out, int outputCount);
	}

	protected static <R extends MachineRecipe> MapCodec<R> codec(Factory<R> factory) {
		return RecordCodecBuilder.mapCodec(inst -> inst.group(
				Ingredient.CODEC_NONEMPTY.fieldOf("ingredients").forGetter((R r) -> r.input),
				Codec.INT.optionalFieldOf("ingredientQuantity", 1).forGetter((R r) -> r.ingredientQuantity),
				Ingredient.CODEC_NONEMPTY.fieldOf("result").forGetter((R r) -> r.result),
				Codec.INT.optionalFieldOf("count", 1).forGetter((R r) -> r.resultCount)
			).apply(inst, factory::create));
	}

	protected static <R extends MachineRecipe> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(Factory<R> factory) {
		return StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC, (R r) -> r.input,
				ByteBufCodecs.VAR_INT, (R r) -> r.ingredientQuantity,
				Ingredient.CONTENTS_STREAM_CODEC, (R r) -> r.result,
				ByteBufCodecs.VAR_INT, (R r) -> r.resultCount,
				factory::create);
	}
}
