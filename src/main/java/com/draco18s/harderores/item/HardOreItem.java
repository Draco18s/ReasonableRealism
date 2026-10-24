package com.draco18s.harderores.item;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.harderores.block.ore.HardOreBlock;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class HardOreItem extends BlockItem {

	public HardOreItem(Block p_40565_, Properties p_40566_) {
		super(p_40565_, p_40566_);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		tooltip.add(Component.translatable("harderores.ore_density", HardOreBlock.getDensity(stack)));
	}
}
