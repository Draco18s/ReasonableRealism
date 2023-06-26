package com.draco18s.hardlib.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.loot.function.HarderSetCount;
import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

@SuppressWarnings("unused")
public class ModLootTableProvider extends LootTableProvider {
	public ModLootTableProvider(PackOutput output)
	{
		super(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK)));
	}

	private static class BlockLootTable extends BlockLootSubProvider {
		public BlockLootTable() { super(Set.of(), FeatureFlags.VANILLA_SET); }

		@Override
		protected Iterable<Block> getKnownBlocks() {
			List<Block> knownBlocks = new ArrayList<Block>();
			//knownBlocks.add(ExpandedIndustry.ModBlocks.machine_wood_hopper);
			//knownBlocks.add(ExpandedIndustry.ModBlocks.machine_distributor);
			knownBlocks.add(HarderOres.ModBlocks.ore_hardcopper);
			knownBlocks.add(HarderOres.ModBlocks.ore_harddiamond);
			knownBlocks.add(HarderOres.ModBlocks.ore_hardgold);
			knownBlocks.add(HarderOres.ModBlocks.ore_hardiron);
			//knownBlocks.add(HarderOres.ModBlocks.ore_limonite);
			//knownBlocks.add(HarderOres.ModBlocks.millstone);
			return knownBlocks;
		}

		@Override
		protected void generate() {
			//dropSelf(ExpandedIndustry.ModBlocks.machine_wood_hopper);
			//dropSelf(ExpandedIndustry.ModBlocks.machine_distributor);
			dropsOreChunks(HarderOres.ModBlocks.ore_hardcopper, HarderOres.ModItems.orechunk_copper);
			dropsOreChunks(HarderOres.ModBlocks.ore_harddiamond, HarderOres.ModItems.orechunk_diamond);
			dropsOreChunks(HarderOres.ModBlocks.ore_hardgold, HarderOres.ModItems.orechunk_gold);
			dropsOreChunks(HarderOres.ModBlocks.ore_hardiron, HarderOres.ModItems.orechunk_iron);
			//dropSelf(HarderOres.ModBlocks.millstone);
			//dropOtherOrSilkTouch(HarderOres.ModBlocks.ore_limonite, HarderOres.ModItems.orechunk_limonite);
		}

		private void dropOtherOrSilkTouch(Block block, ItemLike item) {
			add(block, createSelfDropDispatchTable(block, HAS_SILK_TOUCH, itemToBuilder(block, item)));
		}

		private Builder<?> itemToBuilder(Block block, ItemLike item) {
			return LootItem.lootTableItem(item).apply(applyExplosionCondition(block, SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))));
		}

		private void dropsOreChunks(Block block, Item chunk) {
			add(block, createSelfDropDispatchTable(block, HAS_SILK_TOUCH, oreToChunk(block, chunk, 6)));
		}

		private LootPoolEntryContainer.Builder<?> oreToChunk(Block oreHardiron, Item orechunkIron, float divisor) {
			return LootItem.lootTableItem(orechunkIron).apply(applyExplosionCondition(orechunkIron, HarderSetCount.addHardOreCount(ConstantValue.exactly(divisor))));
		}

		protected static LootTable.Builder createSelfDropDispatchTable(Block block, LootItemCondition.Builder conditionMet, LootPoolEntryContainer.Builder<?> otherwise) {
			return LootTable.lootTable().withPool(
					LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(
							LootItem.lootTableItem(block).when(conditionMet).otherwise(otherwise)));
		}
	}
}
