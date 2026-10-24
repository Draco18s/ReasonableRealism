package com.draco18s.farming.loot;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.draco18s.farming.FarmingConfig;
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

/**
 * Animals drop rawhide instead of leather: 3-5 of it, plus one more per level of Looting, in place of whatever
 * leather the loot table rolled. Leather comes from curing it on a tanning rack.
 */
public class RawLeatherModifier extends LootModifier {
	public static final Supplier<Codec<RawLeatherModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, RawLeatherModifier::new)));

	public RawLeatherModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		// Mob drops only; leather in chests or from fishing stays leather.
		if(!FarmingConfig.doRawLeather.get() || !context.getQueriedLootTableId().getPath().startsWith("entities/")) {
			return generatedLoot;
		}
		if(!generatedLoot.removeIf(st -> st.is(Items.LEATHER))) {
			return generatedLoot;
		}
		int count = 3 + context.getRandom().nextInt(3) + context.getRandom().nextInt(context.getLootingModifier() + 1);
		generatedLoot.add(new ItemStack(HarderFarming.ModItems.raw_leather, count));
		return generatedLoot;
	}
}
