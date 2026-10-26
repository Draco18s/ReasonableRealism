package com.draco18s.harderores.loot.function;

import java.util.Optional;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;


public class HarderSetCount extends LootItemConditionalFunction {
	//float divisor = 1;
	public static final MapCodec<HarderSetCount> CODEC = RecordCodecBuilder.mapCodec(inst -> commonFields(inst)
			.and(ContextFloatProviders.CODEC.fieldOf("divisor").forGetter(f -> f.modProv))
			.apply(inst, HarderSetCount::new));
	Holder<ContextFloatProvider> modProv;

	protected HarderSetCount(Optional<Holder<LootItemCondition>> condition, Holder<ContextFloatProvider> modProv) {
		super(condition);
		this.modProv = modProv;
	}

	/** Registered as harderores:set_count (loot function types are their codecs now). */
	@Override
	public MapCodec<HarderSetCount> codec() {
		return CODEC;
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext context) {
		BlockState state = context.getOptional(LootContextParams.BLOCK_STATE);
		ItemInstance tool = context.getOptional(LootContextParams.TOOL);
		ItemStack itemstack = tool instanceof ItemStack stack2 ? stack2 : null;
		int fortune = 0;
		if (itemstack != null) {
			fortune = HardLib.getEnchantmentLevel(context.getLevel().registryAccess(), Enchantments.FORTUNE, itemstack);
		}
		if(state != null && state.hasProperty(BlockProperties.ORE_DENSITY)) {
			int density = state.getValue(BlockProperties.ORE_DENSITY);
			stack.setCount(context.getRandom().nextIntBetweenInclusive(1, 2+(int)((density-1)/modProv.value().getFloat(context))+fortune));
			int pulverize = itemstack == null ? 0 : HardLib.getEnchantmentLevel(context.getLevel().registryAccess(), HarderOres.ModEnchantments.pulverize, itemstack);
			// Pulverize only grinds what is left of a nearly mined-out block.
			if(pulverize > 0 && density <= pulverize * 2 + 3) {
				stack = processPulverize(stack, pulverize);
			}
			return stack;
		}
		return null;
	}

	private static ItemStack processPulverize(ItemStack stack, int pulverize) {
		if(pulverize > 0) {
			ItemStack milled = HardLibAPI.oreMachines.getMillResult(stack).copy();
			if(!milled.isEmpty()) {
				milled.setCount(stack.getCount() * milled.getCount());
				return milled;
			}
		}
		return stack;
	}

	public static LootItemConditionalFunction.Builder<?> addHardOreCount(Holder<ContextFloatProvider> mod) {
		return simpleBuilder((p_215934_1_) -> {
			return new HarderSetCount(p_215934_1_, mod);
		});
	}
}
