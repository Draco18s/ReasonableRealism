package com.draco18s.farming.item;

import java.util.List;

import com.draco18s.farming.HarderFarming;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ButcherKnife extends DiggerItem {
	//private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.SLIME_BLOCK, Blocks.SPONGE, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.MELON});
	
	public ButcherKnife(Tier material) {
		super((material.getAttackDamageBonus()/2)-1, -1, material, HarderFarming.ModTags.MINABLE_WITH_KNIFE, new Properties().defaultDurability(material.getUses()/2));
	}

	/*@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if(EFFECTIVE_ON.contains(state.getBlock())) return 5;
		return super.getDestroySpeed(stack, state);
	}*/

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(Component.translatable("tooltip.harderfarming:damagebonus", 10));
	}
}
