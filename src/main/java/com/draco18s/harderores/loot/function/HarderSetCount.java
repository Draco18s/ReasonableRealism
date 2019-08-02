package com.draco18s.harderores.loot.function;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IRandomRange;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class HarderSetCount extends LootFunction {
	float divisor = 1;

	private HarderSetCount(ILootCondition[] p_i51222_1_, float mod) {
		super(p_i51222_1_);
		divisor = mod;
	}

	public ItemStack doApply(ItemStack stack, LootContext context) {
		Entity harvester = context.get(LootParameters.THIS_ENTITY);
		BlockState state = context.get(LootParameters.BLOCK_STATE);
		ItemStack itemstack = context.get(LootParameters.TOOL);
		int fortune = 0;
		if (itemstack != null) {
			fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack);
		}
		if(state.has(BlockProperties.ORE_DENSITY)) {
			int density = state.get(BlockProperties.ORE_DENSITY);
			IRandomRange densityRange = new RandomValueRange(1,2+((density-1)/divisor)+fortune);
			stack.setCount(densityRange.generateInt(context.getRandom()));
			int pulverize = fortune = EnchantmentHelper.getEnchantmentLevel(HarderOres.ModEnchantments.pulverize, itemstack);
			stack = processPulverize(harvester, stack, pulverize);
			return stack;
		}
		stack.setCount(0);
		return stack;
	}

	private static ItemStack processPulverize(Entity harvester, ItemStack stack, int pulverize) {
		if(pulverize > 0) {
			ItemStack milled = HardLibAPI.oreMachines.getMillResult(stack).copy();
			if(!milled.isEmpty()) {
				milled.setCount(stack.getCount() * milled.getCount());
				return milled;
			}
		}
		return stack;
	}

	public static LootFunction.Builder<?> func_215932_a(float mod) {
		return builder((p_215934_1_) -> {
			return new HarderSetCount(p_215934_1_, mod);
		});
	}

	public static class Serializer extends LootFunction.Serializer<HarderSetCount> {
		public Serializer() {
			super(new ResourceLocation("harderores:set_count"), HarderSetCount.class);
		}

		public void serialize(JsonObject object, HarderSetCount functionClazz, JsonSerializationContext serializationContext) {
			super.serialize(object, functionClazz, serializationContext);
			object.addProperty("divisor", functionClazz.divisor);
		}

		public HarderSetCount deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
			float mod = object.get("divisor").getAsFloat();
			return new HarderSetCount(conditionsIn, mod);
		}
	}
}