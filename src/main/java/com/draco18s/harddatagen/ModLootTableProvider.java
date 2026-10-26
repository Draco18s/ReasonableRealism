package com.draco18s.harddatagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.draco18s.farming.HarderFarming;
import com.draco18s.flowers.OreFlowers;
import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.loot.function.HarderSetCount;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.advancements.predicates.entity.EntityFlagsPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.floats.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.neoforged.neoforge.registries.DeferredHolder;

/** Block loot tables: registry entries made at data generation now (see ModDataGenerator). */
public class ModLootTableProvider {
	public static LootTableProvider create() {
		return new LootTableProvider(Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK)));
	}

	private static class BlockLootTable extends BlockLootSubProvider {
		public BlockLootTable(LootTableSubProvider.Context context) { super(Set.of(), FeatureFlags.VANILLA_SET, context); }

		@Override
		protected Iterable<Block> getKnownBlocks() {
			List<Block> knownBlocks = new ArrayList<Block>();
			knownBlocks.add(HarderFarming.ModBlocks.crop_winter_wheat);
			knownBlocks.add(HarderFarming.ModBlocks.ore_salt);
			knownBlocks.add(HarderFarming.ModBlocks.crop_weeds);
			knownBlocks.add(HarderFarming.ModBlocks.tanner);
			knownBlocks.add(ExpandedIndustry.ModBlocks.rail_bridge);
			knownBlocks.add(ExpandedIndustry.ModBlocks.powered_rail_bridge);
			knownBlocks.add(ExpandedIndustry.ModBlocks.machine_wood_hopper);
			knownBlocks.add(ExpandedIndustry.ModBlocks.machine_distributor);
			knownBlocks.add(HarderOres.ModBlocks.ore_hardcopper);
			knownBlocks.add(HarderOres.ModBlocks.ore_harddiamond);
			knownBlocks.add(HarderOres.ModBlocks.ore_hardgold);
			knownBlocks.add(HarderOres.ModBlocks.ore_hardiron);
			knownBlocks.add(HarderOres.ModBlocks.ore_harddeepslate_copper);
			knownBlocks.add(HarderOres.ModBlocks.ore_harddeepslate_diamond);
			knownBlocks.add(HarderOres.ModBlocks.ore_harddeepslate_gold);
			knownBlocks.add(HarderOres.ModBlocks.ore_harddeepslate_iron);
			knownBlocks.add(HarderOres.ModBlocks.ore_limonite);
			knownBlocks.add(HarderOres.ModBlocks.machine_sifter);
			knownBlocks.add(HarderOres.ModBlocks.packager);
			knownBlocks.add(HarderOres.ModBlocks.machine_millstone);
			knownBlocks.add(HarderOres.ModBlocks.machine_axel);
			knownBlocks.add(HarderOres.ModBlocks.machine_windvane);
			knownBlocks.add(HarderOres.ModBlocks.sluice);
			OreFlowers.FLOWERS.values().forEach(flower -> knownBlocks.add(flower.get()));
			return knownBlocks;
		}

		@Override
		protected void generate() {
			dropSelf(ExpandedIndustry.ModBlocks.rail_bridge);
			dropSelf(ExpandedIndustry.ModBlocks.powered_rail_bridge);
			dropSelf(ExpandedIndustry.ModBlocks.machine_wood_hopper);
			dropSelf(ExpandedIndustry.ModBlocks.machine_distributor);
			
			dropOtherOrSilkTouch(HarderOres.ModBlocks.ore_limonite, HarderOres.ModItems.orechunk_limonite);
			dropOtherOrSilkTouch(HarderFarming.ModBlocks.ore_salt, HarderFarming.ModItems.salt_chunk);
			
			dropsOreChunks(HarderOres.ModBlocks.ore_hardcopper, Items.RAW_COPPER);
			dropsOreChunks(HarderOres.ModBlocks.ore_harddiamond, HarderOres.ModItems.orechunk_diamond);
			dropsOreChunks(HarderOres.ModBlocks.ore_hardgold, Items.RAW_GOLD);
			dropsOreChunks(HarderOres.ModBlocks.ore_hardiron, Items.RAW_IRON);
			dropsOreChunks(HarderOres.ModBlocks.ore_harddeepslate_copper, Items.RAW_COPPER);
			dropsOreChunks(HarderOres.ModBlocks.ore_harddeepslate_diamond, HarderOres.ModItems.orechunk_diamond);
			dropsOreChunks(HarderOres.ModBlocks.ore_harddeepslate_gold, Items.RAW_GOLD);
			dropsOreChunks(HarderOres.ModBlocks.ore_harddeepslate_iron, Items.RAW_IRON);
			
			dropSelf(HarderOres.ModBlocks.machine_sifter);
			dropSelf(HarderOres.ModBlocks.packager);
			dropSelf(HarderOres.ModBlocks.machine_millstone);
			dropSelf(HarderOres.ModBlocks.machine_axel);
			dropSelf(HarderOres.ModBlocks.machine_windvane);
			dropSelf(HarderOres.ModBlocks.sluice);
			
			LootItemCondition.Builder cropMinAge = MatchBlock.blockMatches(this.blocks, HarderFarming.ModBlocks.crop_winter_wheat, StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7));
			add(HarderFarming.ModBlocks.crop_winter_wheat, createCropDrops(HarderFarming.ModBlocks.crop_winter_wheat, Items.WHEAT, HarderFarming.ModItems.winter_wheat_seeds, cropMinAge));
			dropOther(HarderFarming.ModBlocks.crop_weeds, HarderFarming.ModItems.weed_seeds);
			dropSelf(HarderFarming.ModBlocks.tanner);

			for(DeferredHolder<Block, Block> flower : OreFlowers.FLOWERS.values()) {
				if(flower.get() instanceof DoublePlantBlock) {
					add(flower.get(), createSinglePropConditionTable(flower.get(), DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
				}
				else {
					dropSelf(flower.get());
				}
			}
			// Aveloz sap is an irritant: it only comes away whole if harvested carefully.
			LootItemCondition.Builder careful = AnyOfCondition.anyOf(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setCrouching(true))))
					.or(hasShears()).or(hasSilkTouch());
			LootItemCondition.Builder lowerHalf = MatchBlock.blockMatches(this.blocks, OreFlowers.ModBlocks.aveloz,
					StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
			add(OreFlowers.ModBlocks.aveloz, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
					.add(LootItem.lootTableItem(OreFlowers.ModBlocks.aveloz).when(careful).when(lowerHalf))));
		}

		private void dropOtherOrSilkTouch(Block block, ItemLike item) {
			add(block, createSelfDropDispatchTable(block, hasSilkTouch(), itemToBuilder(block, item)));
		}

		private Builder<?> itemToBuilder(Block block, ItemLike item) {
			return LootItem.lootTableItem(item).apply(applyExplosionCondition(block, SetItemCountFunction.setCount(ContextIntProviders.exactly(1))));
		}

		/** Silk touch takes the whole block, keeping its density on the item; otherwise it drops raw ore. */
		private void dropsOreChunks(Block block, Item chunk) {
			add(block, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).add(
					LootItem.lootTableItem(block).when(hasSilkTouch())
						.apply(CopyBlockState.copyState(block).copy(BlockProperties.ORE_DENSITY))
						.otherwise(oreToChunk(block, chunk, 6)))));
		}

		private LootPoolEntryContainer.Builder<?> oreToChunk(Block oreHardiron, Item orechunkIron, float divisor) {
			return LootItem.lootTableItem(orechunkIron).apply(applyExplosionCondition(orechunkIron, HarderSetCount.addHardOreCount(Holder.direct(new ConstantValue(divisor)))));
		}

		// (createSelfDropDispatchTable is BlockLootSubProvider's, now that conditions are holders.)
	}
}
