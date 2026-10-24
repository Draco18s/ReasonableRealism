package com.draco18s.hardlib.api.recipe;

import javax.annotation.Nonnull;

import com.draco18s.hardlib.api.HardLibAPI;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/**
 * hardlib:tag_output - a shaped recipe whose result is the first item in a tag ({"result": {"tag": id}}), so it makes
 * whichever mod's copper (or the like) is installed.
 */
public class RecipeTagOutput extends ShapedRecipe {
	protected final ShapedRecipePattern tagPattern;
	protected final TagKey<Item> result;

	public RecipeTagOutput(String groupIn, CraftingBookCategory category, ShapedRecipePattern pattern, TagKey<Item> resultIn) {
		super(groupIn, category, pattern, ItemStack.EMPTY, false);
		tagPattern = pattern;
		result = resultIn;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return HardLibAPI.RecipeSerializers.TAG_OUTPUT;
	}

	@Override
	@Nonnull
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return BuiltInRegistries.ITEM.getTag(result)
				.flatMap(items -> items.stream().findFirst())
				.map(Holder::value).map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		return getResultItem(registries);
	}

	public static class Serializer implements RecipeSerializer<RecipeTagOutput> {
		private static final MapCodec<RecipeTagOutput> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
				CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
				ShapedRecipePattern.MAP_CODEC.forGetter(r -> r.tagPattern),
				TagKey.codec(Registries.ITEM).fieldOf("tag").codec().fieldOf("result").forGetter(r -> r.result)
			).apply(inst, RecipeTagOutput::new));
		private static final StreamCodec<RegistryFriendlyByteBuf, RecipeTagOutput> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, ShapedRecipe::getGroup,
				CraftingBookCategory.STREAM_CODEC, ShapedRecipe::category,
				ShapedRecipePattern.STREAM_CODEC, r -> r.tagPattern,
				ResourceLocation.STREAM_CODEC.map(id -> TagKey.create(Registries.ITEM, id), TagKey::location), r -> r.result,
				RecipeTagOutput::new);

		@Override
		public MapCodec<RecipeTagOutput> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, RecipeTagOutput> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
