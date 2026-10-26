package com.draco18s.harderores.item;

import java.util.function.Consumer;


import com.draco18s.harderores.block.ore.HardOreBlock;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public class HardOreItem extends BlockItem {

	public HardOreItem(Block p_40565_, Properties p_40566_) {
		super(p_40565_, p_40566_);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		tooltip.accept(Component.translatable("harderores.ore_density", HardOreBlock.getDensity(stack)));
	}
}
