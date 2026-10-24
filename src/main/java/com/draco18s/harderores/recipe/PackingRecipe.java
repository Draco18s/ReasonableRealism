package com.draco18s.harderores.recipe;

import java.util.Arrays;
import java.util.Collection;

import org.jetbrains.annotations.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.PackagerBlockEntity;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * A pressure packager recipe: ingredientQuantity of the input packed into the result. Storage recipes
 * (9 ingots into a block and the like) are packed too without one; see OreProcessingRecipes.update.
 */
public class PackingRecipe implements Recipe<PackagerBlockEntity> {
	protected ResourceLocation registryName;
	protected int ingredientQuantity;
	protected Ingredient input;
	protected Ingredient result;
	protected int resultCount;

	public PackingRecipe(ResourceLocation regName, Ingredient in, int qntIn, Ingredient out, int outputCount) {
		registryName = regName;
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
	public boolean matches(PackagerBlockEntity packager, Level world) {
		return input.test(packager.getItem(0)) || input.test(packager.getItem(1));
	}

	@Override
	public ItemStack assemble(PackagerBlockEntity packager, RegistryAccess regAccess) {
		return getResultItem(regAccess);
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return w == 1 && h == 1;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess regAccess) {
		Collection<ItemStack> list = Arrays.asList(result.getItems());
		return list.stream().findFirst().map(stack -> stack.copyWithCount(resultCount)).orElse(ItemStack.EMPTY);
	}

	@Override
	public ResourceLocation getId() {
		return registryName;
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
		@Override
		public PackingRecipe fromJson(ResourceLocation reg, JsonObject jsonObject) {
			return new PackingRecipe(reg,
					Ingredient.fromJson(jsonObject.get("ingredients")), jsonObject.get("ingredientQuantity").getAsInt(),
					Ingredient.fromJson(jsonObject.get("result")), GrindingRecipe.resultCount(jsonObject));
		}

		@Override
		public @Nullable PackingRecipe fromNetwork(ResourceLocation reg, FriendlyByteBuf buffer) {
			return new PackingRecipe(reg,
					Ingredient.fromNetwork(buffer), buffer.readVarInt(),
					Ingredient.fromNetwork(buffer), buffer.readVarInt());
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, PackingRecipe recipe) {
			recipe.input.toNetwork(buffer);
			buffer.writeVarInt(recipe.ingredientQuantity);
			recipe.result.toNetwork(buffer);
			buffer.writeVarInt(recipe.resultCount);
		}
	}
}
