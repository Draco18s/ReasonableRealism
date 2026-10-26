package com.draco18s.harderores.recipe;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;
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
	private PlacementInfo placementInfo;

	protected MachineRecipe(Ingredient in, int qntIn, Ingredient out, int outputCount) {
		input = in;
		ingredientQuantity = qntIn;
		result = out;
		resultCount = outputCount;
	}

	public int getIngredientQuantity() {
		return ingredientQuantity;
	}

	public Ingredient getIngredient() {
		return input;
	}

	/** The block whose recipe book (or tooltip) shows this recipe. */
	protected abstract ItemLike getCraftingStation();

	@Override
	public abstract RecipeSerializer<? extends MachineRecipe> getSerializer();

	@Override
	public boolean matches(SingleRecipeInput in, Level world) {
		return input.test(in.item());
	}

	@Override
	public ItemStack assemble(SingleRecipeInput in) {
		return getResultItem();
	}

	public ItemStack getResultItem() {
		return result.items().findFirst().map(item -> new ItemStack(item, resultCount)).orElse(ItemStack.EMPTY);
	}

	@Override
	public boolean showNotification() {
		return true;
	}

	@Override
	public String group() {
		return "";
	}

	@Override
	public PlacementInfo placementInfo() {
		if(placementInfo == null) {
			placementInfo = PlacementInfo.create(input);
		}
		return placementInfo;
	}

	@Override
	public List<RecipeDisplay> display() {
		ItemStack out = getResultItem();
		SlotDisplay resultDisplay = out.isEmpty() ? SlotDisplay.Empty.INSTANCE : new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(out));
		return List.of(new MachineRecipeDisplay(input.display(), ingredientQuantity, resultDisplay, new SlotDisplay.ItemSlotDisplay(getCraftingStation().asItem().builtInRegistryHolder())));
	}

	@FunctionalInterface
	public interface Factory<R extends MachineRecipe> {
		R create(Ingredient in, int qntIn, Ingredient out, int outputCount);
	}

	protected static <R extends MachineRecipe> MapCodec<R> codec(Factory<R> factory) {
		return RecordCodecBuilder.mapCodec(inst -> inst.group(
				Ingredient.CODEC.fieldOf("ingredients").forGetter((R r) -> r.input),
				Codec.INT.optionalFieldOf("ingredientQuantity", 1).forGetter((R r) -> r.ingredientQuantity),
				Ingredient.CODEC.fieldOf("result").forGetter((R r) -> r.result),
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

	/** A machine recipe's serializer (serializers are records of their codecs now). */
	protected static <R extends MachineRecipe> RecipeSerializer<R> serializer(Factory<R> factory) {
		return new RecipeSerializer<R>(codec(factory), streamCodec(factory));
	}
}
