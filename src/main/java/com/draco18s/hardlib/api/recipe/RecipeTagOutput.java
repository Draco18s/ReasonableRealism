package com.draco18s.hardlib.api.recipe;

import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

/**
 * hardlib:tag_output - a shaped recipe whose result is the first item in a tag ({"result": {"tag": id}}), so it makes
 * whichever mod's copper (or the like) is installed. (Shaped recipes need an item result now, so this is its own
 * crafting recipe with a shaped pattern.)
 */
public class RecipeTagOutput extends NormalCraftingRecipe {
	public static final MapCodec<RecipeTagOutput> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			Recipe.CommonInfo.MAP_CODEC.forGetter(r -> r.commonInfo),
			CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(r -> r.bookInfo),
			ShapedRecipePattern.MAP_CODEC.forGetter(r -> r.tagPattern),
			TagKey.codec(Registries.ITEM).fieldOf("tag").codec().fieldOf("result").forGetter(r -> r.result)
		).apply(inst, RecipeTagOutput::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, RecipeTagOutput> STREAM_CODEC = StreamCodec.composite(
			Recipe.CommonInfo.STREAM_CODEC, r -> r.commonInfo,
			CraftingRecipe.CraftingBookInfo.STREAM_CODEC, r -> r.bookInfo,
			ShapedRecipePattern.STREAM_CODEC, r -> r.tagPattern,
			Identifier.STREAM_CODEC.map(id -> TagKey.create(Registries.ITEM, id), TagKey::location), r -> r.result,
			RecipeTagOutput::new);
	public static final RecipeSerializer<RecipeTagOutput> SERIALIZER = new RecipeSerializer<RecipeTagOutput>(MAP_CODEC, STREAM_CODEC);

	protected final ShapedRecipePattern tagPattern;
	protected final TagKey<Item> result;

	public RecipeTagOutput(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, TagKey<Item> resultIn) {
		super(commonInfo, bookInfo);
		tagPattern = pattern;
		result = resultIn;
	}

	@Override
	public RecipeSerializer<RecipeTagOutput> getSerializer() {
		return SERIALIZER;
	}

	@Nonnull
	public ItemStack getResultItem() {
		return BuiltInRegistries.ITEM.get(result)
				.flatMap(items -> items.stream().findFirst())
				.map(Holder::value).map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	@Override
	protected PlacementInfo createPlacementInfo() {
		return PlacementInfo.createFromOptionals(tagPattern.ingredients());
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		return tagPattern.matches(input);
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		return getResultItem();
	}

	@Override
	public List<RecipeDisplay> display() {
		SlotDisplay output = BuiltInRegistries.ITEM.get(result).<SlotDisplay>map(SlotDisplay.TagSlotDisplay::new).orElse(SlotDisplay.Empty.INSTANCE);
		return List.of(new ShapedCraftingRecipeDisplay(tagPattern.width(), tagPattern.height(),
				tagPattern.ingredients().stream().map(e -> e.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE)).toList(),
				output, new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
	}
}
