package com.draco18s.harddatagen;

import com.draco18s.farming.HarderFarming;
import com.draco18s.farming.loot.GrassLootModifier;
import com.draco18s.farming.loot.RawLeatherModifier;
import com.draco18s.farming.loot.TemperatureCheck;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class LootModifierProvider extends GlobalLootModifierProvider {

	public LootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, HarderFarming.MODID);
	}

	@Override
	protected void start() {
		add("grass_modifier", new GrassLootModifier(
                new LootItemCondition[]{
                		LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS).or(
                				LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SHORT_GRASS)
                		).build(),
                		TemperatureCheck.INSTANCE
                })
        );
		add("raw_leather", new RawLeatherModifier(new LootItemCondition[0]));
	}
}
