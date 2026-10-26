package com.draco18s.farming.item;

import java.util.function.Consumer;


import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/** The seeds plant the crop, but are named as an item. */
public class WinterSeedsItem extends BlockItem {

	public WinterSeedsItem(Block crop, Properties props) {
		super(crop, props.useItemDescriptionPrefix());
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		tooltip.accept(Component.translatable("tooltip.harderfarming.growsColdWeather"));
	}
}
