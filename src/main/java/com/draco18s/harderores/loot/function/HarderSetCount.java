package com.draco18s.harderores.loot.function;

import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IRandomRange;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.RandomRanges;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.registries.ForgeRegistries;

public class HarderSetCount extends LootFunction {
	private final IRandomRange countRange;

	private HarderSetCount(ILootCondition[] p_i51222_1_, IRandomRange p_i51222_2_) {
		super(p_i51222_1_);
		this.countRange = p_i51222_2_;
	}

	public ItemStack doApply(ItemStack stack, LootContext context) {
		BlockState state = context.get(LootParameters.BLOCK_STATE);
		ItemStack itemstack = context.get(LootParameters.TOOL);
		int fortune = 0;
		if (itemstack != null) {
			fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack);
		}
		if(state.has(BlockProperties.ORE_DENSITY)) {
			int density = state.get(BlockProperties.ORE_DENSITY);
			IRandomRange densityRange = new RandomValueRange(1,2+((density-1)/6)+fortune);
			stack.setCount(densityRange.generateInt(context.getRandom()));
			return stack;
		}
		stack.setCount(this.countRange.generateInt(context.getRandom()));
		return stack;
	}

	public static LootFunction.Builder<?> func_215932_a(IRandomRange p_215932_0_) {
		return builder((p_215934_1_) -> {
			return new HarderSetCount(p_215934_1_, p_215932_0_);
		});
	}

	public static class Serializer extends LootFunction.Serializer<HarderSetCount> {
		public Serializer() {
			super(new ResourceLocation("harderores:set_count"), HarderSetCount.class);
		}

		public void serialize(JsonObject object, HarderSetCount functionClazz, JsonSerializationContext serializationContext) {
			super.serialize(object, functionClazz, serializationContext);
			object.add("count", RandomRanges.serialize(functionClazz.countRange, serializationContext));
		}

		public HarderSetCount deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
			IRandomRange irandomrange = RandomRanges.deserialize(object.get("count"), deserializationContext);
			return new HarderSetCount(conditionsIn, irandomrange);
		}
	}
}