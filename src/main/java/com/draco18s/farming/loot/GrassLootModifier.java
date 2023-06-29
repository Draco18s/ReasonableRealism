package com.draco18s.farming.loot;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.draco18s.farming.HarderFarming;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class GrassLootModifier extends LootModifier {
	public static final Supplier<Codec<GrassLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, GrassLootModifier::new)));

	public GrassLootModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.removeIf(st -> st.getItem() == Items.WHEAT_SEEDS);
		generatedLoot.add(new ItemStack(HarderFarming.ModItems.winter_wheat_seeds));
		return generatedLoot;
	}
}
