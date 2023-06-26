package com.draco18s.hardlib.api.recipe;

import java.util.Arrays;
import java.util.Collection;

import org.jetbrains.annotations.Nullable;

import com.draco18s.harderores.entity.SifterBlockEntity;
import com.draco18s.hardlib.api.HardLibAPI;
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

public class SiftingRecipe implements Recipe<SifterBlockEntity> {
	protected ResourceLocation registryName;
	protected int ingredientQuantity;
	protected Ingredient input;
	protected Ingredient result;
	
	public SiftingRecipe(ResourceLocation regName, Ingredient in, int qntIn, Ingredient out) {
		registryName = regName;
		input = in;
		ingredientQuantity = qntIn;
		result = out;
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
	public boolean matches(SifterBlockEntity sifter, Level world) {
		if(input.test(sifter.getItem(0))) return true;
		if(input.test(sifter.getItem(1))) return true;
		return false;
	}

	@Override
	public ItemStack assemble(SifterBlockEntity sifter, RegistryAccess regAccess) {
		return null;
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return w == 1 && h == 1;
	}

	public ItemStack getResultItem(RegistryAccess regAccess) {
		Collection<ItemStack> list = Arrays.asList(result.getItems());
		return list.stream().findFirst().orElse(ItemStack.EMPTY);
	}

	@Override
	public ResourceLocation getId() {
		return registryName;
	}

	@Override
	public RecipeType<?> getType() {
		return HardLibAPI.RecipeTypes.SIFTING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return HardLibAPI.RecipeSerializers.SIFTING;
	}
	
	public static class Serializer implements RecipeSerializer<SiftingRecipe> {
		@Override
		public SiftingRecipe fromJson(ResourceLocation reg, JsonObject jsonObject) {
			return new SiftingRecipe(reg,
					Ingredient.fromJson(jsonObject.get("ingredients")), jsonObject.get("ingredientQuantity").getAsInt(),
					Ingredient.fromJson(jsonObject.get("result")));
		}

		@Override
		public @Nullable SiftingRecipe fromNetwork(ResourceLocation reg, FriendlyByteBuf buffer) {
			return new SiftingRecipe(reg,
					Ingredient.fromNetwork(buffer), buffer.readInt(),
					Ingredient.fromNetwork(buffer));
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, SiftingRecipe recipe) {
			recipe.input.toNetwork(buffer);
			buffer.writeInt(recipe.ingredientQuantity);
			recipe.result.toNetwork(buffer);
		}
	}
}
