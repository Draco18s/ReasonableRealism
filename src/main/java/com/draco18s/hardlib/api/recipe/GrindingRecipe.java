package com.draco18s.hardlib.api.recipe;

import java.util.Arrays;
import java.util.Collection;

import org.jetbrains.annotations.Nullable;

import com.draco18s.harderores.entity.MillstoneBlockEntity;
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

public class GrindingRecipe implements Recipe<MillstoneBlockEntity> {
	protected ResourceLocation registryName;
	protected Ingredient input;
	protected Ingredient result;

	public GrindingRecipe(ResourceLocation reg, Ingredient ingred, Ingredient output) {
		registryName = reg;
		input = ingred;
		result = output;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(input);
		return list;
	}

	@Override
	public boolean matches(MillstoneBlockEntity millstone, Level world) {
		if(input.test(millstone.getItem(0))) return true;
		return false;
	}

	@Override
	public ItemStack assemble(MillstoneBlockEntity millstone, RegistryAccess regAccess) {
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
		return HardLibAPI.RecipeTypes.GRINDING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return HardLibAPI.RecipeSerializers.GRINDING;
	}
	
	public static class Serializer implements RecipeSerializer<GrindingRecipe> {
		@Override
		public GrindingRecipe fromJson(ResourceLocation reg, JsonObject jsonObject) {
			return new GrindingRecipe(reg,
					Ingredient.fromJson(jsonObject.get("ingredients")),
					Ingredient.fromJson(jsonObject.get("result")));
		}

		@Override
		public @Nullable GrindingRecipe fromNetwork(ResourceLocation reg, FriendlyByteBuf buffer) {
			return new GrindingRecipe(reg,
					Ingredient.fromNetwork(buffer),
					Ingredient.fromNetwork(buffer));
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, GrindingRecipe recipe) {
			recipe.input.toNetwork(buffer);
			recipe.result.toNetwork(buffer);
		}
	}
}
