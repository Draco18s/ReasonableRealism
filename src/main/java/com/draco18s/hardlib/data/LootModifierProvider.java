package com.draco18s.hardlib.data;

import com.draco18s.farming.HarderFarming;
import com.draco18s.farming.loot.GrassLootModifier;
import com.draco18s.hardlib.api.loot.TemperatureCheck;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class LootModifierProvider extends GlobalLootModifierProvider {

	public LootModifierProvider(PackOutput output) {
		super(output, HarderFarming.MODID);
	}

	@Override
	protected void start() {
		add("grass_modifier", new GrassLootModifier(
                new LootItemCondition[]{
                		LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS).or(
                				LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.GRASS)
                		).build(),
                		new TemperatureCheck()
                })
        );
	}
}
