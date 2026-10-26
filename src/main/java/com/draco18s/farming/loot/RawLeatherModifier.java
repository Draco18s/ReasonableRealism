package com.draco18s.farming.loot;

import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.draco18s.farming.FarmingConfig;
import com.draco18s.farming.HarderFarming;
import com.draco18s.hardlib.HardLib;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Animals drop rawhide instead of leather: 3-5 of it, plus one more per level of Looting, in place of whatever
 * leather the loot table rolled. Leather comes from curing it on a tanning rack.
 */
public class RawLeatherModifier extends LootModifier {
	public static final Supplier<MapCodec<RawLeatherModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, RawLeatherModifier::new)));

	public RawLeatherModifier(Optional<Holder<LootItemCondition>> condition, int priority) {
		super(condition, priority);
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
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
		int looting = context.getOptional(LootContextParams.ATTACKING_ENTITY) instanceof LivingEntity killer
				? HardLib.getEnchantmentLevel(context.getLevel().registryAccess(), Enchantments.LOOTING, killer.getMainHandItem()) : 0;
		int count = 3 + context.getRandom().nextInt(3) + context.getRandom().nextInt(looting + 1);
		generatedLoot.add(new ItemStack(HarderFarming.ModItems.raw_leather, count));
		return generatedLoot;
	}
}
