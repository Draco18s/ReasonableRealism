package com.draco18s.harddatagen;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.draco18s.farming.HarderFarming;
import com.draco18s.farming.loot.GrassLootModifier;
import com.draco18s.farming.loot.RawLeatherModifier;
import com.draco18s.farming.loot.TemperatureCheck;

import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

public class LootModifierProvider extends GlobalLootModifierProvider {

	public LootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, HarderFarming.MODID);
	}

	@Override
	protected void start() {
		// Modifiers have one condition now: both of the old ones.
		LootItemCondition.Builder grass = AnyOfCondition.anyOf(
				MatchBlock.blockMatches(BlockPredicate.Builder.block().of(BuiltInRegistries.BLOCK, Blocks.TALL_GRASS)),
				MatchBlock.blockMatches(BlockPredicate.Builder.block().of(BuiltInRegistries.BLOCK, Blocks.SHORT_GRASS)));
		add("grass_modifier", new GrassLootModifier(
				Optional.of(Holder.direct(AllOfCondition.allOf(grass, () -> TemperatureCheck.INSTANCE).build())), IGlobalLootModifier.DEFAULT_PRIORITY));
		add("raw_leather", new RawLeatherModifier(Optional.empty(), IGlobalLootModifier.DEFAULT_PRIORITY));
	}
}
