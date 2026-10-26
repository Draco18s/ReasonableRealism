package com.draco18s.farming.loot;

import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.draco18s.farming.HarderFarming;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class GrassLootModifier extends LootModifier {
	public static final Supplier<MapCodec<GrassLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, GrassLootModifier::new)));

	public GrassLootModifier(Optional<Holder<LootItemCondition>> condition, int priority) {
		super(condition, priority);
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.removeIf(st -> st.getItem() == Items.WHEAT_SEEDS);
		generatedLoot.add(new ItemStack(HarderFarming.ModItems.winter_wheat_seeds));
		return generatedLoot;
	}
}
