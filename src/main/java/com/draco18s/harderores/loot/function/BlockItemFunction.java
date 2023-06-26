/*package com.draco18s.harderores.loot.function;

import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockItemFunction extends LootFunction {

	protected BlockItemFunction(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	protected ItemStack doApply(ItemStack stack, LootContext context) {
		BlockState state = context.get(LootParameters.BLOCK_STATE);
		if(state.has(BlockProperties.ORE_DENSITY)) {
			int density = state.get(BlockProperties.ORE_DENSITY);
			ItemStack it = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(state.getBlock().getRegistryName().getNamespace(),state.getBlock().getRegistryName().getPath()+"_"+density)));
			it.getOrCreateTag().putInt("harderores:density", Math.max(0, density));
			return it;
		}
		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<BlockItemFunction> {

		public Serializer() {
			super(new ResourceLocation("harderores:blockitem"), BlockItemFunction.class);
		}

		@Override
		public BlockItemFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
			return new BlockItemFunction(conditionsIn);
		}
	}
}*/
