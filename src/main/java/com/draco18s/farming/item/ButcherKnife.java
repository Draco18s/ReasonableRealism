package com.draco18s.farming.item;

import java.util.List;

import com.draco18s.farming.HarderFarming;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

public class ButcherKnife extends DiggerItem {
	//private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.SLIME_BLOCK, Blocks.SPONGE, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.MELON});
	
	private final float attackDamage;

	public ButcherKnife(Tier material) {
		super(material, HarderFarming.Tags.Blocks.MINABLE_WITH_KNIFE, new Properties().durability(material.getUses()/2)
				.attributes(DiggerItem.createAttributes(material, (material.getAttackDamageBonus()/2)-1, -1)));
		attackDamage = (material.getAttackDamageBonus()/2)-1 + material.getAttackDamageBonus();
	}

	/** The knife's attack damage (the attribute value, before the player's base 1). */
	public float getAttackDamage() {
		return attackDamage;
	}

	/*@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if(EFFECTIVE_ON.contains(state.getBlock())) return 5;
		return super.getDestroySpeed(stack, state);
	}*/

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		tooltip.add(Component.translatable("tooltip.harderfarming.damagebonus", 10));
	}
}
