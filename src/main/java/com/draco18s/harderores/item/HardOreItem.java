package com.draco18s.harderores.item;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.harderores.block.ore.HardOreBlock;
import com.draco18s.hardlib.api.block.state.BlockProperties;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class HardOreItem extends BlockItem {

	public HardOreItem(Block p_40565_, Properties p_40566_) {
		super(p_40565_, p_40566_);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, world, tooltip, flag);
		CompoundTag compoundtag = stack.getTag();
		if(compoundtag == null) {
			compoundtag = stack.getOrCreateTag();
			HardOreBlock.setNbtOnStack(stack, BlockProperties.ORE_DENSITY, 16);
		}
		CompoundTag compoundtag1 = compoundtag.getCompound("BlockStateTag");
		String s1 = compoundtag1.getString(BlockProperties.ORE_DENSITY.getName());
		tooltip.add(Component.translatable("harderores.ore_density", "Ore Density %s".formatted(s1), new Object[] {s1}));
	}
}
