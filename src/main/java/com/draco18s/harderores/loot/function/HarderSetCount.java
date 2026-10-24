package com.draco18s.harderores.loot.function;

import java.util.List;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;


public class HarderSetCount extends LootItemConditionalFunction {
	//float divisor = 1;
	public static final MapCodec<HarderSetCount> CODEC = RecordCodecBuilder.mapCodec(inst -> commonFields(inst)
			.and(NumberProviders.CODEC.fieldOf("divisor").forGetter(f -> f.modProv))
			.apply(inst, HarderSetCount::new));
	NumberProvider modProv;
	
	protected HarderSetCount(List<LootItemCondition> conditions, NumberProvider modProv) {
		super(conditions);
		this.modProv = modProv;
	}

	@Override
	@SuppressWarnings("unchecked")
	public LootItemFunctionType<HarderSetCount> getType() {
		return (LootItemFunctionType<HarderSetCount>)HarderOres.LootFunctions.harderSetCountReg.get();
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext context) {
		BlockState state = context.getParam(LootContextParams.BLOCK_STATE);
		ItemStack itemstack = context.getParam(LootContextParams.TOOL);
		int fortune = 0;
		if (itemstack != null) {
			fortune = HardLib.getEnchantmentLevel(context.getLevel().registryAccess(), Enchantments.FORTUNE, itemstack);
		}
		if(state.hasProperty(BlockProperties.ORE_DENSITY)) {
			int density = state.getValue(BlockProperties.ORE_DENSITY);
			stack.setCount(context.getRandom().nextIntBetweenInclusive(1, 2+(int)((density-1)/modProv.getFloat(context))+fortune));
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

	public static LootItemConditionalFunction.Builder<?> addHardOreCount(NumberProvider mod) {
		return simpleBuilder((p_215934_1_) -> {
			return new HarderSetCount(p_215934_1_, mod);
		});
	}
}
