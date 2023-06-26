package com.draco18s.harderores.loot.function;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;


public class HarderSetCount extends LootItemConditionalFunction {
	//float divisor = 1;
	NumberProvider modProv;
	
	protected HarderSetCount(LootItemCondition[] conditions, NumberProvider modProv) {
		super(conditions);
		this.modProv = modProv;
	}

	@Override
	public LootItemFunctionType getType() {
		return HarderOres.LootFunctions.harderSetCountReg.get();
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext context) {
		BlockState state = context.getParam(LootContextParams.BLOCK_STATE);
		ItemStack itemstack = context.getParam(LootContextParams.TOOL);
		int fortune = 0;
		if (itemstack != null) {
			fortune = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.BLOCK_FORTUNE, itemstack);
		}
		if(state.hasProperty(BlockProperties.ORE_DENSITY)) {
			int density = state.getValue(BlockProperties.ORE_DENSITY);
			stack.setCount(context.getRandom().nextIntBetweenInclusive(1, 2+(int)((density-1)/modProv.getFloat(context))+fortune));
			int pulverize = fortune = EnchantmentHelper.getTagEnchantmentLevel(HarderOres.ModEnchantments.pulverize, itemstack);
			stack = processPulverize(stack, pulverize);
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
	
	public static class Serializer extends LootItemConditionalFunction.Serializer<HarderSetCount> {
		@Override
		public void serialize(JsonObject object, HarderSetCount functionClazz, JsonSerializationContext serializationContext) {
			super.serialize(object, functionClazz, serializationContext);
			object.add("divisor", serializationContext.serialize(functionClazz.modProv));
		}
		
		@Override
		public HarderSetCount deserialize(JsonObject object, JsonDeserializationContext ctx, LootItemCondition[] conditionsIn) {
			NumberProvider mod = GsonHelper.getAsObject(object, "divisor", ctx, NumberProvider.class);
			return new HarderSetCount(conditionsIn, mod);
		}
	}
}