package com.draco18s.farming.item;

import java.util.function.Consumer;

import com.draco18s.farming.HarderFarming;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class ButcherKnife extends Item {
	//private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.SLIME_BLOCK, Blocks.SPONGE, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.MELON});
	
	private final float attackDamage;

	public ButcherKnife(ToolMaterial material, Properties props) {
		super(props.tool(material, HarderFarming.Tags.Blocks.MINABLE_WITH_KNIFE, (material.attackDamageBonus()/2)-1, -1, 0)
				.durability(material.durability()/2));
		attackDamage = (material.attackDamageBonus()/2)-1 + material.attackDamageBonus();
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
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, display, tooltip, flagIn);
		tooltip.accept(Component.translatable("tooltip.harderfarming.damagebonus", 10));
	}
}
